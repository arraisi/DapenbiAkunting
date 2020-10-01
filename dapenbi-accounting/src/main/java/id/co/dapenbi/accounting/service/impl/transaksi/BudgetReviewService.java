package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.BudgetReviewDao;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewLog;
import id.co.dapenbi.accounting.mapper.BudgetReviewMapper;
import id.co.dapenbi.accounting.repository.transaksi.BudgetReviewDetailRepository;
import id.co.dapenbi.accounting.repository.transaksi.BudgetReviewRepository;
import id.co.dapenbi.accounting.service.impl.NumberGeneratorService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BudgetReviewService {

    @Autowired
    private BudgetReviewDao dao;

    @Autowired
    private BudgetReviewRepository repository;

    @Autowired
    private BudgetReviewDetailRepository budgetReviewDetailRepository;

    @Autowired
    private BudgetReviewLogService budgetReviewLogService;

    @Autowired
    private BudgetReviewMapper budgetReviewMapper;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BudgetReview save(BudgetReview value, Principal principal) {
        value.setCreatedBy(principal.getName());
        return repository.save(value);
    }

    @Transactional
    public int merge(BudgetReview value) {
        return dao.merge(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BudgetReview update(BudgetReview value, Principal principal) {
        value.setUpdatedBy(principal.getName());
        value.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(value);
    }

    public Optional<BudgetReview> findById(String id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id, Principal principal) {
        budgetReviewDetailRepository.deleteByNoBudgetReview(id);
        repository.deleteById(id);
    }

    public DataTablesResponse<BudgetReviewDTO> datatables(DataTablesRequest<BudgetReviewDTO> params, String search) {
        List<BudgetReviewDTO> data = dao.findForDataTableBudgetReviewDTO(params, search);
        Long rowCount = dao.findForDataTableBudgetReviewDTO(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BudgetReview validasiBudgetReview(BudgetReviewDTO budgetReview, Principal principal) throws Exception {
        // WARKAT LOG
        BudgetReviewLog budgetReviewLog = new BudgetReviewLog();

        // VALIDATION PROCESS
        budgetReviewLog.setAktivitas(budgetReview.getStatusData().equals("VALID") ? "VALIDASI" : "REJECT");

        budgetReview.setTglValidasi(Timestamp.valueOf(LocalDateTime.now()));
        budgetReview.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        budgetReview.setUserValidasi(principal.getName());
        budgetReview.setUpdatedBy(principal.getName());
        BudgetReview _budgetReview = BudgetReviewMapper.INSTANCE.dtoToBudgetReview(budgetReview);
        BudgetReview save = repository.save(_budgetReview);

        budgetReviewLog.setNoBudgetReview(_budgetReview.getNoBudgetReview());
        budgetReviewLog.setKeterangan(_budgetReview.getCatatanValidasi());
        budgetReviewLog.setStatusData(_budgetReview.getStatusData());
        budgetReviewLog.setCreatedBy(principal.getName());
        budgetReviewLog.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        BudgetReviewLog log = budgetReviewLogService.save(budgetReviewLog);

        return save;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BudgetReviewDTO saveBudgetAndDetails(BudgetReviewDTO request, Principal principal) {
        BudgetReview budgetReview = budgetReviewMapper.dtoToBudgetReview(request);
        String noBudgeReviewBefore;
        try {
            noBudgeReviewBefore = findByTahunBukuAndPeriode(budgetReview.getKodeThnBuku(), budgetReview.getKodePeriode());
            budgetReview.setNoBudgetReview(noBudgeReviewBefore);
        } catch (IncorrectResultSizeDataAccessException e){
            noBudgeReviewBefore = "";
            NumberGenerator acc_budgetreview_dtl_seq = numberGeneratorService.findByName("ACC_BUDGETREVIEW_DTL_SEQ");
            budgetReview.setNoBudgetReview(budgetReview.getNoBudgetReview() + acc_budgetreview_dtl_seq.getGenerateNumber());
        }

        if (budgetReview.getCreatedBy() == null) {
            budgetReview.setCreatedBy(principal.getName());
//                save(budgetReview, principal);
        } else {
            budgetReview.setUpdatedBy(principal.getName());
            budgetReview.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
//                update(budgetReview, principal);
        }


        final int mergeResponse = dao.merge(budgetReview);

        if (mergeResponse > 0 && request.getBudgetReviewDetails().size() > 0) {
            final List<BudgetReviewDetailDTO> collect = request.getBudgetReviewDetails().stream().map(value -> {
                if (value.getCreatedBy() == null && value.getCreatedDate() == null) {
                    value.setCreatedBy(principal.getName());
                    value.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                } else {
                    value.setUpdatedBy(principal.getName());
                    value.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
                }
                return value;
            }).collect(Collectors.toList());

            List<BudgetReviewDetailDTO> details = collect;
            final long totalNewDetail = details.stream().filter(i -> i.getIdBudgetReviewDtl() == null).count();

            if (!noBudgeReviewBefore.isEmpty() && totalNewDetail == details.size()) budgetReviewDetailRepository.deleteByNoBudgetReview(noBudgeReviewBefore);
            for (BudgetReviewDetailDTO detail : details) {
                BudgetReviewDetail budgetReviewDetail = budgetReviewMapper.dtoToBudgetReviewDetail(detail);
                budgetReviewDetail.setNoBudgetReview(budgetReview.getNoBudgetReview());
                budgetReviewDetailRepository.save(budgetReviewDetail);
            }
        }

        return request;
    }

    public String findByTahunBukuAndPeriode(String kodeTahunBuku, String kodePeriode) {
        return dao.findByTahunBukuAndPeriode(kodeTahunBuku, kodePeriode);
    }

    public List<BudgetReviewDTO.DaftarRekeningDTO> getDaftarRekening(BudgetReviewDTO request) throws IncorrectResultSizeDataAccessException {
        return dao.getDaftarRekening(request);
    }

    public String generateNumberBudgetReview() {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        NumberGenerator numberGeneratorBR = numberGeneratorService.findByName("ACC_BUDGETREVIEW");
        StringBuilder numberBugetReview = new StringBuilder();
        int numberLength = numberGeneratorBR.getGenerateNumber().toString().length();
        numberBugetReview.append(pengaturanSistem.get().getKodeTahunBuku())
                .append(pengaturanSistem.get().getKodePeriode())
                .append(numberLength <= 6 ? String.format("%06d", numberGeneratorBR.getGenerateNumber()) : numberGeneratorBR.getGenerateNumber());
        return numberBugetReview.toString();
    }

    public List<BudgetReviewDetailDTO> findDetails(String noBudgetReview) {
        return dao.findDetails(noBudgetReview);
    }
}

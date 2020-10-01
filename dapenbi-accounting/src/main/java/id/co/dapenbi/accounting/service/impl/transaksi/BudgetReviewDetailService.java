package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.BudgetReviewDetailDao;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import id.co.dapenbi.accounting.repository.transaksi.BudgetReviewDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetReviewDetailService {

    @Autowired
    private BudgetReviewDetailDao dao;

    @Autowired
    private BudgetReviewDetailRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BudgetReviewDetail save(BudgetReviewDetail value, Principal principal) {
        value.setCreatedBy(principal.getName());
        value.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(value);
    }

    public Optional<BudgetReviewDetail> findById(Integer id) {
        return repository.findById(id);
    }

    public List<BudgetReviewDetail> findByNoBudgetReview(String id) {
        return repository.findByNoBudgetReview(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public DataTablesResponse<BudgetReviewDetailDTO> datatables(DataTablesRequest<BudgetReviewDetailDTO> params, String search) {
        List<BudgetReviewDetailDTO> data = dao.dataTableBudgetReviewDetailDTO(params, search);
        Long rowCount = dao.dataTableBudgetReviewDetailDTO(search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }
}

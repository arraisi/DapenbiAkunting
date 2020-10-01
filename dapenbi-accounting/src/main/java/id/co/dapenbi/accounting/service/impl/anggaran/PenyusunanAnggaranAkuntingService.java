package id.co.dapenbi.accounting.service.impl.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.anggaran.PenyusunanAnggaranAkuntingDao;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDTO;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDetailDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkuntingDetail;
import id.co.dapenbi.accounting.entity.laporan.ojk.*;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.enums.NamaLaporan;
import id.co.dapenbi.accounting.mapper.PenyusunanAnggaranAkuntingMapper;
import id.co.dapenbi.accounting.repository.anggaran.PenyusunanAnggaranAkuntingDetailRepository;
import id.co.dapenbi.accounting.repository.anggaran.PenyusunanAnggaranAkuntingRepository;
import id.co.dapenbi.accounting.repository.parameter.PeriodeRepository;
import id.co.dapenbi.accounting.repository.parameter.TahunBukuRepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoCurrentRepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoFARepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoPARepository;
import id.co.dapenbi.accounting.service.impl.NumberGeneratorService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PenyusunanAnggaranAkuntingService {

    @Autowired
    private PenyusunanAnggaranAkuntingDao dao;

    @Autowired
    private PenyusunanAnggaranAkuntingRepository repository;

    @Autowired
    private PenyusunanAnggaranAkuntingDetailRepository detailRepository;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private PenyusunanAnggaranAkuntingMapper penyusunanAnggaranAkuntingMapper;

    @Autowired
    private PeriodeRepository periodeRepository;

    @Autowired
    private TahunBukuRepository tahunBukuRepository;

    @Autowired
    private SaldoCurrentRepository saldoCurrentRepository;

    @Autowired
    private SaldoFARepository saldoFARepository;

    @Autowired
    private SaldoPARepository saldoPARepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PenyusunanAnggaranAkunting save(PenyusunanAnggaranAkunting value, Principal principal) {
        value.setCreatedBy(principal.getName());
        return repository.save(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int merge(PenyusunanAnggaranAkunting value) {
        return dao.merge(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PenyusunanAnggaranAkunting update(PenyusunanAnggaranAkunting value, Principal principal) {
        value.setUpdatedBy(principal.getName());
        value.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(value);
    }

    public Optional<PenyusunanAnggaranAkunting> findById(String id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id, Principal principal) {
        detailRepository.deleteByNoAnggaran(id);
        repository.deleteById(id);
    }

    public DataTablesResponse<PenyusunanAnggaranAkuntingDTO> datatables(DataTablesRequest<PenyusunanAnggaranAkuntingDTO> params, String search) {
        List<PenyusunanAnggaranAkuntingDTO> data = dao.findForDataTableBudgetReviewDTO(params, search);
        Long rowCount = dao.findForDataTableBudgetReviewDTO(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> getDaftarRekening(String kodeTahunBuku) throws IncorrectResultSizeDataAccessException {
        return dao.getDaftarRekening(kodeTahunBuku);
    }

    public List<PenyusunanAnggaranAkuntingDetailDTO> findDetails(String noAnggaran) {
        return dao.findDetails(noAnggaran);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PenyusunanAnggaranAkuntingDTO saveHeaderAndDetails(PenyusunanAnggaranAkuntingDTO request, Principal principal) {
        log.info("masuk service");
        PenyusunanAnggaranAkunting latestData = getLatestData(request.getKodeThnBuku().getKodeTahunBuku(), "save");
        if (latestData == null){
            request.setVersi("1");
        } else {
            if (latestData.getStatusData().equals("SUBMIT")) {
                return null;
            } else if (latestData.getStatusData().equals("APPROVE")) {
                request.setVersi(String.valueOf(Integer.parseInt(latestData.getVersi()) + 1));
            } else {
                request.setVersi(latestData.getVersi());
            }
        }

        PenyusunanAnggaranAkunting penyusunanAnggaranAkunting = penyusunanAnggaranAkuntingMapper.dtoToEntity(request);
        if (penyusunanAnggaranAkunting.getNoAnggaran() == null) {
            NumberGenerator acc_at_dtl_seq = numberGeneratorService.findByName("ACC_AT_DTL_SEQ");
            penyusunanAnggaranAkunting.setNoAnggaran(penyusunanAnggaranAkunting.getNoAnggaran() + acc_at_dtl_seq.getGenerateNumber());
        }

        log.info("masuk edit entity hdr");
        penyusunanAnggaranAkunting.setCreatedBy(principal.getName());
        penyusunanAnggaranAkunting.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        penyusunanAnggaranAkunting.setStatusAktif("0");

        repository.save(penyusunanAnggaranAkunting);
        log.info("masuk save hdr");

        Stream<PenyusunanAnggaranAkuntingDetailDTO> penyusunanAnggaranAkuntingDetailDTOStream =
                request.getPenyusunanAnggaranAkuntingDetail().stream().map(penyusunanAnggaranAkuntingDetailDTO -> {
            penyusunanAnggaranAkuntingDetailDTO.setCreatedBy(principal.getName());
            penyusunanAnggaranAkuntingDetailDTO.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            penyusunanAnggaranAkuntingDetailDTO.setNoAnggaran(penyusunanAnggaranAkunting);
            penyusunanAnggaranAkuntingDetailDTO.setStatusData(penyusunanAnggaranAkunting.getStatusData());
            penyusunanAnggaranAkuntingDetailDTO.setVersi(penyusunanAnggaranAkunting.getVersi());
            penyusunanAnggaranAkuntingDetailDTO.setKodeThnBuku(penyusunanAnggaranAkunting.getKodeThnBuku().getKodeTahunBuku());
            penyusunanAnggaranAkuntingDetailDTO.setKodePeriode(penyusunanAnggaranAkunting.getKodePeriode().getKodePeriode());
            return penyusunanAnggaranAkuntingDetailDTO;
        });

        if (request.getPenyusunanAnggaranAkuntingDetail().size() > 0) {
            List<PenyusunanAnggaranAkuntingDetailDTO> penyusunanAnggaranAkuntingDetailDTOS = penyusunanAnggaranAkuntingDetailDTOStream.collect(Collectors.toList());
            for (PenyusunanAnggaranAkuntingDetailDTO _details : penyusunanAnggaranAkuntingDetailDTOS) {
                PenyusunanAnggaranAkuntingDetail penyusunanAnggaranAkuntingDetail = penyusunanAnggaranAkuntingMapper.dtoToEntityDetail(_details);
                penyusunanAnggaranAkuntingDetail.setStatusAktif("0");
                detailRepository.save(penyusunanAnggaranAkuntingDetail);
            }
        }

        return request;
    }

    public String generateNumberAnggaran(String kodeTahunBuku, String kodePeriode) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        NumberGenerator numberGeneratorBR = numberGeneratorService.findByName("ACC_ANGGARAN_NO_AT");
        StringBuilder numberAnggaran = new StringBuilder();
        int numberLength = numberGeneratorBR.getGenerateNumber().toString().length();
        numberAnggaran.append(kodeTahunBuku)
                .append(kodePeriode)
                .append(numberLength <= 6 ? String.format("%06d", numberGeneratorBR.getGenerateNumber()) : numberGeneratorBR.getGenerateNumber());
        return numberAnggaran.toString();
    }

    @Transactional
    public String getVersion(String kodeThnBuku) {
        List<PenyusunanAnggaranAkunting> anggaranList = repository.listByTahunBukuPeriode(kodeThnBuku);
        Integer counter = !anggaranList.isEmpty() ? Integer.parseInt(anggaranList.get(0).getVersi()) : 1;
        String version = counter.toString();
        return version;
    }

    @Transactional
    public String getNoAnggaran(String kodeThnBuku) {
        List<PenyusunanAnggaranAkunting> anggaranList = repository.listByTahunBukuPeriode(kodeThnBuku);
        String noAnggaran = !anggaranList.isEmpty() ? anggaranList.get(0).getNoAnggaran() : null;
        return noAnggaran;
    }

    @Transactional
    public PenyusunanAnggaranAkunting getLatestData(String kodeThnBuku, String condition) {
        List<PenyusunanAnggaranAkunting> anggaranList = repository.listByTahunBukuPeriode(kodeThnBuku);
        PenyusunanAnggaranAkunting penyusunanAnggaranAkunting = new PenyusunanAnggaranAkunting();
        if (condition.equals("save")){
            penyusunanAnggaranAkunting = !anggaranList.isEmpty() ? anggaranList.get(0) : null;
        } else{
            penyusunanAnggaranAkunting = !anggaranList.isEmpty() ? anggaranList.get(1) : null;
        }
        return penyusunanAnggaranAkunting;
    }

    public List<Periode> findAllPeriode() {
        return IterableUtils.toList(periodeRepository.findAll());
    }

    public List<TahunBuku> listByStatusAktif() {
        return tahunBukuRepository.listByStatusAktif();
    }

    @Transactional
    public PenyusunanAnggaranAkunting updateStatusData(String noAnggaran, String statusData, String user) {
        String statusAktif = "1";
        if (statusData.equals("REJECT")){
            statusAktif = "0";
        }

        Optional<PenyusunanAnggaranAkunting> penyusunanAnggaranAkuntingOptional = findById(noAnggaran);
        log.info("optional ", penyusunanAnggaranAkuntingOptional);
        PenyusunanAnggaranAkunting request = penyusunanAnggaranAkuntingOptional.get();
        PenyusunanAnggaranAkunting latestData = getLatestData(request.getKodeThnBuku().getKodeTahunBuku(), "update");
        log.info("last data {}", latestData);
        if (latestData != null){
            log.info("no anggaran last data {}", latestData.getNoAnggaran());
            repository.setStatusAktif("0", latestData.getNoAnggaran());
            detailRepository.setStatusAktif("0", latestData.getNoAnggaran());
        }
        repository.setStatusData(noAnggaran, statusData, user, Timestamp.valueOf(LocalDateTime.now()), user, Timestamp.valueOf(LocalDateTime.now()), statusAktif);
        detailRepository.setStatusData(noAnggaran, statusData, user, Timestamp.valueOf(LocalDateTime.now()), user, Timestamp.valueOf(LocalDateTime.now()), statusAktif);
        return null;
    }

    public DataTablesOutput<PenyusunanAnggaranAkunting> validasiDataTable(DataTablesInput input) {
        Specification<PenyusunanAnggaranAkunting> anggaranSpecification1 = this.byStatus("SUBMIT");
        return repository.findAll(input, Specification.where(anggaranSpecification1));
    }

    private Specification<PenyusunanAnggaranAkunting> byStatus(String status) {
        return (book, cq, cb) -> cb.equal(book.get("statusData"), status);
    }

    public DataTablesOutput<PenyusunanAnggaranAkunting> dataTable(DataTablesInput input) {
        return repository.findAll(input);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateSaldoCurrentPAFA(List<PenyusunanAnggaranAkuntingDetailDTO> list) {

        if (list.size() > 0) {
            for (PenyusunanAnggaranAkuntingDetailDTO _details : list) {
                if(_details.getTotalAnggaran().compareTo(BigDecimal.ZERO) != 0){
//                    if (_details.getIdRekening().getTipeRekening().equals("PENDAPATAN")) {
                        saldoCurrentRepository.updateAnggaranPendapatan(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
                        saldoPARepository.updateAnggaranPendapatan(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
                        saldoFARepository.updateAnggaranPendapatan(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
//                    } else {
//                        saldoCurrentRepository.updateAnggaranBiaya(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
//                        saldoPARepository.updateAnggaranBiaya(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
//                        saldoFARepository.updateAnggaranBiaya(_details.getIdRekening().getIdRekening(), _details.getTotalAnggaran());
//                    }
                }
            }
        }
    }

    public byte[] exportPdf(Map<String, Object> parameter, String noAnggaran, String tipeRekening) {
        try {
            final JRBeanCollectionDataSource jrBeanCollectionDataSource;
            final List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> exportData = dao.getDataForExport(noAnggaran, tipeRekening);
            jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);

            String jasperName = "/jasper/laporan_anggaran_tahunan.jasper";
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    jrBeanCollectionDataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(Map<String, Object> parameter, String noAnggaran, String tipeRekening) {
        try {

            final JRBeanCollectionDataSource jrBeanCollectionDataSource;
            final List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> exportData = dao.getDataForExport(noAnggaran, tipeRekening);
            jrBeanCollectionDataSource = new JRBeanCollectionDataSource(exportData);

            String jasperName = "/jasper/laporan_anggaran_tahunan.jasper";
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);

            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameter,
                    jrBeanCollectionDataSource);

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {
                Exporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                return byteArrayOutputStream.toByteArray();
            }
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.WarkatDao;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.laporan.PengantarWarkatDTO;
import id.co.dapenbi.accounting.dto.transaksi.ValidasiWarkatDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.master.LookupMaster;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.mapper.WarkatMapper;
import id.co.dapenbi.accounting.repository.master.LookupMasterRepository;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import id.co.dapenbi.accounting.repository.transaksi.WarkatLogRepository;
import id.co.dapenbi.accounting.repository.transaksi.WarkatRepository;
import id.co.dapenbi.accounting.service.impl.NumberGeneratorService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WarkatService {

    @Autowired
    private WarkatRepository repository;

    @Autowired
    private WarkatDao dao;

    @Autowired
    private WarkatJurnalService warkatJurnalService;

    @Autowired
    private WarkatLogRepository warkatLogRepository;

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private NumberGeneratorService numberGeneratorService;

    @Autowired
    private WarkatMapper warkatMapper;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private LookupMasterRepository lookupMasterRepository;

    public Iterable<Warkat> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat save(Warkat warkat) {
        return repository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat update(PengaturanSistem pengaturanSistem, WarkatDTO request, String user) throws Exception {
        TahunBuku tahunBuku = new TahunBuku(pengaturanSistem.getKodeTahunBuku());
        Periode periode = new Periode(pengaturanSistem.getKodePeriode());
        request.setTahunBuku(tahunBuku);
        request.setKodePeriode(periode);
        request.setArusKas("1");
        if (request.getJenisWarkat() == null || request.getJenisWarkat().isEmpty()) {
            request.setJenisWarkat("WARKAT");
        }
        if (request.getNoWarkat() == null || request.getNoWarkat().isEmpty()) {
            String satker = user.equalsIgnoreCase("PTR") ? request.getKodeOrg() : SessionUtil.getSession("satker");
            String noWarkat = getNoWarkat(satker);
            request.setNoWarkat(noWarkat);
        }
        request.setCreatedBy(user);

        // DELETE CURRENT JURNAL
        warkatJurnalService.deleteByNoWarkat(request.getNoWarkat());

        // SAVE WARKAT
        Warkat warkat = saveWarkatNJurnals(request, user);

        // SAVE LOG
        saveLog(warkat, request.getCreatedBy() != null ? "UPDATE" : "CREATE");
        return warkat;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Warkat saveWarkatNJurnals(WarkatDTO request, String user) {
        // Warkat
        Warkat warkat = warkatMapper.warkatDtoToWarkat(request);
        if (request.getCreatedBy() == null || request.getCreatedDate() == null) {
            warkat.setCreatedBy(user);
            warkat.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            warkat.setUpdatedBy(user);
            warkat.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        Warkat warkatResponse = repository.save(warkat);

        // warkat jurnal
        Stream<WarkatJurnal> warkatJurnalStream = request.getWarkatJurnals().stream().map(jurnal -> {
            jurnal.setNoWarkat(warkatResponse);
            jurnal.setCreatedBy(user);
            jurnal.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            return jurnal;
        });

        List<WarkatJurnal> collect = warkatJurnalStream.collect(Collectors.toList());
        warkatJurnalService.saveAll(collect);

        return warkatResponse;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveLog(Warkat warkat, String aktifitas) {
        WarkatLog warkatLog = new WarkatLog();
        warkatLog.setAktivitas(aktifitas);
        warkatLog.setStatusData(warkat.getStatusData());
        warkatLog.setNoWarkat(warkat.getNoWarkat());
        warkatLog.setKeterangan(warkat.getCatatanValidasi());
        warkatLog.setTotalTransaksi(warkat.getTotalTransaksi());
        warkatLog.setCreatedBy(warkat.getUpdatedBy() == null ? warkat.getCreatedBy() : warkat.getUpdatedBy());
        warkatLog.setUpdatedBy(warkat.getCreatedBy());
        warkatLog.setCreatedDate(warkat.getUpdatedDate() == null ? warkat.getCreatedDate() : warkat.getCreatedDate());
        warkatLog.setUpdatedDate(warkat.getCreatedDate());
        WarkatLog save = warkatLogRepository.save(warkatLog);
        log.info("{}", save);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String noWarkat, Principal principal) {
        // WARKAT JURNAL
        warkatJurnalService.deleteByIdWarkat(noWarkat);
        // WARKAT
        repository.deleteByNoWarkat(noWarkat);
        // WARKAT LOG
        Warkat warkat = new Warkat();
        warkat.setNoWarkat(noWarkat);
        warkat.setCreatedBy(principal.getName());
        warkat.setStatusData("DELETED");
        saveLog(warkat, "DELETE");
    }

    public Optional<Warkat> findById(String id) {
        return this.repository.findById(id);
    }

    @Transactional
    public DataTablesOutput<Warkat> findForDataTable(DataTablesInput input) {
        return this.repository.findAll(input);
    }

    public Iterable<Warkat> findAll() {
        return repository.findAll();
    }

    @Transactional
    public String validasiJurnal(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoCurrent = dao.findSaldoCurrentByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoCurrent.isEmpty()) {
            for (ValidasiWarkatDTO _saldoCurrent : saldoCurrent) {
                _saldoCurrent.setCreatedBy(warkatDTO.getCreatedBy());
                // SEMENTARA TIDAK DIGUNAKAN
//                ValidasiWarkatDTO calculatedSaldoValidasi = getCalculatedSaldoValidasi(_saldoCurrent);
//                dao.updateSaldoCurrent(calculatedSaldoValidasi, null);
//                dao.updateSaldoPA(calculatedSaldoValidasi, null);
//                dao.updateSaldoFA(calculatedSaldoValidasi, null);
            }
        }
        try {
            updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            log.info("{}", e.getMessage());
            return e.getMessage();
        }
    }

    @Transactional
    public String validasi(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoCurrent = dao.findSaldoCurrentByNoWarkat(warkatDTO.getNoWarkat());
        final Integer duplicate = dao.checkDuplicateJurnalByNoWarkat(warkatDTO.getNoWarkat());
        if (duplicate > 0) {
            return "Terdapat Jurnal yang sama.";
        } else if (!saldoCurrent.isEmpty()) {

            // CHECK SALDO
            final String respCheckSaldo = checkSaldoAnggaran(saldoCurrent);
            // CALCULATE AND UPDATE SALDO
            if (respCheckSaldo.equals("OK")) {
                // CALCULATED
                List<ValidasiWarkatDTO> saldoCalculateds = calculateValidasiSaldo(warkatDTO, saldoCurrent);
                // UPDATE SALDO CURRENT
                String kodeDRI = warkatDTO.getJenisWarkat().equalsIgnoreCase("PAJAK") ? "2" : "1";
                saldoCalculateds.forEach(value -> {
                    dao.updateSaldoCurrent(value, kodeDRI);
                });
            } else return respCheckSaldo;

            try {
                updateStatusWarkat(warkatDTO, aktivitas);
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }

        } else return "Data saldo current tidak ditemukan.";
    }

    @Transactional
    public String validasiWithoutUpdateSaldo(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoCurrent = dao.findSaldoCurrentByNoWarkat(warkatDTO.getNoWarkat());
        final Integer duplicate = dao.checkDuplicateJurnalByNoWarkat(warkatDTO.getNoWarkat());
        if (duplicate > 0) {
            return "Terdapat Jurnal yang sama.";
        } else if (!saldoCurrent.isEmpty()) {

            // CHECK SALDO
            final String respCheckSaldo = checkSaldoAnggaran(saldoCurrent);
            // CALCULATE AND UPDATE SALDO
            if (!respCheckSaldo.equals("OK")) {
                return respCheckSaldo;
            }

            try {
                updateStatusWarkat(warkatDTO, aktivitas);
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }

        } else return "Data saldo current tidak ditemukan.";
    }

    @Transactional
    public String validasiPreApproval(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoPA = dao.findSaldoPAByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoPA.isEmpty()) {

            // CHECK SALDO
            final String respCheckSaldo = checkSaldo(saldoPA);
            // CALCULATE AND UPDATE SALDO
            if (respCheckSaldo.equalsIgnoreCase("OK")) {
                // CALCULATED
                List<ValidasiWarkatDTO> saldoCalculateds = calculateValidasiSaldo(warkatDTO, saldoPA);
                // UPDATE SALDO CURRENT
                saldoCalculateds.forEach(value -> {
                    dao.updateSaldoPA(value, null);
                });
            } else return respCheckSaldo;

            try {
                updateStatusWarkat(warkatDTO, aktivitas);
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }

        } else return "Data saldo jurnal tidak ditemukan.";
    }

    @Transactional
    public String validasiPreApprovalWithoutUpdateSaldo(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoPA = dao.findSaldoPAByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoPA.isEmpty()) {

            // CHECK SALDO
            final String respCheckSaldo = checkSaldo(saldoPA);
            // CALCULATE AND UPDATE SALDO
            if (!respCheckSaldo.equalsIgnoreCase("OK")) {
                return respCheckSaldo;
            }

            try {
                updateStatusWarkat(warkatDTO, aktivitas);
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }

        } else return "Data saldo jurnal tidak ditemukan.";
    }

    @Transactional
    public String validasiFinalApproval(WarkatDTO warkatDTO, String aktivitas, String nuwp) {
        List<ValidasiWarkatDTO> saldoFA = dao.findSaldoFAByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoFA.isEmpty()) {

            // CHECK SALDO
            final String respCheckSaldo = checkSaldo(saldoFA);
            // CALCULATE AND UPDATE SALDO
            if (respCheckSaldo.equalsIgnoreCase("OK")) {
                // CALCULATED
                List<ValidasiWarkatDTO> saldoCalculateds = calculateValidasiSaldo(warkatDTO, saldoFA);
                // UPDATE SALDO CURRENT
                saldoCalculateds.forEach(value -> {
                    dao.updateSaldoFA(value, null);
                    dao.updateRealisasiBerjalanAnggaran(value);
                });
            } else return respCheckSaldo;

            try {
                warkatDTO.setNuwp(nuwp);
                updateStatusWarkat(warkatDTO, aktivitas);
                return "OK";
            } catch (Exception e) {
                return e.getMessage();
            }

        } else return "Data saldo jurnal tidak ditemukan.";
    }

    @Transactional
    public String rejectPreApproval(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoCurrent = dao.findSaldoCurrentByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoCurrent.isEmpty()) {
            final List<ValidasiWarkatDTO> saldoCalculateds = calculateRejectSaldo(warkatDTO, saldoCurrent);
            saldoCalculateds.forEach(value -> {
                dao.updateSaldoCurrent(value, null);
            });
        } else return "Data saldo tidak ditemukan";

        try {
            updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String rejectFinalApproval(WarkatDTO warkatDTO, String aktivitas) {
        List<ValidasiWarkatDTO> saldoCurrent = dao.findSaldoCurrentByNoWarkat(warkatDTO.getNoWarkat());
        List<ValidasiWarkatDTO> saldoPA = dao.findSaldoPAByNoWarkat(warkatDTO.getNoWarkat());
        if (!saldoCurrent.isEmpty() && !saldoPA.isEmpty()) {
            // RETRIEVE SALDO PA
            final List<ValidasiWarkatDTO> saldoPACalculateds = calculateRejectSaldo(warkatDTO, saldoPA);
            saldoPACalculateds.forEach(value -> {
                dao.updateSaldoPA(value, null);
            });

            // RETRIEVE SALDO CURRENT
            final List<ValidasiWarkatDTO> saldoCurrentCalculateds = calculateRejectSaldo(warkatDTO, saldoCurrent);
            saldoCurrentCalculateds.forEach(value -> {
                dao.updateSaldoCurrent(value, null);
            });

        } else return "Data saldo tidak ditemukan";

        try {
            updateStatusWarkat(warkatDTO, aktivitas);
            return "OK";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String generateNuwp() {
        NumberGenerator generator = numberGeneratorService.findByName("ACC_WARKAT_NUWP");
        int numberLength = generator.getGenerateNumber().toString().length();
        String numberNuwp;
        if (numberLength <= 4) {
            numberNuwp = String.format("%04d", generator.getGenerateNumber());
        } else {
            numberNuwp = generator.getGenerateNumber().toString();
        }
        return numberNuwp;
    }


    public String checkSaldo(List<ValidasiWarkatDTO> saldo) {
        for (ValidasiWarkatDTO _saldo : saldo) {
            // TIPE REK BIAYA
            if ((_saldo.getTipeRekening().equalsIgnoreCase("BIAYA")
                    || _saldo.getTipeRekening().equalsIgnoreCase("ASET_OPR"))
                    && _saldo.getSaldoNormal().equals("D")) {

                if (_saldo.getSaldoAnggaran() != null) {
                    // cek jumlah debit tidak boleh > saldo anggaran
                    if (_saldo.getJumlahDebit().compareTo(_saldo.getSaldoAnggaran()) > 0) {
                        return "Saldo akun " + _saldo.getKodeRekekning() + " sebesar " + _saldo.getSaldoAnggaran() + " tidak mencukupi untuk  transaksi sebesar " + _saldo.getJumlahDebit();
                    }
                } else
                    return "Saldo akun " + _saldo.getKodeRekekning() + " - " + _saldo.getNamaRekening() + " tidak ditemukan.";

            }

            // TIPE REK KAS
            if (_saldo.getTipeRekening().equals("KAS") && _saldo.getSaldoNormal().equals("K")) {

                if (_saldo.getSaldoAkhir() != null) {
                    // cek jumlah kredit tidak boleh > saldo akhir
                    if (_saldo.getJumlahKredit().compareTo(_saldo.getSaldoAkhir()) > 0) {
                        return "Saldo akun " + _saldo.getKodeRekekning() + " sebesar " + _saldo.getSaldoAkhir() + " tidak mencukupi untuk  transaksi sebesar " + _saldo.getJumlahKredit();
                    }
                } else
                    return "Saldo akun " + _saldo.getKodeRekekning() + " - " + _saldo.getNamaRekening() + " tidak ditemukan.";

            }
        }
        return "OK";
    }

    public String checkSaldoAnggaran(List<ValidasiWarkatDTO> saldo) {
        for (ValidasiWarkatDTO _saldo : saldo) {
            if (_saldo.getTipeRekening() != null) {
                // TIPE REK BIAYA
                if ((_saldo.getTipeRekening().equals("BIAYA") || _saldo.getTipeRekening().equals("ASET_OPR")) &&
                        _saldo.getSaldoNormal().equals("D")) {

                    if (_saldo.getSaldoAnggaran() != null) {
                        // cek jumlah debit tidak boleh > saldo anggaran
                        if (_saldo.getJumlahDebit().compareTo(_saldo.getSaldoAnggaran()) > 0) {
                            return "Saldo anggaran " + _saldo.getKodeRekekning() + " - " + _saldo.getNamaRekening() + " tidak mencukupi.";
                        }
                    } else
                        return "Data saldo " + _saldo.getKodeRekekning() + " - " + _saldo.getNamaRekening() + " tidak ditemukan.";

                }
            } else {
                return "NOT OK, Tipe rekening null";
            }
        }
        return "OK";
    }

    public List<ValidasiWarkatDTO> calculateValidasiSaldo(WarkatDTO warkatDTO, List<ValidasiWarkatDTO> saldo) {
        List<ValidasiWarkatDTO> saldoCalculateds = new ArrayList<>();
        Integer combination = dao.findAsetCondition(warkatDTO.getNoWarkat());
        for (ValidasiWarkatDTO _saldo : saldo) {
            _saldo.setCreatedBy(warkatDTO.getCreatedBy());
            saldoCalculateds.add(getCalculatedSaldoValidasi(_saldo, combination));
        }
        return saldoCalculateds;
    }

    public List<ValidasiWarkatDTO> calculateRejectSaldo(WarkatDTO warkatDTO, List<ValidasiWarkatDTO> saldo) {
        List<ValidasiWarkatDTO> saldoCalculateds = new ArrayList<>();
        Integer combination = dao.findAsetCondition(warkatDTO.getNoWarkat());
        for (ValidasiWarkatDTO _saldo : saldo) {
            _saldo.setCreatedBy(warkatDTO.getCreatedBy());
            saldoCalculateds.add(getCalculatedSaldoReject(_saldo, combination));
        }
        return saldoCalculateds;
    }

    private ValidasiWarkatDTO getCalculatedSaldoValidasi(ValidasiWarkatDTO saldo, Integer combination) {
        ValidasiWarkatDTO validasiWarkatDTO = new ValidasiWarkatDTO();
        BigDecimal saldoDebit = saldo.getSaldoDebet().add(saldo.getJumlahDebit()); // SALDO_DEBIT += NOMINAL_DEBIT
        BigDecimal saldoKredit = saldo.getSaldoKredit().add(saldo.getJumlahKredit()); // SALDO_KREDIT += NOMINAL_KREDIT

        BigDecimal saldoAkhir; // SALDO_AKHIR = SALDO_AWAL + SALDO_DEBIT - SALDO_KREDIT
        if (saldo.getSaldoNormal().equals("D")) {
            saldoAkhir = saldo.getSaldoAwal().add(saldoDebit).subtract(saldoKredit);
        } else {
            saldoAkhir = saldo.getSaldoAwal().subtract(saldoDebit).add(saldoKredit);
        }

        if (saldo.getTipeRekening().equals("PENDAPATAN")) {
            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .add(saldo.getJumlahDebit())
                    .subtract(saldo.getJumlahKredit()); // SALDO_ANGGARAN += JUMLAH_DEBIT - JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);
        } else if (saldo.getTipeRekening().equals("BIAYA")) { // biaya, aset opr, kombinasi false
            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .subtract(saldo.getJumlahDebit())
                    .add(saldo.getJumlahKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);
        } else if (saldo.getTipeRekening().equals("ASET_OPR")) { // biaya, aset opr, kombinasi false
            if (combination == 2) {
                BigDecimal saldoJual = saldo.getSaldoJual().add(saldo.getJumlahKredit()); // SALDO_JUAL += JUMLAH_KREDIT
                saldo.setSaldoJual(saldoJual);
            } else {
                BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                        .subtract(saldo.getJumlahDebit())
                        .add(saldo.getJumlahKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT + JUMLAH_KREDIT
                saldo.setSaldoAnggaran(saldoAnggaran);
            }
        }

        ValidasiWarkatDTO validationValue = getValidasiWarkatDTO(saldo, validasiWarkatDTO, saldoDebit, saldoKredit, saldoAkhir, saldo.getSaldoAnggaran(), saldo.getSaldoJual());
        return validationValue;
    }

    private ValidasiWarkatDTO getCalculatedSaldoReject(ValidasiWarkatDTO saldo, Integer kombinasi) {
        ValidasiWarkatDTO validasiWarkatDTO = new ValidasiWarkatDTO();
        BigDecimal saldoDebit = saldo.getSaldoDebet().subtract(saldo.getJumlahDebit()); // SALDO_DEBIT -= NOMINAL_DEBIT
        BigDecimal saldoKredit = saldo.getSaldoKredit().subtract(saldo.getJumlahKredit()); // SALDO_KREDIT -= NOMINAL_KREDIT

        BigDecimal saldoAkhir; // SALDO_AKHIR = SALDO_AKHIR + NOMINAL_KREDIT - NOMINAL_DEBIT
        if (saldo.getSaldoNormal().equals("D")) {
            saldoAkhir = saldo.getSaldoAkhir().add(saldo.getJumlahKredit()).subtract(saldo.getJumlahDebit());
        } else {
            saldoAkhir = saldo.getSaldoAkhir().subtract(saldo.getJumlahKredit()).add(saldo.getJumlahDebit());
        }

        if (saldo.getTipeRekening().equals("PENDAPATAN")) {
            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .subtract(saldo.getJumlahDebit())
                    .add(saldo.getJumlahKredit()); // SALDO_ANGGARAN -= JUMLAH_DEBIT - JUMLAH_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);
        } else if (saldo.getTipeRekening().equals("BIAYA")) {
            BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                    .add(saldo.getJumlahDebit())
                    .subtract(saldo.getJumlahKredit()); // SALDO_ANGGARAN += NOMINAL_DEBIT - NOMINAL_KREDIT
            saldo.setSaldoAnggaran(saldoAnggaran);
        } else if (saldo.getTipeRekening().equals("ASET_OPR")) {
            if (kombinasi == 2) {
                BigDecimal saldoJual = saldo.getSaldoJual().subtract(saldo.getJumlahKredit()); // SALDO_JUAL -= JUMLAH_KREDIT
                saldo.setSaldoJual(saldoJual);
            } else {
                BigDecimal saldoAnggaran = saldo.getSaldoAnggaran()
                        .add(saldo.getJumlahDebit())
                        .subtract(saldo.getJumlahKredit()); // SALDO_ANGGARAN += NOMINAL_DEBIT - NOMINAL_KREDIT
                saldo.setSaldoAnggaran(saldoAnggaran);
            }
        }
        ValidasiWarkatDTO validationValue = getValidasiWarkatDTO(saldo, validasiWarkatDTO, saldoDebit, saldoKredit, saldoAkhir, saldo.getSaldoAnggaran(), saldo.getSaldoJual());
        return validationValue;
    }

    private ValidasiWarkatDTO getValidasiWarkatDTO(ValidasiWarkatDTO saldo, ValidasiWarkatDTO validasiWarkatDTO, BigDecimal saldoDebit, BigDecimal saldoKredit, BigDecimal saldoAkhir, BigDecimal saldoAnggaran, BigDecimal saldoJual) {
        validasiWarkatDTO.setIdRekening(saldo.getIdRekening());
        validasiWarkatDTO.setSaldoDebet(saldoDebit);
        validasiWarkatDTO.setSaldoKredit(saldoKredit);
        validasiWarkatDTO.setSaldoAkhir(saldoAkhir);
        validasiWarkatDTO.setSaldoAnggaran(saldoAnggaran);
        validasiWarkatDTO.setSaldoJual(saldoJual);
        validasiWarkatDTO.setIdRekening(saldo.getIdRekening());
        validasiWarkatDTO.setCreatedBy(saldo.getCreatedBy());
        return validasiWarkatDTO;
    }

    @Transactional
    public int updateStatusWarkat(WarkatDTO warkatDTO, String aktivitas) throws Exception {
        Optional<Warkat> warkat = findById(warkatDTO.getNoWarkat());
        warkat.get().setNoWarkat(warkatDTO.getNoWarkat());
        warkat.get().setStatusData(warkatDTO.getStatusData());
        warkat.get().setKeterangan(warkatDTO.getKeterangan());
        warkat.get().setCatatanValidasi(warkatDTO.getCatatanValidasi());
        warkat.get().setNuwp(warkatDTO.getNuwp());
        warkat.get().setArusKas(warkatDTO.getArusKas());
        warkat.get().setTglValidasi(Timestamp.valueOf(LocalDateTime.now()));
        warkat.get().setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        warkat.get().setUserValidasi(warkatDTO.getCreatedBy());
        warkat.get().setUpdatedBy(warkatDTO.getCreatedBy());
        Warkat _save = save(warkat.get());
        saveLog(_save, aktivitas);
        log.info("{}", _save);
        return 1;
    }

    public DataTablesResponse<WarkatDTO> datatables(DataTablesRequest<WarkatDTO> params, String search) {
        List<WarkatDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<WarkatDTO> datatablesLog(DataTablesRequest<WarkatDTO> params, String search) {
        List<WarkatDTO> data = dao.datatableLog(params, search);
        Long rowCount = dao.datatableLog(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<WarkatDTO> datatablesJurnalBiaya(DataTablesRequest<WarkatDTO> params, String search, String statusData) {
        List<WarkatDTO> data = dao.datatableJurnalBiaya(params, search, statusData);
        Long rowCount = dao.datatableJurnalBiaya(search, statusData);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public String getNoWarkat(String satker) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemRepository.findByStatusAktif();
        NumberGenerator accWarkatDraftNo = numberGeneratorService.findByName("ACC_WARKAT_DRAFT");

        StringBuilder noWarkat = new StringBuilder();
        String numberGenerator;

        int numberLength = accWarkatDraftNo.getGenerateNumber().toString().length();

        if (numberLength <= 6) {
            numberGenerator = String.format("%06d", accWarkatDraftNo.getGenerateNumber());
        } else {
            numberGenerator = accWarkatDraftNo.getGenerateNumber().toString();
        }
        return noWarkat.append(pengaturanSistem.get().getKodeTahunBuku()).append("/").append(numberGenerator).append("/").append(satker).toString();
    }

    public List<MSTLookUp> findJenisWarkat() {
        return dao.findJenisWarkat();
    }

    public List<MSTLookUp> findJenisJurnal() {
        return dao.findJenisJurnal();
    }

    public List<MSTLookUp> getStatusWarkatList() {
        return dao.getStatusWarkatList();
    }

    public DataTablesResponse<PengantarWarkatDTO> pengantarWarkatDTODatatables(DataTablesRequest<PengantarWarkatDTO> params, String search) {
        List<PengantarWarkatDTO> data = dao.pengantarWarkatDatatables(params, search);
        Long rowCount = dao.pengantarWarkatDatatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public WarkatDTO findByNoWarkat(String noWarkat) {
        WarkatDTO warkat = dao.findByNoWarkat(noWarkat);
        warkat.setDebitList(warkatJurnalService.findByNoWarkatAndSaldoNormal(noWarkat, "D"));
        warkat.setKreditList(warkatJurnalService.findByNoWarkatAndSaldoNormal(noWarkat, "K"));
        return warkat;
    }

    public Boolean checkStatusOpen() {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (pengaturanSistem.isPresent()) {
            if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("C"))
//                {         // CHECK STATUS OPEN
//                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                    String today = format.format(new Date());
//                    String tglTransaksi = format.format(pengaturanSistem.get().getTglTransaksi());
//                    if (!today.equalsIgnoreCase(tglTransaksi)) {                                        // CHECK TANGGAL TRANSAKSI
//                        return new ResponseEntity<>("Transaksi sebelumnya belum ditutup.", HttpStatus.ACCEPTED);
//                    }
//                } else
                return false;
        } else {
            return false;
        }
        return true;
    }

    public byte[] exportPdf(Map<String, Object> params, List<WarkatDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_warkat_pembukuan.jrxml");
            ClassPathResource jasperStreamSR = new ClassPathResource("/jasper/laporan_warkat_pembukuan_subreport.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportSR = JasperCompileManager.compileReport(jasperStreamSR.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("jurnalDebit", jasperReportSR);
            params.put("jurnalKredit", jasperReportSR);
            params.put("logo", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.US);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
//            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportPdfLog(List<WarkatDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_tolak_warkat.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());

            Map<String, Object> params = new HashMap<>();
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logo", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.US);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
//            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcel(Map<String, Object> params, List<WarkatDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_warkat_pembukuan.jrxml");
            ClassPathResource jasperStreamSR = new ClassPathResource("/jasper/laporan_warkat_pembukuan_subreport.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());
            JasperReport jasperReportSR = JasperCompileManager.compileReport(jasperStreamSR.getInputStream());

            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("jurnalWarkatPembukuan", jasperReportSR);
            params.put("logo", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

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
//            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportExcelLog(List<WarkatDTO> data) {
        try {
            ClassPathResource jasperStream = new ClassPathResource("/jasper/laporan_tolak_warkat.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream.getInputStream());

            Map<String, Object> params = new HashMap<>();
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            params.put("logo", image);
            params.put(JRParameter.REPORT_LOCALE, Locale.US);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    new JRBeanCollectionDataSource(data));

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
//            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    public byte[] exportPengantarWarkatPDF(List<Object> object, Map<String, Object> param, String jasperName) {
        try {
            LookupMaster lookupMaster = lookupMasterRepository.findByKodeLookup("PENGANTAR_WARKAT").get();
            PengaturanSistem pengaturanSistem = pengaturanSistemRepository.findByStatusAktif().get();
            BigInteger pengantarWarkatInt = numberGeneratorService.findByName("PENGANTAR_WARKAT").getGenerateNumber();
            String noPengantarWarkat = "No. " + pengaturanSistem.getKodeTahunBuku() + "/" + pengaturanSistem.getKodePeriode() + "/" + pengantarWarkatInt + "/" + param.get("tanggalPengantarWarkat");

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            param.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            param.put("logoLocation", image);
            param.put("kepada", lookupMaster.getNamaLookup());
            param.put("dari", lookupMaster.getKeterangan());
            param.put("kepalaDivisi", pengaturanSistem.getKdiv());
            param.put("noPengantarWarkat", noPengantarWarkat);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    param,
                    new JRBeanCollectionDataSource(object)
            );

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportPengantarWarkatEXCEL(List<Object> object, Map<String, Object> param, String jasperName) {
        try {
            LookupMaster lookupMaster = lookupMasterRepository.findByKodeLookup("PENGANTAR_WARKAT").get();
            PengaturanSistem pengaturanSistem = pengaturanSistemRepository.findByStatusAktif().get();
            String noPengantarWarkat = "No. " + pengaturanSistem.getKodeTahunBuku() + "/" + pengaturanSistem.getKodePeriode() + "/" + pengaturanSistem.getNoPengantarWarkat() + param.get("tanggalPengantarWarkat");

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            param.put(JRParameter.REPORT_LOCALE, Locale.ITALY);
            param.put("logoLocation", image);
            param.put("kepada", lookupMaster.getNamaLookup());
            param.put("dari", lookupMaster.getKeterangan());
            param.put("kepalaDivisi", pengaturanSistem.getKdiv());
            param.put("noPengantarWarkat", noPengantarWarkat);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    param,
                    new JRBeanCollectionDataSource(object)
            );

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
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

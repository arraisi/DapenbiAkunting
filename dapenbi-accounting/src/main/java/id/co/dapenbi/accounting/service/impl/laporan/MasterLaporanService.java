package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.MasterLaporanDao;
import id.co.dapenbi.accounting.dto.laporan.MasterLaporanDTO;
import id.co.dapenbi.accounting.entity.laporan.*;
import id.co.dapenbi.accounting.entity.parameter.ArusKasRincian;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.mapper.ArusKasBulananMapper;
import id.co.dapenbi.accounting.mapper.LaporanKeuanganMapper;
import id.co.dapenbi.accounting.mapper.LaporanRencanaBisnisMapper;
import id.co.dapenbi.accounting.repository.laporan.*;
import id.co.dapenbi.accounting.repository.parameter.ArusKasRincianRepository;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import id.co.dapenbi.accounting.service.impl.parameter.ArusKasRincianService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MasterLaporanService {

    @Autowired
    private LaporanHeaderRepository laporanHeaderRepository;

    @Autowired
    private LaporanDetailRepository laporanDetailRepository;

    @Autowired
    private MasterLaporanDao masterLaporanDao;

    @Autowired
    private LaporanRekeningRepository laporanRekeningRepository;

    @Autowired
    private RekeningRepository rekeningRepository;

    @Autowired
    private LaporanKeuanganRepository laporanKeuanganRepository;

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private InvestasiHeaderRepository investasiHeaderRepository;

    @Autowired
    private LaporanRencanaBisnisRepository laporanRencanaBisnisRepository;

    @Autowired
    private ArusKasRincianService arusKasRincianService;

    public List<LaporanHeader> listByNamaTabelSortByUrutan(String namaTabel) {
        return laporanHeaderRepository.listByNamaTabelSortByUrutan(namaTabel);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public LaporanHeader save(MasterLaporanDTO dto, String user) {
        LaporanHeader laporanHeader = dto.getLaporanHeader();
        laporanHeader.setCreatedBy(user);
        laporanHeader.setCreatedDate(new Timestamp(new Date().getTime()));
        laporanHeader = laporanHeaderRepository.save(laporanHeader);
        for (LaporanDetail laporanDetail : dto.getLaporanDetails()) {
            laporanDetail.setLaporanHeader(laporanHeader.getIdLaporanHdr() + 1);
            laporanDetail.setCreatedBy(user);
            laporanDetail.setCreatedDate(new Timestamp(new Date().getTime()));
            laporanDetailRepository.save(laporanDetail);
        }
        return laporanHeader;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public LaporanHeader update(MasterLaporanDTO dto, String user) {
        Optional<LaporanHeader> oldData = laporanHeaderRepository.findById(dto.getIdLaporanHdr());
        LaporanHeader newData = new LaporanHeader();
        if (oldData.isPresent()) {
            newData = oldData.get();
            newData.setNamaLaporan(dto.getNamaLaporan());
            newData.setKeterangan(dto.getKeterangan());
            newData.setUpdatedBy(user);
            newData.setUpdatedDate(new Timestamp(new Date().getTime()));
            newData = laporanHeaderRepository.save(newData);

            laporanDetailRepository.deleteByLaporanHeader(newData.getIdLaporanHdr());
            for (LaporanDetail laporanDetail : dto.getLaporanDetails()) {
                laporanDetail.setLaporanHeader(newData.getIdLaporanHdr());
                laporanDetail.setCreatedBy(user);
                laporanDetail.setCreatedDate(new Timestamp(new Date().getTime()));
                laporanDetailRepository.save(laporanDetail);
            }
        }
        return newData;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer idLapHeader) {
        Iterable<LaporanDetail> laporanDetails = laporanDetailRepository.findByLaporanHeader(idLapHeader);
        laporanDetails.forEach(v -> {
            laporanRekeningRepository.deleteByIdLaporanDtl(v.getIdLaporanDtl());
        });
        laporanDetailRepository.deleteByLaporanHeader(idLapHeader);
        laporanHeaderRepository.deleteById(idLapHeader);
    }

    public DataTablesOutput<LaporanHeader> getDataTablesOutput(DataTablesInput input) {
        return laporanHeaderRepository.findAll(input);
    }

    public Iterable<LaporanDetail> findByLapHeader(Integer idLapHeader) {
        return laporanDetailRepository.findByLaporanHeader(idLapHeader);
    }

    public List<LaporanHeader> findAllHeader() {
        return IterableUtils.toList(laporanHeaderRepository.findAll());
    }

    public DataTablesResponse<LaporanDetail> laporanRumusDatatables(DataTablesRequest<LaporanDetail> params, String search) {
        List<LaporanDetail> data = masterLaporanDao.laporanRumusDatatables(params, search);
        Long rowCount = masterLaporanDao.laporanRumusDatatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public Optional<LaporanDetail> findByIdLaporanDetail(Integer idLaporanDetail) {
        return laporanDetailRepository.findById(idLaporanDetail);
    }

    public DataTablesResponse<LaporanRekening> laporanRekeningDatatables(DataTablesRequest<LaporanRekening> params, String search) {
        List<LaporanRekening> data = masterLaporanDao.laporanRekeningDatatables(params, search);
        Long rowCount = masterLaporanDao.laporanRekeningDatatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public LaporanHeader saveLaporanHeader(LaporanHeader laporanHeader) {
        return laporanHeaderRepository.save(laporanHeader);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateLaporanHeader(LaporanHeader laporanHeader) {
        laporanHeaderRepository.updateLaporanHeader(
                laporanHeader.getIdLaporanHdr(),
                laporanHeader.getNamaLaporan(),
                laporanHeader.getKeterangan(),
                laporanHeader.getNamaTabel(),
                laporanHeader.getUrutan(),
                laporanHeader.getStatusData(),
                laporanHeader.getUpdatedBy(),
                laporanHeader.getUpdatedDate()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public LaporanDetail saveLaporanDetail(LaporanDetail laporanDetail) {
        return laporanDetailRepository.save(laporanDetail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateLaporanDetail(LaporanDetail laporanDetail) {
        laporanDetailRepository.updateLaporanDetail(
                laporanDetail.getIdLaporanDtl(),
                laporanDetail.getKodeRumus(),
                laporanDetail.getLevelAkun(),
                laporanDetail.getRumus(),
                laporanDetail.getStatusData(),
                laporanDetail.getUpdatedBy(),
                laporanDetail.getUpdatedDate(),
                laporanDetail.getCetakJudul(),
                laporanDetail.getCetakGaris(),
                laporanDetail.getSpi(),
                laporanDetail.getJudul(),
                laporanDetail.getUrutan(),
                laporanDetail.getSaldoSebelum(),
                laporanDetail.getStatusRumus());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteLaporanDetail(Integer id) {
        laporanRekeningRepository.deleteByIdLaporanDtl(id);
        laporanDetailRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public LaporanRekening saveLaporanRekening(LaporanRekening laporanRekening) {
        Optional<Rekening> rekeningOptional = rekeningRepository.findById(laporanRekening.getIdRekening());
        Rekening rekening = rekeningOptional.orElseGet(Rekening::new);
        laporanRekening.setKodeRekening(rekening.getKodeRekening());
        laporanRekening.setNamaRekening(rekening.getNamaRekening());
        return laporanRekeningRepository.save(laporanRekening);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateLaporanRekening(LaporanRekening laporanRekening) {
        Optional<Rekening> rekeningOptional = rekeningRepository.findById(laporanRekening.getIdRekening());
        Rekening rekening = rekeningOptional.orElseGet(Rekening::new);
        laporanRekening.setKodeRekening(rekening.getKodeRekening());
        laporanRekening.setNamaRekening(rekening.getNamaRekening());
        masterLaporanDao.updateLaporanRekening(laporanRekening);
//        log.info("Laporan Rekening: {}", laporanRekening);
//        laporanRekeningRepository.updateLaporanRekening(
//                laporanRekening.getIdLaporanRek(),
//                laporanRekening.getIdRekening(),
//                laporanRekening.getRumusUrutan(),
//                laporanRekening.getRumusOperator(),
//                laporanRekening.getStatusData(),
//                laporanRekening.getUpdatedBy(),
//                laporanRekening.getUpdatedDate()
//        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteLaporanRekening(Integer id) {
        laporanRekeningRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteByTahunBukuAndPeriodeAndDRI(String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        laporanKeuanganRepository.deleteByTahunBukuAndPeriodeAndDRI(kodeTahunBuku, kodePeriode, kodeDRI);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void prosesMasterLaporanKeuangan(Map<String, String> object, Principal principal) throws ParseException {
//        log.info("Testing object: {}", object);
        Iterable<LaporanDetail> laporanDetails = laporanDetailRepository.findByLaporanHeader(Integer.valueOf(object.get("idLaporanHdr")));

        if (object.get("idLaporanHdr").equals("5")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(object.get("tanggal"));
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            arusKasRincianService.deleteArusKasBulananByTahunBukuAndPeriodeAndDRI(object.get("kodeTahunBuku"), object.get("kodePeriode"), object.get("dri"));
            arusKasRincianService.copyArusKasRincianToArusKasBulanan(object.get("kodeTahunBuku"), object.get("kodePeriode"), object.get("dri"), timestamp, principal.getName());
        }

//        log.info("List Laporan Detail: {}", laporanDetails);
        for (LaporanDetail laporanDetail : laporanDetails) {
            LaporanKeuangan laporanKeuangan = LaporanKeuanganMapper.INSTANCE.laporanDetailToLaporanKeuangan(laporanDetail);
            laporanKeuangan.setKodeThnBuku(object.get("kodeTahunBuku"));
            laporanKeuangan.setKodePeriode(object.get("kodePeriode"));
            laporanKeuangan.setKodeDRI(object.get("dri"));
            laporanKeuangan.setCreatedBy(principal.getName());
            laporanKeuangan.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
//            log.info("Testing Mapper Laporan Keuangan: {}", laporanKeuangan);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(object.get("tanggal"));
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            if (laporanDetail.getSpi().equals("Y")) {
                Optional<InvestasiHeader> investasiHeaderOptional = investasiHeaderRepository.findByIdLaporanHdrAndidLaporanDtl(laporanDetail.getLaporanHeader(), laporanDetail.getIdLaporanDtl());
                BigDecimal nilaiWajar = investasiHeaderOptional.isPresent() ? masterLaporanDao.sumNilaiWajarFromSPIDtlByTahunBukuAndPeriode(investasiHeaderOptional.get().getIdInvestasi(), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode()) : BigDecimal.ZERO;
//                laporanKeuangan.setTglLaporan(timestamp);
                laporanKeuangan.setSaldoBerjalan(nilaiWajar);
            } else if (laporanDetail.getRumus() != null && !laporanDetail.getRumus().isEmpty() && ((laporanDetail.getStatusRumus().equals(object.get("jenis"))) || laporanDetail.getStatusRumus().equals("ALL"))) {
                String[] getNumber = laporanDetail.getRumus().split("(?<=[-+*/()])|(?=[-+*/()])");
                if (laporanKeuangan.getKodeRumus().equals("C48")) log.info("Split String: {}", (Object) getNumber);
                for (int j = 0; j < getNumber.length; j++) {
                    if (!getNumber[j].equals("%") && !getNumber[j].equals("/") && !getNumber[j].equals("*") && !getNumber[j].equals("+") && !getNumber[j].equals("-") && !getNumber[j].equals("(") && !getNumber[j].equals(")") && !getNumber[j].contains("#") && !getNumber[j].contains("!") && !getNumber[j].contains("?") && !getNumber[j].contains("&")) {
//                        if (getNumber[j].equals("F10")) log.info("Kode Rumus: {}, ID HDR: {}, Tahun Buku: {}, Periode: {}, DRI: {}", getNumber[j], laporanDetail.getLaporanHeader(), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI());
//                        List<LaporanKeuangan> list = laporanKeuanganRepository.listByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(getNumber[j], laporanDetail.getLaporanHeader(), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI());
//                        if (list.size() > 1) {
//                            log.info("The Trouble!: {}", list);
//                        }
                        Optional<LaporanKeuangan> laporanKeuanganOptional = laporanKeuanganRepository.findByKodeRumusAndTahunBukuAndPeriodeAndDRI(getNumber[j], laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI(), laporanKeuangan.getIdLaporanDtl());
//                        if (getNumber[j].equals("C47") || getNumber[j].equals("C48") || getNumber[j].equals("B25")) log.info("Kode : {}", laporanKeuanganOptional.get());
                        BigDecimal saldoAkhir = laporanKeuanganOptional.isPresent() ? laporanKeuanganOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                        if (getNumber[j].contains("-")) getNumber[j] = "(" + getNumber[j] + ")";
                    } else if (getNumber[j].contains("#")) {
                        getNumber[j] = getNumber[j].replace("#", "");
                    } else if (getNumber[j].contains("!")) {
                        Optional<LaporanKeuangan> laporanKeuanganOptional = laporanKeuanganRepository.findByKodeRumusAndTahunBukuAndPeriodeAndDRI(getNumber[j].replace("!", ""), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI(), laporanKeuangan.getIdLaporanDtl());
//                        if (getNumber[j].contains("C47") || getNumber[j].contains("C48")) log.info("Kode : {}", laporanKeuanganOptional.get());
                        BigDecimal saldoAkhir = laporanKeuanganOptional.isPresent() ? laporanKeuanganOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                        if (getNumber[j].contains("-")) getNumber[j] = "(" + getNumber[j] + ")";
                    } else if (getNumber[j].contains("?")) {
//                        Optional<LaporanKeuangan> laporanKeuanganOptional = laporanKeuanganRepository.findByKodeRumusAndTahunBukuAndPeriodeAndDRI(getNumber[j].replace("?", ""), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI(), laporanKeuangan.getIdLaporanDtl());
//                        if (getNumber[j].contains("C47") || getNumber[j].contains("C48")) log.info("Kode : {}", laporanKeuanganOptional.get());
                        Optional<LaporanDetail> laporanDetailOptional = laporanDetailRepository.findByKodeRumus(getNumber[j].replace("?", ""));
//                        BigDecimal saldoAkhir = laporanKeuanganOptional.isPresent() ? laporanKeuanganOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        BigDecimal saldoAkhir = laporanDetailOptional.isPresent() ? laporanDetailOptional.get().getSaldoSebelum() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                        if (getNumber[j].contains("-")) getNumber[j] = "(" + getNumber[j] + ")";
                    } else if (getNumber[j].contains("&")) {
                        String kodeArusKas = getNumber[j].replace("&", "");
//                        BigDecimal penambahan = arusKasRincianService.sumArusKasRincian(laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI(), object.get("tanggal"), "1", kodeArusKas);
//                        BigDecimal pengurangan = arusKasRincianService.sumArusKasRincian(laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI(), object.get("tanggal"), "2", kodeArusKas);
                        BigDecimal penambahan = masterLaporanDao.sumSaldoFromArusKasBulanan(kodeArusKas, object.get("tanggal"), "1");
                        BigDecimal pengurangan = masterLaporanDao.sumSaldoFromArusKasBulanan(kodeArusKas, object.get("tanggal"), "2");
                        BigDecimal saldoBerjalan = penambahan.subtract(pengurangan);
                        getNumber[j] = saldoBerjalan.toString();
                        if (getNumber[j].contains("-")) getNumber[j] = "(" + getNumber[j] + ")";
                    }
                    if (laporanKeuangan.getKodeRumus().equals("")) log.info("Build String: {}", (Object) getNumber);
                }
                String res = String.join("", getNumber);
                res = res.replace("0/0", "0");
                if (laporanKeuangan.getKodeRumus().equals("C48")) log.info("String Result: {}", res);
//                log.info("Eval Result: {}", eval(res));
                BigDecimal saldoBerjalan = BigDecimal.valueOf(eval(res));
//                log.info("BigDecimal Result: {}", saldoBerjalan);
//                laporanKeuangan.setTglLaporan(timestamp);
                laporanKeuangan.setSaldoBerjalan(saldoBerjalan);
            }

            Iterable<LaporanRekening> laporanRekenings = laporanRekeningRepository.findByIdLaporanDtl(laporanDetail.getIdLaporanDtl());
            BigDecimal saldoBerjalan = BigDecimal.ZERO;
            BigDecimal getSaldoBerjalan = laporanKeuangan.getSaldoBerjalan() == null ? BigDecimal.ZERO : laporanKeuangan.getSaldoBerjalan();
            for (LaporanRekening laporanRekening : laporanRekenings) {
                BigDecimal saldoAkhir = masterLaporanDao.sumSaldoAkhirFromSaldoByIdRekeningAndTglSaldoAndDRI(laporanRekening.getKodeRekening(), object.get("tanggal"), laporanKeuangan.getKodeDRI());
                if (laporanRekening.getRumusOperator().equals("1")) {
//                            if (laporanKeuangan.getIdLaporanDtl() == 32) log.info("Saldo Berjalan: {}, Kode Rekening: {}", saldoBerjalan, laporanRekening.getKodeRekening());
                    saldoBerjalan = saldoBerjalan.add(saldoAkhir);
                } else if (laporanRekening.getRumusOperator().equals("0")) {
                    saldoBerjalan = saldoBerjalan.subtract(saldoAkhir);
                }
            };
//            log.info("Saldo Berjalan: {}, Result: {}", laporanKeuangan.getSaldoBerjalan(), saldoBerjalan);
            laporanKeuangan.setSaldoBerjalan(getSaldoBerjalan.add(saldoBerjalan));
            if (laporanDetail.getJudul().contains("Pendapatan Kena Pajak")) laporanKeuangan.setSaldoBerjalan(roundHundredValue(laporanKeuangan.getSaldoBerjalan()));
            laporanKeuangan.setTglLaporan(timestamp);

//            laporanKeuanganRepository.deleteByTahunBukuAndPeriodeAndDRI(laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI());
            laporanKeuanganRepository.save(laporanKeuangan);

//            Optional<LaporanKeuangan> laporanKeuanganOptional = laporanKeuanganRepository.findByIdLaporanDtlAndTahunBukuAndPeriodeAndDRI(laporanDetail.getIdLaporanDtl(), laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanKeuangan.getKodeDRI());
//            if (laporanKeuanganOptional.isPresent()) {
//                laporanKeuangan.setUpdatedBy(principal.getName());
//                laporanKeuangan.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
//                BigDecimal saldoSebelum = laporanKeuanganOptional.get().getSaldoSebelum();
//                laporanKeuanganRepository.update(laporanKeuanganOptional.get().getIdLaporan(), laporanKeuangan.getSaldoBerjalan(), saldoSebelum, timestamp, laporanKeuangan.getUpdatedBy(), laporanKeuangan.getUpdatedDate());
//            } else {
////                log.info("Kode Laporan Detail: {}", laporanDetail.getIdLaporanDtl());
////                if (laporanKeuangan.getSaldoSebelum().equals(BigDecimal.ZERO)) laporanKeuangan.setSaldoSebelum(masterLaporanDao.getSaldoSebelum(laporanKeuangan.getKodeThnBuku(), laporanKeuangan.getKodePeriode(), laporanDetail.getIdLaporanDtl(), laporanKeuangan.getKodeDRI()));
//                laporanKeuanganRepository.save(laporanKeuangan);
//            }
        };
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void prosesMasterLaporanRencanaBisnis(Map<String, String> object, Principal principal) throws ParseException {
        Iterable<LaporanDetail> laporanDetails = laporanDetailRepository.findByLaporanHeader(Integer.valueOf(object.get("idLaporanHdr")));
//        log.info("List Laporan Detail: {}", laporanDetails);
        for (LaporanDetail laporanDetail : laporanDetails) {
            LaporanRencanaBisnis laporanRencanaBisnis = LaporanRencanaBisnisMapper.INSTANCE.laporanDetailToLaporanRencanaBisnis(laporanDetail);
            laporanRencanaBisnis.setKodeThnBuku(object.get("kodeTahunBuku"));
            laporanRencanaBisnis.setKodePeriode(object.get("kodePeriode"));
            laporanRencanaBisnis.setKodeDRI(object.get("dri"));
            laporanRencanaBisnis.setCreatedBy(principal.getName());
            laporanRencanaBisnis.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
//            log.info("Testing Mapper Laporan Keuangan: {}", laporanKeuangan);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(object.get("tanggal"));
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            if (laporanDetail.getSpi().equals("Y")) {
                Optional<InvestasiHeader> investasiHeaderOptional = investasiHeaderRepository.findByIdLaporanHdrAndidLaporanDtl(laporanDetail.getLaporanHeader(), laporanDetail.getIdLaporanDtl());
                BigDecimal nilaiWajar = investasiHeaderOptional.isPresent() ? masterLaporanDao.sumNilaiWajarFromSPIDtlByTahunBukuAndPeriode(investasiHeaderOptional.get().getIdInvestasi(), laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode()) : BigDecimal.ZERO;
                laporanRencanaBisnis.setTglLaporan(timestamp);
                laporanRencanaBisnis.setSaldoBerjalan(nilaiWajar);
            } else if (laporanDetail.getRumus() != null && !laporanDetail.getRumus().isEmpty()) {
                String[] getNumber = laporanDetail.getRumus().split("(?<=[-+*/()])|(?=[-+*/()])");
//                log.info("Split String: {}", (Object) getNumber);
                for (int j = 0; j < getNumber.length; j++) {
                    if (!getNumber[j].equals("%") && !getNumber[j].equals("/") && !getNumber[j].equals("*") && !getNumber[j].equals("+") && !getNumber[j].equals("-") && !getNumber[j].equals("(") && !getNumber[j].equals(")") && !getNumber[j].contains("#")) {
                        Optional<LaporanRencanaBisnis> laporanRencanaBisnisOptional = laporanRencanaBisnisRepository.findByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(getNumber[j], laporanDetail.getLaporanHeader(), laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode(), laporanRencanaBisnis.getKodeDRI());
                        BigDecimal saldoAkhir = laporanRencanaBisnisOptional.isPresent() ? laporanRencanaBisnisOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                    } else if (getNumber[j].contains("#")) {
                        getNumber[j] = getNumber[j].replace("#", "");
                    } else if (getNumber[j].contains("!")) {
                        Optional<LaporanRencanaBisnis> laporanRencanaBisnisOptional = laporanRencanaBisnisRepository.findByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(getNumber[j], laporanDetail.getLaporanHeader(), laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode(), laporanRencanaBisnis.getKodeDRI());
                        BigDecimal saldoAkhir = laporanRencanaBisnisOptional.isPresent() ? laporanRencanaBisnisOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                    } else if (getNumber[j].contains("?")) {
                        Optional<LaporanRencanaBisnis> laporanRencanaBisnisOptional = laporanRencanaBisnisRepository.findByKodeRumusAndIdLaporanHdrAndTahunBukuAndPeriodeAndDRI(getNumber[j], laporanDetail.getLaporanHeader(), laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode(), laporanRencanaBisnis.getKodeDRI());
                        BigDecimal saldoAkhir = laporanRencanaBisnisOptional.isPresent() ? laporanRencanaBisnisOptional.get().getSaldoBerjalan() : BigDecimal.ZERO;
                        getNumber[j] = saldoAkhir.toString();
                    }
//                    log.info("Build String: {}", (Object) getNumber);
                }
                String res = String.join("", getNumber);
                res = res.replace("0/0", "0");
//                log.info("String Result: {}", res);
//                log.info("Eval Result: {}", eval(res));
                BigDecimal saldoBerjalan = BigDecimal.valueOf(eval(res));
//                log.info("BigDecimal Result: {}", saldoBerjalan);
                laporanRencanaBisnis.setTglLaporan(timestamp);
                laporanRencanaBisnis.setSaldoBerjalan(saldoBerjalan);
            } else {
                Iterable<LaporanRekening> laporanRekenings = laporanRekeningRepository.findByIdLaporanDtl(laporanDetail.getIdLaporanDtl());
                BigDecimal saldoBerjalan = BigDecimal.ZERO;
                for (LaporanRekening laporanRekening : laporanRekenings) {
                    BigDecimal saldoAkhir = masterLaporanDao.sumSaldoAkhirFromSaldoByIdRekeningAndTglSaldoAndDRI(laporanRekening.getKodeRekening(), object.get("tanggal"), laporanRencanaBisnis.getKodeDRI());
                    if (laporanRekening.getRumusOperator().equals("1")) {
                        saldoBerjalan = saldoBerjalan.add(saldoAkhir);
                    } else if (laporanRekening.getRumusOperator().equals("0")) {
                        saldoBerjalan = saldoBerjalan.subtract(saldoAkhir);
                    }
                };
//                log.info("Testing saldo berjalan: {}", saldoBerjalan);
                laporanRencanaBisnis.setTglLaporan(timestamp);
                laporanRencanaBisnis.setSaldoBerjalan(saldoBerjalan);
            }
            Optional<LaporanRencanaBisnis> laporanRencanaBisnisOptional = laporanRencanaBisnisRepository.findByIdLaporanDtlAndTahunBukuAndPeriodeAndDRI(laporanDetail.getIdLaporanDtl(), laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode(), laporanRencanaBisnis.getKodeDRI());
            if (laporanRencanaBisnisOptional.isPresent()) {
                laporanRencanaBisnis.setUpdatedBy(principal.getName());
                laporanRencanaBisnis.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
                laporanRencanaBisnisRepository.update(laporanRencanaBisnisOptional.get().getIdLaporan(), laporanRencanaBisnis.getSaldoBerjalan(), laporanRencanaBisnisOptional.get().getSaldoSebelum(), timestamp, laporanRencanaBisnis.getUpdatedBy(), laporanRencanaBisnis.getUpdatedDate());
            } else {
                laporanRencanaBisnis.setSaldoSebelum(masterLaporanDao.getSaldoSebelum(laporanRencanaBisnis.getKodeThnBuku(), laporanRencanaBisnis.getKodePeriode(), laporanDetail.getIdLaporanDtl(), laporanRencanaBisnis.getKodeDRI()));
                laporanRencanaBisnisRepository.save(laporanRencanaBisnis);
            }
        };
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x = BigDecimal.valueOf(x).multiply(BigDecimal.valueOf(parseFactor())).setScale(4, RoundingMode.HALF_UP).doubleValue(); // multiplication
                    else if (eat('/')) x = BigDecimal.valueOf(x).divide(BigDecimal.valueOf(parseFactor()), MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP).doubleValue(); // division
                    else return x;
                }
//                for (;;) {
//                    if      (eat('*')) x *= parseFactor(); // multiplication
//                    else if (eat('/')) x /= parseFactor(); // division
//                    else return x;
//                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public static BigDecimal roundHundredValue(BigDecimal value) {
        value = value.multiply(new BigDecimal(0.001));
        value = value.setScale(3, RoundingMode.UP);
        String n = value.toString();
        if (n.indexOf(".") > 0)
            n = n.substring(0, n.indexOf("."));
        value = new BigDecimal(n);
        value = value.multiply(new BigDecimal(1000));
        return value;
    }
}

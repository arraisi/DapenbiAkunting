package id.co.dapenbi.accounting.service.impl.anggaran;

import id.co.dapenbi.accounting.dao.anggaran.AnggaranDao;
import id.co.dapenbi.accounting.dto.anggaran.AnggaranDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.anggaran.Anggaran;
import id.co.dapenbi.accounting.entity.master.LookupMaster;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.SaldoFA;
import id.co.dapenbi.accounting.entity.transaksi.SaldoPA;
import id.co.dapenbi.accounting.repository.NumberGeneratorRepository;
import id.co.dapenbi.accounting.repository.anggaran.AnggaranRepository;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import id.co.dapenbi.accounting.repository.parameter.PeriodeRepository;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import id.co.dapenbi.accounting.repository.parameter.TahunBukuRepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoCurrentRepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoFARepository;
import id.co.dapenbi.accounting.repository.transaksi.SaldoPARepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AnggaranService {

    @Autowired
    private AnggaranRepository repository;

    @Autowired
    private RekeningRepository rekeningRepository;

    @Autowired
    private PeriodeRepository periodeRepository;

    @Autowired
    private TahunBukuRepository tahunBukuRepository;

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private NumberGeneratorRepository numberGeneratorRepository;

    @Autowired
    private AnggaranDao anggaranDao;

    @Autowired
    private SaldoCurrentRepository saldoCurrentRepository;

    @Autowired
    private SaldoFARepository saldoFARepository;

    @Autowired
    private SaldoPARepository saldoPARepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Anggaran save(Anggaran anggaran) {
        String version = getVersion(
                anggaran.getIdRekening().getIdRekening(), anggaran.getTahunBuku().getKodeTahunBuku()
        );
        anggaran.setVersi(version);
//        anggaran.setStatusAktif("1");
        anggaran.setNoAnggaran(generateNoAT());
        anggaran = repository.save(anggaran);
        numberGeneratorRepository.incrementByName("ACC_ANGGARAN_NO_AT");
        return anggaran;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Anggaran update(Anggaran newAnggaran) {
        Optional<Anggaran> oldAnggaran = repository.findById(newAnggaran.getNoAnggaran());
        if (oldAnggaran.isPresent()) {
            newAnggaran.setVersi(oldAnggaran.get().getVersi());
            newAnggaran.setKeterangan(oldAnggaran.get().getKeterangan());
            newAnggaran.setTerbilang(oldAnggaran.get().getTerbilang());
            newAnggaran.setTglValidasi(oldAnggaran.get().getTglValidasi());
            newAnggaran.setUserValidasi(oldAnggaran.get().getUserValidasi());
            newAnggaran.setCatatanValidasi(oldAnggaran.get().getCatatanValidasi());
            newAnggaran.setCreatedBy(oldAnggaran.get().getCreatedBy());
            newAnggaran.setCreatedDate(oldAnggaran.get().getCreatedDate());
        }
        newAnggaran = repository.save(newAnggaran);
        return newAnggaran;
    }

    @Transactional
    public Anggaran updateStatusData(String noAnggaran, String statusData, Integer idRekening, String kdTahunBuku, String user) {
        if (idRekening != null) updateStatusAktifByRekening(idRekening, kdTahunBuku);
        Optional<Anggaran> oldAnggaran = repository.findById(noAnggaran);
        Anggaran newAnggaran;
        if (oldAnggaran.isPresent()) {
            newAnggaran = oldAnggaran.get();
            newAnggaran.setStatusData(statusData);
            newAnggaran.setStatusAktif("1");
            newAnggaran.setUpdatedBy(user);
            newAnggaran.setUpdatedDate(new Timestamp(new Date().getTime()));
            newAnggaran.setUserValidasi(user);
            newAnggaran.setTglValidasi(Timestamp.valueOf(LocalDateTime.now()));
            newAnggaran = repository.save(newAnggaran);
            return newAnggaran;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Anggaran rejectWithDescription(Anggaran anggaran) {
        Optional<Anggaran> oldAnggaran = repository.findById(anggaran.getNoAnggaran());
        Anggaran newAnggaran;
        if (oldAnggaran.isPresent()) {
            newAnggaran = oldAnggaran.get();
            newAnggaran.setStatusData(anggaran.getStatusData());
            newAnggaran.setUpdatedBy(anggaran.getUpdatedBy());
            newAnggaran.setUpdatedDate(new Timestamp(new Date().getTime()));
            newAnggaran.setCatatanValidasi(anggaran.getCatatanValidasi());
            newAnggaran = repository.save(newAnggaran);
            return newAnggaran;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String noAnggaran) {
        repository.deleteById(noAnggaran);
    }

    public DataTablesOutput<Anggaran> penyusunanDataTable(DataTablesInput input) {
        return repository.findAll(input);
    }

    public DataTablesOutput<Anggaran> penyusunanDataTable(DataTablesInput input, String idSatker) {
        Specification<Anggaran> specification = this.byIdSatker(idSatker);
        return repository.findAll(input, specification);
    }

    public DataTablesOutput<Anggaran> validasiDataTable(DataTablesInput input) {
        Specification<Anggaran> anggaranSpecification = this.byStatus("SUBMIT");
        return repository.findAll(input, anggaranSpecification);
    }

    private Specification<Anggaran> byStatus(String status) {
        return (book, cq, cb) -> cb.equal(book.get("statusData"), status);
    }

    private Specification<Anggaran> byIdSatker(String satker) {
        return (anggaran, cq, cb) -> cb.equal(anggaran.get("idSatker"), new LookupMaster(satker));
    }

    public DataTablesOutput<Rekening> findForMataAnggaranDataTable(DataTablesInput input) {
        return rekeningRepository.findAll(input);
    }

    public Rekening findIdRekening(Integer idRekening) {
        return rekeningRepository.findByIdRekening(idRekening).orElse(null);
    }

    public TahunBuku findByKodeTahunBuku(String kodeTahunBuku) {
        return tahunBukuRepository.findById(kodeTahunBuku).orElse(null);

    }

    public BigDecimal getDataRelasi(Long mataAnggaran) {
        return anggaranDao.getDataRelasi(mataAnggaran);
    }

    public BigDecimal getDataAT(Long mataAnggaran, String kodeTahunBuku) {
        TahunBuku tahunBuku = tahunBukuRepository.findById(kodeTahunBuku).get();

        String kodeTahunBukuSebelum = String.valueOf(Integer.parseInt(tahunBuku.getTahun()) - 1);

        Optional<TahunBuku> tahunBukuSebelumOptional = tahunBukuRepository.findByTahun(kodeTahunBukuSebelum);
        TahunBuku tahunBukuSebelum = tahunBukuSebelumOptional.orElseGet(TahunBuku::new);
        try {
            return anggaranDao.getDataAT(mataAnggaran, kodeTahunBuku);
        } catch (IncorrectResultSizeDataAccessException e) {
            return BigDecimal.ZERO;
        }
//        if (tahunBukuSebelumOptional.isPresent()) {
//            try {
//                return anggaranDao.getDataAT(mataAnggaran, kodeTahunBuku);
//            } catch (IncorrectResultSizeDataAccessException e) {
//                return BigDecimal.ZERO;
//            }
//        } else
//            return BigDecimal.ZERO;
    }

    public List<Periode> findAllPeriode() {
        return IterableUtils.toList(periodeRepository.findAll());
    }

    public AnggaranDTO.Value getValue() {
        AnggaranDTO.Value anggaranDTO = new AnggaranDTO.Value();
        Optional<PengaturanSistem> tahunBukuBerjalan = pengaturanSistemRepository.findByStatusAktif();
        if (tahunBukuBerjalan.isPresent()) {
            Optional<TahunBuku> tahunBuku = tahunBukuRepository.findById(tahunBukuBerjalan.get().getKodeTahunBuku());
            anggaranDTO.setTahunBukuBerjalan(tahunBuku.get());
            String thnBerikut = String.valueOf(Integer.parseInt(tahunBuku.get().getTahun()) + 1);
            Optional<TahunBuku> tahunBukuBeberikut = tahunBukuRepository.findByTahun(thnBerikut);
            tahunBukuBeberikut.ifPresent(anggaranDTO::setTahunBukuBerikut);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        if (month.length() <= 1) month = "0" + month;

        Optional<Periode> periode = periodeRepository.findById(month);
        periode.ifPresent(anggaranDTO::setPeriode);
//        anggaranDTO.setNoAT(generateNoAT());
        return anggaranDTO;
    }

    public List<List<Object>> getExcelValue(String filePath) {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            List<List<Object>> lists = new ArrayList<>();
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    lists.add(new ArrayList<>());
                } else {
                    List<Object> objects = new ArrayList<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            objects.add("");
//                            log.info("CELL NULL: {}", objects);
                        } else {
                            switch (cell.getCellTypeEnum()) {
                                case STRING:
                                    objects.add(cell.getStringCellValue());
//                                    log.info("CELL STRING: {}", objects);
                                    break;
                                case NUMERIC:
                                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                        objects.add(cell.getDateCellValue());
                                    } else {
                                        objects.add((int) cell.getNumericCellValue());
                                    }
//                                    log.info("CELL NUMERIC: {}", objects);
                                    break;
                            }
                        }
                    }
                    lists.add(objects);
                }
            }
            return lists;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void reWriteExcel(AnggaranDTO.Excel param) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet 1");


        int rowCount = 0;

        for (List<Object> aBook : param.getListData()) {
            Row row = sheet.createRow(rowCount);

            int columnCount = 0;

            for (Object field : aBook) {
                log.info("Row: {}, Column: {}", rowCount, columnCount);
                log.info("VALUE CELL: {}", field);
                Cell cell = row.createCell(columnCount);
                if (field == null) {
                    cell.setCellValue("");
                }
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
                columnCount++;
            }

            rowCount++;
        }

        try (FileOutputStream outputStream = new FileOutputStream(param.getFilePath())) {
            workbook.write(outputStream);
        }
    }

    @Transactional
    public String generateNoAT() {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemRepository.findByStatusAktif();
        Optional<NumberGenerator> numberGenerator = numberGeneratorRepository.findByName("ACC_ANGGARAN_NO_AT");

        String kodeTahunBuku = pengaturanSistem.get().getKodeTahunBuku();
        String kodePeriode = pengaturanSistem.get().getKodePeriode();

        StringBuilder noAT = new StringBuilder();
        BigInteger counter = numberGenerator.get().getGenerateNumber().add(new BigInteger("1"));
        Integer prefixTotal = 6 - String.valueOf(counter).length();
        String prefixValue = "";
        while (prefixTotal > 0) {
            prefixValue = prefixValue + "0";
            prefixTotal--;
        }
        return noAT.append(kodeTahunBuku).append(kodePeriode).append(prefixValue).append(counter).toString();
    }

    @Transactional
    public String getVersion(Integer idRekening, String kodeTahunBuku) {
        List<Anggaran> anggaranList = repository.listByRekeningAndTahunBuku(idRekening, kodeTahunBuku);
        Integer counter = !anggaranList.isEmpty() ? Integer.parseInt(anggaranList.get(0).getVersi()) + 1 : 1;
        String version = counter.toString();

        /*if (!anggaranList.isEmpty())
            setStatusAktif("0", anggaranList.get(0).getNoAnggaran());*/
        return version;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void setStatusAktif(String statusAktif, String noAnggaran) {
        repository.setStatusAktif(statusAktif, noAnggaran);
    }

    public BigDecimal getRealisasiBerjalan(Integer idRekening, String kodeTahunBuku) {
        log.info("idRekening {}", idRekening);
        log.info("kodeTahunBuku {}", kodeTahunBuku);

        TahunBuku tahunBuku = tahunBukuRepository.findById(kodeTahunBuku).get();

        String kodeTahunBukuSebelum = String.valueOf(Integer.parseInt(tahunBuku.getTahun()) - 1);
        log.info("Kode Tahun Buku Sebelum: {}", kodeTahunBukuSebelum);

        Optional<TahunBuku> tahunBukuSebelumOptional = tahunBukuRepository.findByTahun(kodeTahunBukuSebelum);
        TahunBuku tahunBukuSebelum = tahunBukuSebelumOptional.orElseGet(TahunBuku::new);
        log.info("Objek Tahun Buku Sebelum: {}", tahunBukuSebelum);

        Optional<Anggaran> anggaran = repository.getRealisasiBerjalan(idRekening, kodeTahunBuku);
//        log.info("Realisasi Berjalan: {}", anggaran.get().getRealisasiBerjalan());
        if (anggaran.isPresent() && anggaran.get().getRealisasiBerjalan() != null)
            return anggaran.get().getRealisasiBerjalan();
        else
            return BigDecimal.ZERO;
    }

    public PengaturanSistem findParamByKodeTahunBuku(String kodeTahunBuku) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemRepository.findByKodeTahunBuku(kodeTahunBuku);
        return pengaturanSistem.orElse(null);
    }

    @Transactional
    public void updateSaldoCurrent(Anggaran anggaran) {
        Optional<SaldoCurrent> optionalSaldoCurrent = saldoCurrentRepository.findByIdRekening(anggaran.getIdRekening().getIdRekening());
        if (optionalSaldoCurrent.isPresent()) {
            SaldoCurrent saldoCurrent = optionalSaldoCurrent.get();
            BigDecimal serap = saldoCurrent.getSerapTambah().subtract(saldoCurrent.getSerapKurang());
            BigDecimal saldoAnggaran = (anggaran.getTotalAnggaran().add(serap)).subtract(saldoCurrent.getSaldoDebet());
            saldoCurrent.setSaldoAnggaran(saldoAnggaran);
            saldoCurrentRepository.save(saldoCurrent);
        }

        Optional<SaldoFA> optionalSaldoFA = saldoFARepository.findById(anggaran.getIdRekening().getIdRekening());
        if (optionalSaldoFA.isPresent()) {
            SaldoFA saldoFA = optionalSaldoFA.get();
            BigDecimal serap = saldoFA.getSerapTambah().subtract(saldoFA.getSerapKurang());
            BigDecimal saldoAnggaran = (anggaran.getTotalAnggaran().add(serap)).subtract(saldoFA.getSaldoDebet());
            saldoFA.setSaldoAnggaran(saldoAnggaran);
            saldoFARepository.save(saldoFA);
        }

        Optional<SaldoPA> optionalSaldoPA = saldoPARepository.findById(anggaran.getIdRekening().getIdRekening());
        if (optionalSaldoPA.isPresent()) {
            SaldoPA saldoPA = optionalSaldoPA.get();
            BigDecimal serap = saldoPA.getSerapTambah().subtract(saldoPA.getSerapKurang());
            BigDecimal saldoAnggaran = (anggaran.getTotalAnggaran().add(serap)).subtract(saldoPA.getSaldoDebet());
            saldoPA.setSaldoAnggaran(saldoAnggaran);
            saldoPARepository.save(saldoPA);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateSaldoCurrentPAFA(Rekening idRekening, BigDecimal totalAnggaran) {
        if (idRekening.getTipeRekening().equals("PENDAPATAN")) {
            saldoCurrentRepository.updateAnggaranPendapatan(idRekening.getIdRekening(), totalAnggaran);
            saldoPARepository.updateAnggaranPendapatan(idRekening.getIdRekening(), totalAnggaran);
            saldoFARepository.updateAnggaranPendapatan(idRekening.getIdRekening(), totalAnggaran);
        } else {
            saldoCurrentRepository.updateAnggaranBiaya(idRekening.getIdRekening(), totalAnggaran);
            saldoPARepository.updateAnggaranBiaya(idRekening.getIdRekening(), totalAnggaran);
            saldoFARepository.updateAnggaranBiaya(idRekening.getIdRekening(), totalAnggaran);
        }
    }

    public Optional<Anggaran> findByIdRekening(Integer idRekening) {
        try {
            return anggaranDao.findByIdRekening(idRekening);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Anggaran> findByIdRekeningAndIdSatker(Integer idRekening, String idSatker) {
        try {
            return anggaranDao.findByIdRekeningAndIsSatker(idRekening, idSatker);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void updateStatusAktifByRekening(Integer idRekening, String kodeThnBuku) {
        repository.updateStatusAktifByIdRekening(idRekening, kodeThnBuku);
    }

    public Iterable<Anggaran> findAnggaranByIdRekening(Integer idRekening) {
        return repository.findByIdRekeningAndStatusDataNot(new Rekening(idRekening), "APPROVE");
    }
}

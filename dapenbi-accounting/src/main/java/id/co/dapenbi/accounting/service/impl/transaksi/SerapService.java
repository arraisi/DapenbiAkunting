package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dao.transaksi.InformasiSaldoDao;
import id.co.dapenbi.accounting.dto.transaksi.SerapDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.mapper.SerapMapper;
import id.co.dapenbi.accounting.repository.NumberGeneratorRepository;
import id.co.dapenbi.accounting.repository.transaksi.SerapRepository;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class SerapService {

    @Autowired
    private SerapRepository serapRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private SerapDetailService serapDetailService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private NumberGeneratorRepository numberGeneratorRepository;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Serap save(Serap serap) {
        serap.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        serap = serapRepository.save(serap);

        return serap;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Serap update(Serap serap) {
        serap.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        serap = serapRepository.save(serap);

        return serap;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStatusData(Serap serap) {
        String query = "UPDATE \n" +
                "   ACC_SERAP \n" +
                "SET\n" +
                "   STATUS_DATA = :status,\n" +
                "   TGL_VALIDASI = :tglValidasi,\n" +
                "   USER_VALIDASI = :userValidasi,\n" +
                "   KETERANGAN_DEBET = :keteranganDebit,\n" +
                "   KETERANGAN_KREDIT = :keteranganKredit\n" +
//                (serap.getStatusData().equals("REJECT") ? ",   TGL_VALIDASI = ''\n" : ",   TGL_VALIDASI = :tglValidasi\n") +
//                (serap.getStatusData().equals("REJECT") ? ",   USER_VALIDASI = ''\n" : ",   USER_VALIDASI = :userValidasi\n") +
                (serap.getStatusData().equals("REJECT") ? ",   CATATAN_VALIDASI = :catatan\n" : ",   CATATAN_VALIDASI = ''\n") +
                (serap.getStatusData().equals("REJECT") ? ",   UPDATED_DATE = :updatedDate\n" : "") +
                (serap.getStatusData().equals("REJECT") ? ",   UPDATED_BY = :updatedBy\n" : "") +
                "WHERE NO_SERAP = :id\n";

//        log.info("{}", serap.getCatatanValidasi());

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("status", serap.getStatusData());
        map.addValue("id", serap.getNoSerap());
        map.addValue("tglValidasi", serap.getTglValidasi());
        map.addValue("userValidasi", serap.getUserValidasi());
        map.addValue("keteranganDebit", serap.getKeteranganDebet());
        map.addValue("keteranganKredit", serap.getKeteranganKredit());
        if(serap.getStatusData().equals("REJECT")) {
            map.addValue("catatan", serap.getCatatanValidasi());
            map.addValue("updatedDate", Timestamp.valueOf(LocalDateTime.now()));
            map.addValue("updatedBy", serap.getUpdatedBy());
        }
        this.namedParameterJdbcTemplate.update(query, map);
    }

    public Optional<Serap> findById(String id) {
        return serapRepository.findById(id);
    }

    public Optional<SerapDTO.Serap> findByIdDTO(String id) {
        String query = "SELECT \n" +
                "   as1.NO_SERAP             AS noSerap,\n" +
                "   as1.TGL_SERAP            AS tglSerap,\n" +
                "   as1.KODE_THNBUKU         AS kodeTahunBuku,\n" +
                "   as1.KODE_PERIODE         AS kodePeriode,\n" +
                "   as1.KETERANGAN_DEBET     AS keteranganDebet,\n" +
                "   as1.KETERANGAN_KREDIT    AS keteranganKredit,\n" +
                "   as1.TOTAL_TRANSAKSI      AS totalTransaksi,\n" +
                "   as1.TERBILANG            AS terbilang,\n" +
                "   as1.TGL_VALIDASI         AS tglValidasi,\n" +
                "   as1.USER_VALIDASI        AS userValidasi,\n" +
                "   as1.CATATAN_VALIDASI     AS catatanValidasi,\n" +
                "   as1.STATUS_DATA          AS statusData,\n" +
                "   as1.CREATED_BY           AS createdBy,\n" +
                "   as1.CREATED_DATE         AS createdDate,\n" +
                "   as1.UPDATED_BY           AS updatedBy,\n" +
                "   as1.UPDATED_DATE         AS updatedDate\n" +
                "FROM ACC_SERAP as1\n" +
                "WHERE as1.NO_SERAP = :id\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<SerapDTO.Serap>>() {
                @Override
                public Optional<SerapDTO.Serap> mapRow(ResultSet resultSet, int i) throws SQLException {
                    SerapDTO.Serap value = new SerapDTO.Serap();
                    TahunBuku tahunBuku = tahunBukuService.findById(resultSet.getString("kodeTahunBuku")).get();
                    Periode periode = periodeService.findById(resultSet.getString("kodePeriode")).get();
                    value.setNoSerap(resultSet.getString("noSerap"));
                    value.setTglSerap(resultSet.getTimestamp("tglSerap"));
//                    value.setKodeTahunBuku(tahunBuku);
//                    value.setKodePeriode(periode);
                    value.setKeteranganDebet(resultSet.getString("keteranganDebet"));
                    value.setKeteranganKredit(resultSet.getString("keteranganKredit"));
                    value.setTotalTransaksi(resultSet.getBigDecimal("totalTransaksi"));
                    value.setTerbilang(resultSet.getString("terbilang"));
                    value.setTglValidasi(resultSet.getTimestamp("tglValidasi"));
                    value.setUserValidasi(resultSet.getString("userValidasi"));
                    value.setCatatanValidasi(resultSet.getString("catatanValidasi"));
                    value.setStatusData(resultSet.getString("statusData"));
                    value.setCreatedBy(resultSet.getString("createdBy"));
                    value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                    value.setUpdatedBy(resultSet.getString("updatedBy"));
                    value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        serapRepository.deleteById(id);
    }

    public DataTablesOutput<Serap> datatables(DataTablesInput input) {
        return serapRepository.findAll(input);
    }

    public DataTablesResponse<Serap> datatablesService(DataTablesRequest<Serap> params, String search) {
        List<Serap> data = datatables(params, search);
        Long rowCount = datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<Serap> datatables(DataTablesRequest<Serap> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   as1.NO_SERAP             AS noSerap,\n" +
                "   as1.TGL_SERAP            AS tglSerap,\n" +
                "   as1.KODE_THNBUKU         AS kodeTahunBuku,\n" +
                "   as1.KODE_PERIODE         AS kodePeriode,\n" +
                "   as1.KETERANGAN_DEBET     AS keteranganDebet,\n" +
                "   as1.KETERANGAN_KREDIT    AS keteranganKredit,\n" +
                "   as1.TOTAL_TRANSAKSI      AS totalTransaksi,\n" +
                "   as1.TERBILANG            AS terbilang,\n" +
                "   as1.TGL_VALIDASI         AS tglValidasi,\n" +
                "   as1.USER_VALIDASI        AS userValidasi,\n" +
                "   as1.CATATAN_VALIDASI     AS catatanValidasi,\n" +
                "   as1.STATUS_DATA          AS statusData,\n" +
                "   as1.CREATED_BY           AS createdBy,\n" +
                "   as1.CREATED_DATE         AS createdDate,\n" +
                "   as1.UPDATED_BY           AS updatedBy,\n" +
                "   as1.UPDATED_DATE         AS updatedDate\n" +
                "FROM ACC_SERAP as1\n" +
                "JOIN ACC_THNBUKU thb ON thb.KODE_THNBUKU = as1.KODE_THNBUKU\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("as1.NO_SERAP", "as1.NO_SERAP");
        MapSqlParameterSource map = new MapSqlParameterSource();
        SerapDatatablesQueryComparator queryComparator = new SerapDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        log.info("{}", finalQuery);

        List<Serap> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<Serap>() {
            @Override
            public Serap mapRow(ResultSet resultSet, int i) throws SQLException {
                Serap value = new Serap();
                TahunBuku tahunBuku = tahunBukuService.findById(resultSet.getString("kodeTahunBuku")).get();
                value.setNoSerap(resultSet.getString("noSerap"));
                value.setTglSerap(resultSet.getTimestamp("tglSerap"));
                value.setTahunBuku(tahunBuku);
                value.setKodePeriode(resultSet.getString("kodePeriode"));
                value.setKeteranganDebet(resultSet.getString("keteranganDebet"));
                value.setKeteranganKredit(resultSet.getString("keteranganKredit"));
                value.setTotalTransaksi(resultSet.getBigDecimal("totalTransaksi"));
                value.setTglValidasi(resultSet.getTimestamp("tglValidasi"));
                value.setUserValidasi(resultSet.getString("userValidasi"));
                value.setCatatanValidasi(resultSet.getString("catatanValidasi"));
                value.setStatusData(resultSet.getString("statusData"));
                value.setCreatedBy(resultSet.getString("createdBy"));
                value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                value.setUpdatedBy(resultSet.getString("updatedBy"));
                value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                return value;
            }
        });
        return list;
    }

    public Long datatables(Serap params, String search) {
        String baseQuery = "select count(*) as value_row \n" +
                "from ACC_SERAP as1\n" +
                "JOIN ACC_THNBUKU thb ON thb.KODE_THNBUKU = as1.KODE_THNBUKU\n" +
                "where 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        SerapDatatablesQueryComparator queryComparator = new SerapDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class SerapDatatablesQueryComparator implements QueryComparator<Serap> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public SerapDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Serap params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, Serap params) {
            if(!params.getStatusData().isEmpty()) {
                builder.append("AND as1.STATUS_DATA = :statusData\n");
                map.addValue("statusData", params.getStatusData());
            }

            if(!value.isEmpty()) {
                builder.append("and (lower(as1.NO_SERAP) like '%").append(value).append("%'\n")
                        .append("or lower(thb.NAMA_THNBUKU) like '%").append(value).append("%'\n")
                        .append("or lower(as1.KODE_PERIODE) like '%").append(value).append("%'\n")
                        .append("or lower(as1.TGL_SERAP) like '%").append(value).append("%'\n")
                        .append("or lower(as1.KETERANGAN_DEBET) like '%").append(value).append("%'\n")
                        .append("or lower(as1.TOTAL_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(as1.CREATED_DATE) like '%").append(value).append("%'\n")
                        .append("or lower(as1.CREATED_BY) like '%").append(value).append("%'\n")
                        .append("or lower(as1.STATUS_DATA) like '%").append(value).append("%')\n");
            }

            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public DataTablesResponse<Rekening> datatablesRekeningService(DataTablesRequest<Rekening> params, String search) {
        List<Rekening> data = datatablesRekening(params, search);
        Long rowCount = datatablesRekening(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<Rekening> datatablesRekening(DataTablesRequest<Rekening> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   ar.ID_REKENING           AS idRekening,\n" +
                "   ar.LEVEL_REKENING        AS levelrekening,\n" +
                "   ar.KODE_REKENING         AS kodeRekening,\n" +
                "   ar.NAMA_REKENING         AS namaRekening,\n" +
                "   asc1.SALDO_ANGGARAN      AS saldoAnggaran\n" +
                "FROM ACC_REKENING ar \n" +
                "LEFT JOIN ACC_SALDO_CURRENT asc1 ON asc1.ID_REKENING = ar.ID_REKENING \n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("ar.ID_REKENING", "ar.LEVEL_REKENING", "ar.KODE_REKENING", "ar.NAMA_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        RekeningDatatablesQueryComparator queryComparator = new RekeningDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        log.info("{}", finalQuery);

        List<Rekening> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<Rekening>() {
            @Override
            public Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                Rekening value = new Rekening();
                Optional<SaldoCurrent> saldoCurrent = saldoCurrentService.findByIdRekening(resultSet.getInt("idRekening"));
                value.setIdRekening(resultSet.getInt("idRekening"));
                value.setLevelRekening(resultSet.getInt("levelRekening"));
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoCurrent(saldoCurrent.isPresent() ? saldoCurrent.get() : new SaldoCurrent());
                return value;
            }
        });
        return list;
    }

    public Long datatablesRekening(Rekening params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_REKENING ar \n" +
                "LEFT JOIN ACC_SALDO_CURRENT asc1 ON asc1.ID_REKENING = ar.ID_REKENING \n" +
                "WHERE 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        RekeningDatatablesQueryComparator queryComparator = new RekeningDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    public class RekeningDatatablesQueryComparator implements QueryComparator<Rekening> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public RekeningDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Rekening params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, Rekening params) {
            if(!params.getTipeRekening().isEmpty()) {
                builder.append(" and ar.TIPE_REKENING = :tipeRekening\n");
                map.addValue("tipeRekening", params.getTipeRekening());
            }

            if(params.getLevelRekening() != null) {
                builder.append(" and ar.LEVEL_REKENING = :levelRekening\n");
                map.addValue("levelRekening", params.getLevelRekening());
            }

            if (!params.getIsSummary().isEmpty()) {
                builder.append(" and ar.IS_SUMMARY = :isSummary\n");
                map.addValue("isSummary", params.getIsSummary());
            }

            if(!value.isEmpty()) {
                builder.append(" and (lower(ar.ID_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.LEVEL_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(asc1.SALDO_ANGGARAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public Optional<SaldoCurrent> findSaldoCurrentByIdRekening(Integer idRekening) {
        String query = "SELECT \n" +
                "   as2.ID_REKENING         AS idRekening,\n" +
                "   as2.SALDO_AWAL          AS saldoAwal,\n" +
                "   as2.SALDO_DEBET         AS saldoDebet,\n" +
                "   as2.SALDO_KREDIT        AS saldoKredit,\n" +
                "   as2.SALDO_AKHIR         AS saldoAkhir,\n" +
                "   as2.SERAP_TAMBAH        AS serapTambah,\n" +
                "   as2.SERAP_KURANG        AS serapKurang,\n" +
                "   as2.SALDO_ANGGARAN      AS saldoAnggaran\n" +
                "FROM ACC_SALDO as2 \n" +
                "WHERE as2.ID_REKENING = :idRekening\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening);

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<SaldoCurrent>>() {
                @Override
                public Optional<SaldoCurrent> mapRow(ResultSet resultSet, int i) throws SQLException {
                    SaldoCurrent value = new SaldoCurrent();
                    value.setIdRekening(resultSet.getInt("idRekening"));
                    value.setSaldoAwal(resultSet.getBigDecimal("saldoAwal"));
                    value.setSaldoDebet(resultSet.getBigDecimal("saldoDebet"));
                    value.setSaldoKredit(resultSet.getBigDecimal("saldoKredit"));
                    value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir"));
                    value.setSerapTambah(resultSet.getBigDecimal("serapTambah"));
                    value.setSerapKurang(resultSet.getBigDecimal("serapKurang"));
                    value.setSaldoAnggaran(resultSet.getBigDecimal("saldoAnggaran"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String getNoSerap() {
        numberGeneratorRepository.incrementByName("ACC_SERAP");
        Optional<NumberGenerator> numberGenerator = numberGeneratorRepository.findByName("ACC_SERAP");
        BigInteger counter = numberGenerator.get().getGenerateNumber();
        return counter.toString();
    }

    public String generatedNoSerap() {
        String query = "SELECT\n" +
                "   min_a - 1 + LEVEL\n" +
                "FROM(\n" +
                "       SELECT\n" +
                "           min(TO_NUMBER(as2.NO_SERAP))  min_a,\n" +
                "           max(TO_NUMBER(as2.NO_SERAP))  max_a\n" +
                "       FROM ACC_SERAP as2 \n" +
                "   )\n" +
                "CONNECT BY LEVEL <= max_a - min_a + 1\n" +
                "MINUS \n" +
                "SELECT \n" +
                "   TO_NUMBER(as3.NO_SERAP) \n" +
                "FROM ACC_SERAP as3 \n";

        List<String> data1 = this.namedParameterJdbcTemplate.queryForList(query, new HashMap<>(), String.class);
        if(data1.isEmpty()) {
            return getNoSerap();
        } else {
            return data1.get(0);
        }
    }

    public byte[] exportPDF(String jasperName, Map<String, Object> parameter) {
        try {
            PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
            Serap serap = findById(parameter.get("noSerap").toString()).get();
            List<SerapDTO.SerapExport> list = SerapMapper.INSTANCE.mapToSerapExport(serap.getSerapDetail());
//            List<SerapDetail> list = serap.getSerapDetail();
            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameter.put("logoLocation", image);
            parameter.put("keterangan", serap.getKeteranganDebet());
            parameter.put("kepalaDivisi", pengaturanSistem.getKdiv());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, new JRBeanCollectionDataSource(list));
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public byte[] exportEXCEL(String jasperName, Map<String, Object> parameter) {
        try {
            PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
            Serap serap = findById(parameter.get("noSerap").toString()).get();
            List<SerapDTO.SerapExport> list = SerapMapper.INSTANCE.mapToSerapExport(serap.getSerapDetail());

            InputStream jasperStream = this.getClass().getResourceAsStream(jasperName);
            BufferedImage image = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            parameter.put("logoLocation", image);
            parameter.put("keterangan", serap.getKeteranganDebet());
            parameter.put("kepalaDivisi", pengaturanSistem.getKdiv());
            parameter.put(JRParameter.REPORT_LOCALE, Locale.ITALY);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, new JRBeanCollectionDataSource(list));
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

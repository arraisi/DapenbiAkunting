package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class InformasiSaldoDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    public List<SaldoCurrent> datatablesSaldoCurrent(DataTablesRequest<SaldoCurrent> params, String search) {
        //language=Oracle
        String query = "SELECT " +
                "ROW_NUMBER() over (%s) as no,\n" +
                "    asc2.ID_REKENING AS idRekening,\n" +
                "    ar.KODE_REKENING AS koderekening,\n" +
                "    ar.NAMA_REKENING AS namaRekening,\n" +
                "    COALESCE (asc2.NILAI_ANGGARAN, 0) AS nilaiAnggaran,\n" +
                "    COALESCE (asc2.SALDO_AWAL, 0) AS saldoAwal,\n" +
                "    COALESCE (asc2.SALDO_DEBET, 0) AS saldoDebet,\n" +
                "    COALESCE (asc2.SALDO_KREDIT, 0) AS saldoKredit,\n" +
                "    COALESCE (asc2.SALDO_AKHIR, 0) AS saldoAkhir,\n" +
                "    COALESCE (asc2.SERAP_TAMBAH, 0) AS serapTambah,\n" +
                "    COALESCE (asc2.SERAP_KURANG, 0) AS serapKurang,\n" +
                "    COALESCE (asc2.SALDO_ANGGARAN, 0) AS saldoAnggaran,\n" +
                "    asc2.STATUS_DATA AS statusData\n" +
                "FROM ACC_SALDO_CURRENT asc2\n" +
                "    " +
                "JOIN ACC_REKENING ar\n" +
                "ON ar.ID_REKENING = asc2.ID_REKENING\n" +
                "where 1 = 1 \n";

        OrderingByColumns columns = new OrderingByColumns("asc2.ID_REKENING", "ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        SaldoCurrentDatatablesQueryComparator queryComparator = new SaldoCurrentDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());
//        log.info("{}", finalQuery);

        List<SaldoCurrent> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<SaldoCurrent>() {
            @Override
            public SaldoCurrent mapRow(ResultSet resultSet, int i) throws SQLException {
                SaldoCurrent value = new SaldoCurrent();
                value.setIdRekening(resultSet.getInt("idRekening"));
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoAwal(resultSet.getBigDecimal("saldoAwal"));
                value.setSaldoDebet(resultSet.getBigDecimal("saldoDebet"));
                value.setSaldoKredit(resultSet.getBigDecimal("saldoKredit"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir"));
                value.setSerapTambah(resultSet.getBigDecimal("serapTambah"));
                value.setSerapKurang(resultSet.getBigDecimal("serapKurang"));
                value.setSaldoAnggaran(resultSet.getBigDecimal("saldoAnggaran"));
                value.setNilaiAnggaran(resultSet.getBigDecimal("nilaiAnggaran"));

                String statusData = params.getValue().getStatusData();
                Map<String, BigDecimal> totalDebitKredit = getTotalDebitKredit(value.getIdRekening(), statusData);
//                BigDecimal totalDebit = totalDebitWarkat(value.getIdRekening(), statusData);
//                BigDecimal totalKredit = totalKreditWarkat(value.getIdRekening(), statusData);
//                log.info("{}", totalDebitKredit);
//                if (!statusData.isEmpty())
//                    value.setSaldoDebet(value.getSaldoDebet().add(totalDebitKredit.get("debit")));
//                    value.setSaldoKredit(value.getSaldoKredit().add(totalDebitKredit.get("kredit")));
//                    BigDecimal saldoAkhir = value.getSaldoAwal().add(value.getSaldoDebet()).subtract(value.getSaldoKredit());
//                    value.setSaldoAkhir(saldoAkhir);
                return value;
            }
        });

        return list;
    }

    public Long datatablesSaldoCurrent(String search, SaldoCurrent params) {
        String baseQuery = "select count(*) as value_row \n" +
                "from ACC_SALDO_CURRENT asc2\n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = asc2.ID_REKENING\n" +
                "where 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        SaldoCurrentDatatablesQueryComparator queryComparator = new SaldoCurrentDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class SaldoCurrentDatatablesQueryComparator implements QueryComparator<SaldoCurrent> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public SaldoCurrentDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(SaldoCurrent params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, SaldoCurrent params) {
            if(!params.getStatusData().isEmpty()) {
                builder.append("and asc2.STATUS_DATA = :statusData\n");
                map.addValue("statusData", params.getStatusData());
            }
            if(!value.isEmpty()) {
                builder.append("and (lower(ar.KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SALDO_AWAL) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SALDO_DEBET) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SALDO_KREDIT) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SALDO_AKHIR) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SERAP_TAMBAH) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SERAP_KURANG) like '%").append(value).append("%'\n")
                        .append("or lower(asc2.SALDO_ANGGARAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<Saldo> datatablesSaldo(DataTablesRequest<Saldo> params, String search) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (%s) as no,\n" +
                "    as2.ID_REKENING AS idRekening,\n" +
                "    ar.KODE_REKENING AS koderekening,\n" +
                "    ar.NAMA_REKENING AS namaRekening,\n" +
                "    COALESCE (as2.SALDO_AWAL, 0) AS saldoAwal,\n" +
                "    COALESCE (as2.SALDO_DEBET, 0) AS saldoDebet,\n" +
                "    COALESCE (as2.SALDO_KREDIT, 0) AS saldoKredit,\n" +
                "    COALESCE (as2.SALDO_AKHIR, 0) AS saldoAkhir,\n" +
                "    COALESCE (as2.SERAP_TAMBAH, 0) AS serapTambah,\n" +
                "    COALESCE (as2.SERAP_KURANG, 0) AS serapKurang,\n" +
                "    COALESCE (as2.SALDO_ANGGARAN, 0) AS saldoAnggaran,\n" +
                "    COALESCE (as2.NILAI_ANGGARAN, 0) AS nilaiAnggaran\n" +
                "FROM ACC_SALDO as2\n" +
                "    JOIN ACC_REKENING ar\n" +
                "ON ar.ID_REKENING = as2.ID_REKENING\n" +
                "WHERE 1 = 1 \n";

        OrderingByColumns columns = new OrderingByColumns("asc2.ID_REKENING", "ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        SaldoDatatablesQueryComparator queryComparator = new SaldoDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());
//        log.info("{}", finalQuery);

        List<Saldo> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<Saldo>() {
            @Override
            public Saldo mapRow(ResultSet resultSet, int i) throws SQLException {
                Saldo value = new Saldo();
                value.setIdRekening(resultSet.getInt("idRekening"));
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoAwal(resultSet.getBigDecimal("saldoAwal"));
                value.setSaldoDebet(resultSet.getBigDecimal("saldoDebet"));
                value.setSaldoKredit(resultSet.getBigDecimal("saldoKredit"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir"));
                value.setSerapTambah(resultSet.getBigDecimal("serapTambah"));
                value.setSerapKurang(resultSet.getBigDecimal("serapKurang"));
                value.setSaldoAnggaran(resultSet.getBigDecimal("saldoAnggaran"));
                value.setNilaiAnggaran(resultSet.getBigDecimal("nilaiAnggaran"));
                return value;
            }
        });

        return list;
    }

    public Long datatablesSaldo(String search, Saldo params) {
        String baseQuery = "select count(*) as value_row \n" +
                "from ACC_SALDO as2\n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = as2.ID_REKENING\n" +
                "where 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        SaldoDatatablesQueryComparator queryComparator = new SaldoDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class SaldoDatatablesQueryComparator implements QueryComparator<Saldo> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public SaldoDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Saldo params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, Saldo params) {
            if(!params.getKodeDri().isEmpty()) {
                builder.append("AND as2.KODE_DRI = :kodeDRI\n");
                map.addValue("kodeDRI", params.getKodeDri());
            }

            if(!params.getTglSaldo().equals(null)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                builder.append("AND TRUNC(as2.TGL_SALDO) = TO_DATE(:tglSaldo, 'YYYY-MM-DD')\n");
                map.addValue("tglSaldo", formatter.format(params.getTglSaldo()));
//                log.info("{}", formatter.format(params.getTglSaldo()));
            }

            if(!value.isEmpty()) {
                builder.append("and (lower(ar.KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SALDO_AWAL) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SALDO_DEBET) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SALDO_KREDIT) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SALDO_AKHIR) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SERAP_TAMBAH) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SERAP_KURANG) like '%").append(value).append("%'\n")
                        .append("or lower(as2.SALDO_ANGGARAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public BigDecimal saldoAsetSaldo(String kodeDRI, String tglSaldo) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT SUM(asc2.SALDO_DEBET ) FROM ACC_SALDO asc2 WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!kodeDRI.isEmpty()){
            query.append(" AND asc2.KODE_DRI = :KODE_DRI ");
            params.addValue("KODE_DRI", kodeDRI);
        }
        if (!tglSaldo.isEmpty()){
            query.append(" AND TRUNC(asc2.TGL_SALDO) = TO_DATE(:TGL_SALDO, 'YYYY-MM-DD') ");
            params.addValue("TGL_SALDO", tglSaldo);
        }
        return this.namedParameterJdbcTemplate.queryForObject(query.toString(), params, BigDecimal.class);
    }

    public BigDecimal saldoKewajibanSaldo(String kodeDRI, String tglSaldo) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT SUM(asc2.SALDO_KREDIT ) FROM ACC_SALDO asc2 WHERE 1=1 ");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (!kodeDRI.isEmpty()){
            query.append(" AND asc2.KODE_DRI = :KODE_DRI ");
            params.addValue("KODE_DRI", kodeDRI);
        }
        if (!tglSaldo.isEmpty()){
            query.append(" AND TRUNC(asc2.TGL_SALDO) = TO_DATE(:TGL_SALDO, 'YYYY-MM-DD') ");
            params.addValue("TGL_SALDO", tglSaldo);
        }
        return this.namedParameterJdbcTemplate.queryForObject(query.toString(), params, BigDecimal.class);
    }

    public BigDecimal saldoAsetSaldoCurrent() {
        String query = "SELECT SUM(asc2.SALDO_DEBET )\n" +
                "FROM ACC_SALDO_CURRENT asc2 \n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING  = asc2.ID_REKENING";
        return this.namedParameterJdbcTemplate.queryForObject(query, new HashMap<>(), BigDecimal.class);
    }

    public BigDecimal saldoKewajibanSaldoCurrent() {
        String query = "SELECT SUM(asc2.SALDO_KREDIT )\n" +
                "FROM ACC_SALDO_CURRENT asc2 \n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING  = asc2.ID_REKENING";

        return this.namedParameterJdbcTemplate.queryForObject(query, new HashMap<>(), BigDecimal.class);
    }

    public BigDecimal totalDebitWarkat(Integer idRekening, String statusData) {
        String query = "SELECT \n" +
                "\tSUM(awj.JUMLAH_DEBIT) \n" +
                "FROM ACC_WARKAT aw \n" +
                "JOIN ACC_WARKAT_JURNAL awj ON awj.NO_WARKAT = aw.NO_WARKAT \n" +
                "WHERE (aw.STATUS_DATA = :status1 OR aw.STATUS_DATA = :status2)\n" +
                "AND TO_CHAR(aw.TGL_TRANSAKSI, 'yyyy-MM-dd') = :today\n" +
                "AND aw.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND awj.ID_REKENING = :idRekening\n";
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Optional<PengaturanSistemDTO> pengaturanSistemDTO = pengaturanSistemService.findByCreatedDate();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening);
        map.addValue("status1", statusData);
        map.addValue("status2", statusData.equals("PA") ? "FA" : "");
        map.addValue("today", new Date(today.getTime()).toString());
        map.addValue("kodeTahunBuku", pengaturanSistemDTO.get().getKodeTahunBuku().getKodeTahunBuku());
        BigDecimal result = namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);
//        log.info("{}", result);
        if (result == null)
            return new BigDecimal(0);
        else
            return result;
    }

    public BigDecimal totalKreditWarkat(Integer idRekening, String statusData) {
        String query = "SELECT \n" +
                "\tSUM(awj.JUMLAH_KREDIT) \n" +
                "FROM ACC_WARKAT aw \n" +
                "JOIN ACC_WARKAT_JURNAL awj ON awj.NO_WARKAT = aw.NO_WARKAT \n" +
                "WHERE (aw.STATUS_DATA = :status1 OR aw.STATUS_DATA = :status2)\n" +
                "AND TO_CHAR(aw.TGL_TRANSAKSI, 'yyyy-MM-dd') = :today\n" +
                "AND aw.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND awj.ID_REKENING = :idRekening\n";
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Optional<PengaturanSistemDTO> pengaturanSistemDTO = pengaturanSistemService.findByCreatedDate();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening);
        map.addValue("status1", statusData);
        map.addValue("status2", statusData.equals("PA") ? "FA" : "");
        map.addValue("today", new Date(today.getTime()).toString());
        map.addValue("kodeTahunBuku", pengaturanSistemDTO.get().getKodeTahunBuku().getKodeTahunBuku());
        BigDecimal result =  namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);
        if (result == null)
            return new BigDecimal(0);
        else
            return result;
    }

    public Map<String, BigDecimal> getTotalDebitKredit(Integer idRekening, String statusData) {
        String query = "SELECT \n" +
                "   COALESCE (SUM(COALESCE (awj.JUMLAH_DEBIT, 0)), 0)        AS totalDebit, \n" +
                "   COALESCE (SUM(COALESCE (awj.JUMLAH_KREDIT, 0)), 0)\t   AS totalKredit \n" +
                "FROM ACC_WARKAT aw \n" +
                "JOIN ACC_WARKAT_JURNAL awj ON awj.NO_WARKAT = aw.NO_WARKAT \n" +
                "WHERE (aw.STATUS_DATA = :status1 OR aw.STATUS_DATA = :status2)\n" +
                "AND TO_CHAR(aw.TGL_TRANSAKSI, 'yyyy-MM-dd') = :today\n" +
                "AND aw.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND awj.ID_REKENING = :idRekening\n";
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Optional<PengaturanSistemDTO> pengaturanSistemDTO = pengaturanSistemService.findByCreatedDate();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening);
        map.addValue("status1", statusData);
        map.addValue("status2", statusData.equals("PA") ? "FA" : "");
        map.addValue("today", new Date(today.getTime()).toString());
        map.addValue("kodeTahunBuku", pengaturanSistemDTO.map(v -> v.getKodeTahunBuku().getKodeTahunBuku()).orElse(""));
        Map<String, BigDecimal> hashMap = new HashMap<>();
        try {
            hashMap = namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Map<String, BigDecimal>>() {
                @Override
                public Map<String, BigDecimal> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Map<String, BigDecimal> mapper = new HashMap<>();
                    mapper.put("debit", resultSet.getBigDecimal("totalDebit"));
                    mapper.put("kredit", resultSet.getBigDecimal("totalKredit"));
                    return mapper;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            log.error("Empty Result", e);
            hashMap.put("debit", BigDecimal.ZERO);
            hashMap.put("kredit", BigDecimal.ZERO);

        }
        return hashMap;
    }

    public Map<String, BigDecimal> totalAsetKewajibanSaldoWarkat(String tableName, String kodeDRI, String tglTransaksi) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        String queryDebit = "SELECT\n" +
                "   SUM(COALESCE(asc2.SALDO_AKHIR, 0))\n" +
                "FROM %s asc2 \n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING  = asc2.ID_REKENING\n" +
                "WHERE ar.SALDO_NORMAL = 'D'\n";

        StringBuilder builderDebit = new StringBuilder(String.format(queryDebit, tableName));
        if (!tglTransaksi.isEmpty()) {
            builderDebit.append("AND TO_CHAR(asc2.TGL_SALDO, 'yyyy-MM-dd') = :tglTransaksi\n");
            map.addValue("tglTransaksi", tglTransaksi);
        }

        if (!kodeDRI.isEmpty()) {
            builderDebit.append("AND asc2.KODE_DRI = :kodeDRI\n");
            map.addValue("kodeDRI", kodeDRI);
        }

        BigDecimal totalAset = this.namedParameterJdbcTemplate.queryForObject(builderDebit.toString(), map, BigDecimal.class);
        totalAset = totalAset == null ? BigDecimal.ZERO : totalAset;

        String queryKredit = "SELECT \n" +
                "   SUM(COALESCE(asc2.SALDO_AKHIR, 0))\n" +
                "FROM %s asc2 \n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING  = asc2.ID_REKENING\n" +
                "WHERE ar.SALDO_NORMAL = 'K'\n";

        StringBuilder builderKredit = new StringBuilder(String.format(queryKredit, tableName));
        if (!tglTransaksi.isEmpty()) {
            builderKredit.append("AND TO_CHAR(asc2.TGL_SALDO, 'yyyy-MM-dd') = :tglTransaksi\n");
//            map.addValue("tglTransaksi", tglTransaksi);
        }

        if (!kodeDRI.isEmpty()) {
            builderKredit.append("AND asc2.KODE_DRI = :kodeDRI\n");
//            map.addValue("kodeDRI", kodeDRI);
        }

        BigDecimal totalKewajiban = this.namedParameterJdbcTemplate.queryForObject(builderKredit.toString(), map, BigDecimal.class);
        totalKewajiban = totalKewajiban == null ? BigDecimal.ZERO : totalKewajiban;

        log.info("Total Aset: {}", totalAset);
        log.info("Total Kewajiban: {}", totalKewajiban);
        BigDecimal selisih = totalAset.subtract(totalKewajiban);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("totalAset", totalAset);
        result.put("totalKewajiban", totalKewajiban);
        result.put("selisih", selisih);
        return result;
    }
}

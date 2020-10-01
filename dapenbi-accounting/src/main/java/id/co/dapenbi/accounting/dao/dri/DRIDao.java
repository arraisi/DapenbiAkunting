package id.co.dapenbi.accounting.dao.dri;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.dri.DRIDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class DRIDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DRIDTO.DRISementara> sementara(DataTablesRequest<DRIDTO.DRISementara> params, String search) {
        String query = "select \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   ar.ID_REKENING      as idRekening ,\n" +
                "   ar.KODE_REKENING    as kodeRekening ,\n" +
                "   ar.NAMA_REKENING    as namaRekening,\n" +
                "   ar.LEVEL_REKENING   as levelRekening,\n" +
                "   ar.IS_SUMMARY       as isSummary,\n" +
                "   ar.SALDO_NORMAL     as saldoNormal,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           sum(COALESCE(asf.SALDO_AKHIR, 0))\n" +
                "       FROM ACC_SALDO_FA asf \n" +
                "       WHERE asf.KODE_REKENING LIKE ar.KODE_REKENING||'%s'\n" +
                "   )                   as saldoAkhir\n" +
                "FROM ACC_REKENING ar\n" +
                "WHERE 1 = 1\n" +
                "AND ar.LEVEL_REKENING in (1,3,6)\n";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING", "ar.NAMA_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        SementaraDatatablesQueryComparator queryComparator = new SementaraDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder()), '%'), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<DRIDTO.DRISementara> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<DRIDTO.DRISementara>() {
            @Override
            public DRIDTO.DRISementara mapRow(ResultSet resultSet, int i) throws SQLException {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                value.setIdRekening(resultSet.getInt("idRekening"));
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setLevelRekening(resultSet.getInt("levelRekening"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir"));
                value.setIsSummary(resultSet.getString("isSummary"));
                value.setSaldoNormal(resultSet.getString("saldoNormal"));
                value.setSaldoAkhirFormatted(resultSet.getBigDecimal("saldoAkhir"));
                return value;
            }
        });

        return list;
    }

    public Long sementara(String search, DRIDTO.DRISementara params) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_REKENING ar\n" +
                "where 1 = 1\n" +
                "AND ar.LEVEL_REKENING < 4\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        SementaraDatatablesQueryComparator queryComparator = new SementaraDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class SementaraDatatablesQueryComparator implements QueryComparator<DRIDTO.DRISementara> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public SementaraDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(DRIDTO.DRISementara params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, DRIDTO.DRISementara params) {
//            if (!params.getTglPeriode().isEmpty()) {
//                builder.append("and TO_CHAR(asc2.UPDATED_DATE, 'yyyy-MM-dd') = :tglPeriode\n");
//                map.addValue("tglPeriode", params.getTglPeriode());
//            }

            if(!params.getKodeRekening().isEmpty()) {
                builder.append("and ar.KODE_REKENING like '").append(params.getKodeRekening()).append("%'\n");
//                map.addValue("idRekening", params.getIdRekening());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(ar.KODE_REKENING) like '").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%')\n");
            }

            if (!params.getSaldoNormal().isEmpty()) {
                builder.append("and ar.SALDO_NORMAL = :saldoNormal\n");
                map.addValue("saldoNormal", params.getSaldoNormal());
            }

            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<DRIDTO.DRISementara> exportSementara(String saldoNormal) {
        String query = "select \n" +
                "   ar.ID_REKENING      as idRekening ,\n" +
                "   ar.KODE_REKENING    as kodeRekening ,\n" +
                "   ar.NAMA_REKENING    as namaRekening,\n" +
                "   ar.LEVEL_REKENING   as levelRekening,\n" +
                "   ar.IS_SUMMARY       as isSummary,\n" +
                "   ar.SALDO_NORMAL     as saldoNormal,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           sum(COALESCE(asf.SALDO_AKHIR, 0))\n" +
                "       FROM ACC_SALDO_FA asf \n" +
                "       WHERE asf.KODE_REKENING LIKE ar.KODE_REKENING||'%s'\n" +
                "   )                   as saldoAkhir\n" +
                "FROM ACC_REKENING ar\n" +
                "WHERE 1 = 1\n" +
                "AND ar.LEVEL_REKENING in (1,3,6)\n" +
                "AND ar.SALDO_NORMAL = :saldoNormal\n" +
                "order by ar.KODE_REKENING asc\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("saldoNormal", saldoNormal);

        return this.namedParameterJdbcTemplate.query(String.format(query, "%"), map, new RowMapper<DRIDTO.DRISementara>() {
            @Override
            public DRIDTO.DRISementara mapRow(ResultSet resultSet, int i) throws SQLException {
                DRIDTO.DRISementara value = new DRIDTO.DRISementara();
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir"));
                value.setSaldoAkhirFormatted(resultSet.getBigDecimal("saldoAkhir"));
//                value.setSaldoAkhirString(value.getSaldoAkhir().toString());
                value.setLevelRekening(resultSet.getInt("levelRekening"));
                value.setCetakTebal("");
                return value;
            }
        });
    }

    public List<DRIDTO.DRI> dri(DataTablesRequest<DRIDTO.DRI> params, String search) {
        String query = "select \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   ar.ID_REKENING      as idRekening ,\n" +
                "   ar.KODE_REKENING    as kodeRekening ,\n" +
                "   ar.NAMA_REKENING    as namaRekening,\n" +
                "   ar.LEVEL_REKENING   as levelRekening,\n" +
                "   ar.IS_SUMMARY       as isSummary,\n" +
                "   ar.SALDO_NORMAL     as saldoNormal,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           sum(COALESCE(asf.SALDO_AKHIR, 0))\n" +
                "       FROM ACC_SALDO asf \n" +
                "       WHERE asf.KODE_REKENING LIKE ar.KODE_REKENING||'%s'\n" +
                "       and asf.KODE_DRI = :kodeDRI\n" +
                "       and TO_CHAR(asf.TGL_SALDO, 'yyyy-MM-dd') = :tglPeriode\n" +
                "   )                   as saldoAkhir\n" +
                "FROM ACC_REKENING ar\n" +
                "WHERE 1 = 1\n" +
                "AND ar.LEVEL_REKENING in (1,3,6)\n";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING", "ar.NAMA_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getValue().getKodeDRI());
        map.addValue("tglPeriode", params.getValue().getTglPeriode());
//        log.info("Testing KodeDRI: {}, Testing Tanggal Transaksi: {}", params.getValue().getKodeDRI(), params.getValue().getTglPeriode());

        DRIDatatablesQueryComparator queryComparator = new DRIDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder()), '%'), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());
//        log.info("Testing finalQuery: {}", finalQuery);

        List<DRIDTO.DRI> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<DRIDTO.DRI>() {
            @Override
            public DRIDTO.DRI mapRow(ResultSet resultSet, int i) throws SQLException {
                DRIDTO.DRI value = new DRIDTO.DRI();
                value.setIdRekening(resultSet.getInt("idRekening"));
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir") == null ? BigDecimal.ZERO : resultSet.getBigDecimal("saldoAkhir"));
                value.setSaldoNormal(resultSet.getString("saldoNormal"));
                value.setSaldoAkhirFormatted(resultSet.getBigDecimal("saldoAkhir"));
                value.setLevelRekening(resultSet.getInt("levelRekening"));
                return value;
            }
        });
//        log.info("Data Datatable DRI: {}", list);

        return list;
    }

    public Long dri(DRIDTO.DRI params, String search) {
        String query = "select count(*) as value_row\n" +
                "FROM ACC_REKENING ar \n" +
                "where 1 = 1\n" +
                "AND ar.LEVEL_REKENING < 4\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        DRIDatatablesQueryComparator queryComparator = new DRIDatatablesQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class DRIDatatablesQueryComparator implements QueryComparator<DRIDTO.DRI> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DRIDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(DRIDTO.DRI params) {
            return null;
        }

        public StringBuilder getQuerySearch(DRIDTO.DRI params, String value) {
            if(!params.getKodeRekening().isEmpty()) {
                builder.append("and ar.KODE_REKENING like '").append(params.getKodeRekening()).append("%'\n");
//                map.addValue("idRekening", params.getIdRekening());
            }

            if(!value.isEmpty()) {
                builder.append("and (lower(ar.KODE_REKENING) like '").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%')\n");
            }

            if (!params.getSaldoNormal().isEmpty()) {
                builder.append("and ar.SALDO_NORMAL = :saldoNormal\n");
                map.addValue("saldoNormal", params.getSaldoNormal());
            }

            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<DRIDTO.DRI> exportDRI(String saldoNormal, String kodeDRI, String tglTransaksi) {
        String query = "select \n" +
                "   ar.ID_REKENING      as idRekening ,\n" +
                "   ar.KODE_REKENING    as kodeRekening ,\n" +
                "   ar.NAMA_REKENING    as namaRekening,\n" +
                "   ar.LEVEL_REKENING   as levelRekening,\n" +
                "   ar.IS_SUMMARY       as isSummary,\n" +
                "   ar.SALDO_NORMAL     as saldoNormal,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           sum(COALESCE(asf.SALDO_AKHIR, 0))\n" +
                "       FROM ACC_SALDO asf \n" +
                "       WHERE asf.KODE_REKENING LIKE ar.KODE_REKENING||'%s'\n" +
                "       and asf.KODE_DRI = :kodeDRI\n" +
                "       and TO_CHAR(asf.TGL_SALDO, 'yyyy-MM-dd') = :tglPeriode\n" +
                "   )                   as saldoAkhir\n" +
                "FROM ACC_REKENING ar\n" +
                "WHERE 1 = 1\n" +
                "AND ar.LEVEL_REKENING in (1,3,6)\n" +
                "AND ar.SALDO_NORMAL = :saldoNormal\n" +
                "order by ar.KODE_REKENING asc\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("saldoNormal", saldoNormal);
        map.addValue("kodeDRI", kodeDRI);
        map.addValue("tglPeriode", tglTransaksi);

        return this.namedParameterJdbcTemplate.query(String.format(query, "%"), map, new RowMapper<DRIDTO.DRI>() {
            @Override
            public DRIDTO.DRI mapRow(ResultSet resultSet, int i) throws SQLException {
                DRIDTO.DRI value = new DRIDTO.DRI();
                value.setKodeRekening(resultSet.getString("kodeRekening"));
                value.setNamaRekening(resultSet.getString("namaRekening"));
                value.setSaldoAkhir(resultSet.getBigDecimal("saldoAkhir") == null ? BigDecimal.ZERO : resultSet.getBigDecimal("saldoAkhir"));
                value.setSaldoAkhirFormatted(resultSet.getBigDecimal("saldoAkhir"));
                value.setLevelRekening(resultSet.getInt("levelRekening"));
                value.setCetakTebal("");
                return value;
            }
        });
    }
}

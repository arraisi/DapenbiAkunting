package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.entity.laporan.LaporanRencanaBisnis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LaporanRencanaBisnisDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<LaporanRencanaBisnis> datatables(DataTablesRequest<LaporanRencanaBisnis> params, String search) {
        String query = "SELECT\n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   rb.*\n" +
                "FROM LAP_RB rb\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("TO_NUMBER(rb.URUTAN)");
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRencanaBisnisQueryComparator queryComparator = new LaporanRencanaBisnisQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<LaporanRencanaBisnis> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanRencanaBisnis>() {
            @Override
            public LaporanRencanaBisnis mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanRencanaBisnis value = new LaporanRencanaBisnis();
                value.setIdLaporanHdr(resultSet.getInt("ID_LAPORAN_HDR"));
                value.setUraian(resultSet.getString("URAIAN"));
                value.setSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                value.setSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                value.setUrutan(resultSet.getString("URUTAN"));
                value.setCetakGaris(resultSet.getString("CETAK_GARIS"));
                value.setCetakMiring(resultSet.getString("CETAK_MIRING"));
                value.setCetakTebal(resultSet.getString("CETAK_TEBAL"));
                value.setLevelAkun(resultSet.getInt("LEVEL_AKUN"));
//                value.setWarna("#000000");
                return value;
            }
        });
        return list;
    }

    public Long datatables(LaporanRencanaBisnis params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM LAP_RB rb\n" +
                "WHERE 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRencanaBisnisQueryComparator queryComparator = new LaporanRencanaBisnisQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class LaporanRencanaBisnisQueryComparator implements QueryComparator<LaporanRencanaBisnis> {
        private StringBuilder builder;
        private MapSqlParameterSource map;

        public LaporanRencanaBisnisQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        public StringBuilder getQuerySearch(LaporanRencanaBisnis params, String value) {
            if(params.getIdLaporanHdr() != null) {
                builder.append("and rb.ID_LAPORAN_HDR = :idLaporanHDR\n");
                map.addValue("idLaporanHDR", params.getIdLaporanHdr());
            }

            if (!params.getKodeThnBuku().isEmpty()) {
                builder.append("and rb.KODE_THNBUKU = :kodeTahunBuku\n");
                map.addValue("kodeTahunBuku", params.getKodeThnBuku());
            }

            if (!params.getKodePeriode().isEmpty()) {
                builder.append("and rb.KODE_PERIODE = :kodePeriode\n");
                map.addValue("kodePeriode", params.getKodePeriode());
            }

            if (!params.getKodeDRI().isEmpty()) {
                builder.append("and rb.KODE_DRI = :kodeDRI\n");
                map.addValue("kodeDRI", params.getKodeDRI());
            }
            return builder;
        }

        @Override
        public StringBuilder getQuery(LaporanRencanaBisnis params) {
            return null;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

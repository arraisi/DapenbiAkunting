package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dao.transaksi.InformasiSaldoDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanSPIDTO;
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
import java.util.Map;

@Repository
public class LaporanSPIDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<LaporanSPIDTO> datatables(DataTablesRequest<LaporanSPIDTO> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s)  as no,\n" +
                "   b.KETERANGAN_SPI        as keterangan,\n" +
                "   a.NILAI_PEROLEHAN       as nilaiPerolehan,\n" +
                "   a.NILAI_WAJAR           as nilaiWajar,\n" +
                "   a.NILAI_SPI             as nilaiSPI \n" +
                "FROM ACC_SPI_DTL a,\n" +
                "ACC_INVESTASI_DTL b\n" +
                "WHERE a.ID_SPI = b.ID_SPI AND a.ID_INVESTASI = b.ID_INVESTASI\n";

        OrderingByColumns columns = new OrderingByColumns("a.ID_SPI_DTL", "b.KETERANGAN_SPI", "b.ID_INVESTASI, b.ID_SPI");
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanSPIDTO>() {
            @Override
            public LaporanSPIDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanSPIDTO value = new LaporanSPIDTO();
                value.setKeterangan(resultSet.getString("keterangan"));
                value.setNilaiPerolehan(resultSet.getBigDecimal("nilaiPerolehan"));
                value.setNilaiWajar(resultSet.getBigDecimal("nilaiWajar"));
                value.setNilaiSPI(resultSet.getBigDecimal("nilaiSPI"));
                return value;
            }
        });
    }

    public Long datatables(LaporanSPIDTO params, String search) {
        String baseQuery = "select count(*) as value_row \n" +
                "FROM ACC_SPI_DTL a,\n" +
                "ACC_INVESTASI_DTL b\n" +
                "WHERE a.ID_SPI = b.ID_SPI AND a.ID_INVESTASI = b.ID_INVESTASI AND a.ID_SPI_HDR = 2\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        return this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
    }

    private static class DatatablesQueryComparator implements QueryComparator<LaporanSPIDTO> {
        private final StringBuilder builder;
        private final MapSqlParameterSource map;

        public DatatablesQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        @Override
        public StringBuilder getQuery(LaporanSPIDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(LaporanSPIDTO params, String value) {
            if (params.getIdSPIHDR() != null) {
                builder.append("AND a.ID_SPI_HDR = :kodeSPIHDR\n");
                map.addValue("kodeSPIHDR", params.getIdSPIHDR());
            }

            if(!value.isEmpty()) {
                builder.append("and (lower(b.KETERANGAN_SPI) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public Map<String, BigDecimal> sumNilaiPerolehanAndNilaiWajarAndNilaiSPIByIdHDR(Integer idHDR) {
        String query = "SELECT \n" +
                "   SUM(a.NILAI_PEROLEHAN) AS totalNilaiPerolehan,\n" +
                "   SUM(a.NILAI_WAJAR) AS totalNilaiWajar,\n" +
                "   SUM(a.NILAI_SPI) AS totalNilaiSPI \n" +
                "FROM ACC_SPI_DTL a,\n" +
                "ACC_INVESTASI_DTL b\n" +
                "WHERE a.ID_SPI = b.ID_SPI AND a.ID_INVESTASI = b.ID_INVESTASI AND a.ID_SPI_HDR = :idHDR\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idHDR", idHDR);

        return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Map<String, BigDecimal>>() {
            @Override
            public Map<String, BigDecimal> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, BigDecimal> value = new HashMap<>();
                value.put("totalNilaiPerolehan", resultSet.getBigDecimal("totalNilaiPerolehan"));
                value.put("totalNilaiWajar", resultSet.getBigDecimal("totalNilaiWajar"));
                value.put("totalNilaiSPI", resultSet.getBigDecimal("totalNilaiSPI"));
                return value;
            }
        });
    }
}

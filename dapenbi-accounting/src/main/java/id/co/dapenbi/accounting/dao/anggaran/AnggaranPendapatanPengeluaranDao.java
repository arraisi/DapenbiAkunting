package id.co.dapenbi.accounting.dao.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.anggaran.AnggaranPendapatanPengeluaranDto;
import id.co.dapenbi.accounting.dto.laporan.LaporanBudgetReviewDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class AnggaranPendapatanPengeluaranDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public class pendapatanMapper implements RowMapper<AnggaranPendapatanPengeluaranDto.Pendapatan> {
        public AnggaranPendapatanPengeluaranDto.Pendapatan mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            AnggaranPendapatanPengeluaranDto.Pendapatan value = new AnggaranPendapatanPengeluaranDto.Pendapatan();
            value.setSandiMaPendapatan(resultSet.getString("sandiMaPendapatan"));
            value.setPendapatan(resultSet.getString("pendapatan"));
            value.setJumlahPendapatan(resultSet.getBigDecimal("jumlahPendapatan"));
            return value;
        }
    }

    public class pengeluaranMapper implements RowMapper<AnggaranPendapatanPengeluaranDto.Pengeluaran> {
        public AnggaranPendapatanPengeluaranDto.Pengeluaran mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            AnggaranPendapatanPengeluaranDto.Pengeluaran value = new AnggaranPendapatanPengeluaranDto.Pengeluaran();
            value.setSandiMaPengeluaran(resultSet.getString("sandiMaPengeluaran"));
            value.setPengeluaran(resultSet.getString("pengeluaran"));
            value.setJumlahPengeluaran(resultSet.getBigDecimal("jumlahPengeluaran"));
            return value;
        }
    }

    public List<AnggaranPendapatanPengeluaranDto.Pendapatan> datatablesPendapatan(DataTablesRequest<AnggaranPendapatanPengeluaranDto.Request> params) {
        String query = "SELECT\n" +
                "   ar.KODE_REKENING                AS sandiMaPendapatan,\n" +
                "   ar.NAMA_REKENING                AS pendapatan,\n" +
                "   COALESCE(aa.TOTAL_ANGGARAN, 0)  AS jumlahPendapatan \n" +
                "FROM ACC_ANGGARAN aa\n" +
                "   LEFT JOIN ACC_REKENING ar ON aa.ID_REKENING = ar.ID_REKENING\n" +
                "WHERE ar.TIPE_REKENING = 'PENDAPATAN' AND aa.STATUS_AKTIF = '1' ";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        if (params.getLength() == -1)
            return this.namedParameterJdbcTemplate.query(stringBuilder.toString(), map, new pendapatanMapper());

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new pendapatanMapper());
    }

    public Long datatablesPendapatan(AnggaranPendapatanPengeluaranDto.Request params) {
        String baseQuery = "SELECT COUNT(*) AS value_row\n" +
                "FROM ACC_ANGGARAN aa\n" +
                "   LEFT JOIN ACC_REKENING ar ON aa.ID_REKENING = ar.ID_REKENING\n" +
                "WHERE ar.TIPE_REKENING = 'PENDAPATAN' AND aa.STATUS_AKTIF = '1' ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuery(params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    public List<AnggaranPendapatanPengeluaranDto.Pengeluaran> datatablesPengeluaran(DataTablesRequest<AnggaranPendapatanPengeluaranDto.Request> params) {
        String query = "SELECT\n" +
                "   ar.KODE_REKENING                AS sandiMaPengeluaran,\n" +
                "   ar.NAMA_REKENING                AS pengeluaran,\n" +
                "   COALESCE(aa.TOTAL_ANGGARAN, 0)  AS jumlahPengeluaran \n" +
                "FROM ACC_ANGGARAN aa\n" +
                "   LEFT JOIN ACC_REKENING ar ON aa.ID_REKENING = ar.ID_REKENING\n" +
                "WHERE ar.TIPE_REKENING = 'BIAYA' AND aa.STATUS_AKTIF = '1' ";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        if (params.getLength() == -1)
            return this.namedParameterJdbcTemplate.query(stringBuilder.toString(), map, new pengeluaranMapper());

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new pengeluaranMapper());
    }

    public Long datatablesPengeluaran(AnggaranPendapatanPengeluaranDto.Request params) {
        String baseQuery = "SELECT COUNT(*) AS value_row\n" +
                "FROM ACC_ANGGARAN aa\n" +
                "   LEFT JOIN ACC_REKENING ar ON aa.ID_REKENING = ar.ID_REKENING\n" +
                "WHERE ar.TIPE_REKENING = 'BIAYA' AND aa.STATUS_AKTIF = '1' ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuery(params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class DatatablesQueryComparator implements QueryComparator<AnggaranPendapatanPengeluaranDto.Request> {
        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(AnggaranPendapatanPengeluaranDto.Request params) {

            if (!params.getKodeTahunBuku().isEmpty()) {
                builder.append(" AND aa.KODE_THNBUKU = :kodeTahunBuku ");
                map.addValue("kodeTahunBuku", params.getKodeTahunBuku());
            }

            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

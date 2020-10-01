package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
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
public class LaporanBudgetReviewDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public class laporanBudgetReviewMapper implements RowMapper<LaporanBudgetReviewDto.Response> {
        public LaporanBudgetReviewDto.Response mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            LaporanBudgetReviewDto.Response value = new LaporanBudgetReviewDto.Response();
            value.setIdRekening(resultSet.getInt("IDREKENING"));
            value.setLevelRekening(resultSet.getInt("LEVELREKENING"));
            value.setIdRekeningParentLvl2(resultSet.getString("IDREKENINGLVL2"));
            value.setIdRekeningParentLvl3(resultSet.getString("IDREKENINGLVL3"));
            value.setNoMataAnggaran(resultSet.getString("MATAANGGARAN"));
            value.setNamaMataAnggaran(resultSet.getString("NAMAREKENING"));
            value.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARANTAHUNAN"));
            value.setRealisasi(resultSet.getBigDecimal("REALISASI"));
            value.setPersen(resultSet.getFloat("PERSEN"));
            value.setSaldoAnggaranTahunan(resultSet.getBigDecimal("SALDOANGGARANTAHUNAN"));
            value.setKeterangan(resultSet.getString("KETERANGAN"));
            return value;
        }
    }

    public List<LaporanBudgetReviewDto.Response> datatables(DataTablesRequest<LaporanBudgetReviewDto.Request> params) {
        String query = "SELECT\n" +
                "   ROW_NUMBER() over (%s)                  AS no,\n" +
                "   ar.ID_REKENING                          AS IDREKENING,\n" +
                "   ar.LEVEL_REKENING                       AS LEVELREKENING,\n" +
                "   (SELECT\n" +
                "       (SELECT \n" +
                "           (SELECT arlvl3.ID_PARENT FROM ACC_REKENING arlvl3 WHERE arlvl3.ID_REKENING = arlvl4.ID_PARENT) \n" +
                "        FROM ACC_REKENING arlvl4 WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT) \n" +
                "    FROM ACC_REKENING arlvl5 WHERE arlvl5.ID_REKENING = ar.ID_PARENT\n" +
                "   )                                       AS IDREKENINGLVL2,\n" +
                "   (SELECT \n" +
                "       (SELECT arlvl4.ID_PARENT FROM ACC_REKENING arlvl4 WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT) \n" +
                "    FROM ACC_REKENING arlvl5 WHERE arlvl5.ID_REKENING = ar.ID_PARENT\n" +
                "   )                                       AS IDREKENINGLVL3,\n" +
                "   ar.KODE_REKENING                        AS MATAANGGARAN,\n" +
                "   ar.NAMA_REKENING                        AS NAMAREKENING,\n" +
                "   COALESCE(abd.ANGGARAN_TAHUNAN, 0)       AS ANGGARANTAHUNAN,\n" +
                "   COALESCE(abd.REALISASI, 0)              AS REALISASI,\n" +
                "   COALESCE(abd.PERSEN, 0)                 AS PERSEN,\n" +
                "   COALESCE(abd.SALDO, 0)                  AS SALDOANGGARANTAHUNAN,\n" +
                "   abd.KETERANGAN           AS KETERANGAN\n" +
                "FROM ACC_BUDGETREVIEW_DTL abd\n" +
                "   LEFT JOIN ACC_REKENING ar ON abd.ID_REKENING = ar.ID_REKENING\n" +
                "   LEFT JOIN ACC_BUDGETREVIEW ab ON abd.NO_BUDGETREVIEW = ab.NO_BUDGETREVIEW\n" +
                "WHERE 1 = 1 \n";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        if (params.getLength() == -1)
            return this.namedParameterJdbcTemplate.query(stringBuilder.toString(), map, new laporanBudgetReviewMapper());

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new laporanBudgetReviewMapper());
    }

    public Long datatables(LaporanBudgetReviewDto.Request params) {
        String baseQuery = "SELECT COUNT(*) AS value_row\n" +
                "FROM ACC_BUDGETREVIEW_DTL abd\n" +
                "   LEFT JOIN ACC_REKENING ar ON abd.ID_REKENING = ar.ID_REKENING\n" +
                "   LEFT JOIN ACC_BUDGETREVIEW ab ON abd.NO_BUDGETREVIEW = ab.NO_BUDGETREVIEW\n" +
                "WHERE 1 = 1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuery(params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class DatatablesQueryComparator implements QueryComparator<LaporanBudgetReviewDto.Request> {
        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(LaporanBudgetReviewDto.Request params) {

            builder.append("and ab.STATUS_DATA = 'VALID'\n");

            if (!params.getKodeTahunBuku().isEmpty()) {
                builder.append("AND ab.KODE_THNBUKU = :kodeTahunBuku\n");
                map.addValue("kodeTahunBuku", params.getKodeTahunBuku());
            }

            if (!params.getTriwulan().isEmpty()) {
                builder.append("AND ab.TRIWULAN = :triwulan\n");
                map.addValue("triwulan", params.getTriwulan());
            }

            if (!params.getNoBudgetReview().isEmpty()) {
                builder.append("AND ab.NO_BUDGETREVIEW = :noBudgetReview\n");
                map.addValue("noBudgetReview", params.getNoBudgetReview());
            }

            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<String> findListBudgetReviewValid(String kodeTahunBuku, String triwuan) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT\n" +
                "   NO_BUDGETREVIEW\n" +
                "FROM ACC_BUDGETREVIEW\n" +
                "WHERE  STATUS_DATA = 'VALID' AND\n" +
                "       KODE_THNBUKU = :KODE_THNBUKU AND\n" +
                "       TRIWULAN = :TRIWULAN\n");
        String queryFinal = builder.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("KODE_THNBUKU", kodeTahunBuku);
        params.addValue("TRIWULAN", triwuan);
        return namedParameterJdbcTemplate.query(queryFinal, params, (resultSet, i) -> {
            return resultSet.getString("NO_BUDGETREVIEW");
        });
    }
}

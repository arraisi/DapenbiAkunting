package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dao.parameter.RekeningDao;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import id.co.dapenbi.accounting.repository.transaksi.BudgetReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BudgetReviewDetailDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private BudgetReviewRepository budgetReviewRepository;

    @Autowired
    private RekeningRepository rekeningRepository;

    @Autowired
    private RekeningDao rekeningDao;

    public List<BudgetReviewDetailDTO> findByNoBudgetReview(String noBudgetreview) {
        //language=Oracle
        String query = "" +
                "SELECT ID_BUDGETREVIEW_DTL,\n" +
                "       NO_BUDGETREVIEW,\n" +
                "       ID_REKENING,\n" +
                "       ANGGARAN_TAHUNAN,\n" +
                "       REALISASI,\n" +
                "       PERSEN,\n" +
                "       SALDO,\n" +
                "       COALESCE(KETERANGAN, '-') AS KETERANGAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_BUDGETREVIEW_DTL\n" +
                "WHERE NO_BUDGETREVIEW = :NO_BUDGETREVIEW\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("NO_BUDGETREVIEW", noBudgetreview);
        return namedParameterJdbcTemplate.query(query, map, new RowMapper<BudgetReviewDetailDTO>() {
            @Override
            public BudgetReviewDetailDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                BudgetReviewDetailDTO value = new BudgetReviewDetailDTO();

                Optional<Rekening> rekening = rekeningRepository.findByIdRekening(resultSet.getInt("ID_REKENING"));
                Optional<BudgetReview> budgetReview = budgetReviewRepository.findById(resultSet.getString("NO_BUDGETREVIEW"));

                value.setIdBudgetReviewDtl(resultSet.getInt("ID_BUDGETREVIEW_DTL"));
                value.setNoBudgetReview(budgetReview.get());
                if (rekening.isPresent()) value.setIdRekening(rekening.get());
                value.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
                value.setRealisasi(resultSet.getBigDecimal("REALISASI"));
                value.setPersen(resultSet.getFloat("PERSEN"));
                value.setSaldo(resultSet.getBigDecimal("SALDO"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });
    }

    public List<BudgetReviewDetailDTO> dataTableBudgetReviewDetailDTO(DataTablesRequest<BudgetReviewDetailDTO> params, String search) {
        //language=Oracle
        String query = "" +
                "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       ID_BUDGETREVIEW_DTL,\n" +
                "       NO_BUDGETREVIEW,\n" +
                "       ID_REKENING,\n" +
                "       ANGGARAN_TAHUNAN,\n" +
                "       REALISASI,\n" +
                "       PERSEN,\n" +
                "       SALDO,\n" +
                "       KETERANGAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_BUDGETREVIEW_DTL\n" +
                "WHERE 1 = 1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesBudgetReviewDTLQueryComparator queryComparator = new DatatablesBudgetReviewDTLQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("ID_BUDGETREVIEW_DTL", "NO_BUDGETREVIEW");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<BudgetReviewDetailDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<BudgetReviewDetailDTO>() {
            @Override
            public BudgetReviewDetailDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                BudgetReviewDetailDTO value = new BudgetReviewDetailDTO();
                Optional<BudgetReview> budgetReview = budgetReviewRepository.findById(resultSet.getString("NO_BUDGETREVIEW"));
                Rekening rekening = rekeningDao.findById(resultSet.getString("ID_REKENING"));


                value.setIdBudgetReviewDtl(resultSet.getInt("ID_BUDGETREVIEW_DTL"));
                value.setNoBudgetReview(budgetReview.get());
                value.setIdRekening(rekening);
                value.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
                value.setRealisasi(resultSet.getBigDecimal("REALISASI"));
                value.setPersen(resultSet.getFloat("PERSEN"));
                value.setSaldo(resultSet.getBigDecimal("SALDO"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });

        return list;
    }

    public Long dataTableBudgetReviewDetailDTO(String search) {
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW FROM ACC_BUDGETREVIEW_DTL WHERE 1=1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesBudgetReviewDTLQueryComparator queryComparator = new DatatablesBudgetReviewDTLQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("VALUE_ROW"));
        return row;
    }

    private class DatatablesBudgetReviewDTLQueryComparator implements QueryComparator<BudgetReviewDetailDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesBudgetReviewDTLQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(BudgetReviewDetailDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value) {

            if (!value.isEmpty()) {
                builder.append("and (lower(NO_BUDGETREVIEW) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(ID_REKENING) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(ANGGARAN_TAHUNAN) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(REALISASI) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(PERSEN) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(SALDO) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KETERANGAN) like '%").append(value.toLowerCase()).append("%' escape ' ')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

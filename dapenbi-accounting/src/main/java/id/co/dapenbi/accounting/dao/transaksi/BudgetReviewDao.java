package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO.DaftarRekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BudgetReviewDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private BudgetReviewDetailDao budgetReviewDetailDao;

    public List<DaftarRekeningDTO> getDaftarRekening(BudgetReviewDTO budgetReview) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
//        String query = "SELECT R.ID_REKENING,\n" +
//                "       R.KODE_REKENING                                                                                      AS NOMOR,\n" +
//                "       R.NAMA_REKENING                                                                                      AS NAMA_REKENING,\n" +
//                "       (S.SALDO_ANGGARAN + S.SERAP_TAMBAH - S.SERAP_KURANG)                                                 AS ANGGARAN,\n" +
//                "       S.SALDO_DEBET                                                                                        AS REALISASI,\n" +
//                "       CASE\n" +
//                "           WHEN (S.SALDO_ANGGARAN + S.SERAP_TAMBAH - S.SERAP_KURANG) = 0 THEN 0\n" +
//                "           ELSE\n" +
//                "         " +
//                "      ROUND(((S.SALDO_DEBET / (S.SALDO_ANGGARAN + S.SERAP_TAMBAH - S.SERAP_KURANG)) * 100), 2) END AS PERSEN,\n" +
//                "       ((S.SALDO_ANGGARAN + S.SERAP_TAMBAH - S.SERAP_KURANG) - S.SALDO_DEBET)                               AS SALDO\n" +
//                "FROM ACC_SALDO S\n" +
//                "         LEFT JOIN ACC_REKENING R on S.ID_REKENING = R.ID_REKENING\n" +
//                "WHERE S.KODE_THNBUKU = :KODE_THNBUKU\n" +
//                "  AND to_date(S.TGL_SALDO) = to_date(:TGL_SALDO, 'yyyy-mm-dd')\n" +
//                "  AND S.KODE_DRI = 1\n" +
//                "  AND EXISTS(SELECT ID_REKENING\n" +
//                "             FROM ACC_ANGGARAN A\n" +
//                "             WHERE A.ID_REKENING = R.ID_REKENING\n" +
//                "               AND A.STATUS_DATA = 'APPROVE');\n";
//        String satker = SessionUtil.getSession("satker");
//        if (!satker.equalsIgnoreCase("DKI")) {
//            finalQuery += "\nAND R.STATUS_PEMILIK_ANGGARAN = :SATKER";
//        }
//        String tglSaldo = budgetReview.getCreatedDate().toString().substring(0, 10);\

//        String finalQuery = "SELECT " +
//                "R.ID_REKENING,\n" +
//                "       R.KODE_REKENING,\n" +
//                "       R.NAMA_REKENING,\n" +
//                "       A.TOTAL_ANGGARAN,\n" +
//                "       A.REALISASI_BERJALAN,\n" +
//                "       (A.TOTAL_ANGGARAN - A.REALISASI_BERJALAN) AS SALDO\n" +
//                "FROM ACC_ANGGARAN A\n" +
//                "         LEFT JOIN ACC_REKENING R on A.ID_REKENING = R.ID_REKENING\n" +
//                "WHERE A.STATUS_AKTIF = 1\n" +
//                "  AND A.STATUS_DATA = 'APPROVE'\n" +
//                "  AND A.KODE_THNBUKU = :KODE_THNBUKU ";
//
        String finalQuery = "SELECT \n" +
                "       S.ID_REKENING                                                                               AS ID_REKENING,\n" +
                "       S.KODE_REKENING                                                                             AS KODE_REKENING,\n" +
                "       S.NAMA_REKENING                                                                             AS NAMA_REKENING,\n" +
                "       (COALESCE(S.NILAI_ANGGARAN, 0) + COALESCE(S.SERAP_TAMBAH, 0) - COALESCE(S.SERAP_KURANG, 0)) AS ANGGARAN,\n" +
                "       COALESCE(S.SALDO_DEBET, 0)                                                                  AS REALISASI,\n" +
                "       ROUND((COALESCE(S.SALDO_DEBET, 0) /\n" +
                "              ((COALESCE(S.NILAI_ANGGARAN, 0) + COALESCE(S.SERAP_TAMBAH, 0) - COALESCE(S.SERAP_KURANG, 0))) *\n" +
                "              100), 2)                                                                             AS PERSEN,\n" +
                "       ((COALESCE(S.NILAI_ANGGARAN, 0) + COALESCE(S.SERAP_TAMBAH, 0) - COALESCE(S.SERAP_KURANG, 0)) -\n" +
                "        COALESCE(S.SALDO_DEBET, 0))                                                                AS SALDO\n" +
                "FROM ACC_SALDO S\n" +
                "         LEFT JOIN ACC_REKENING AR on S.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE TO_CHAR(S.TGL_SALDO, 'YYYY-MM-DD') = :TGL_SALDO AND S.KODE_DRI = '1'\n" +
                "  AND (COALESCE(S.NILAI_ANGGARAN, 0) + COALESCE(S.SERAP_TAMBAH, 0) - COALESCE(S.SERAP_KURANG, 0)) != 0\n" +
                "  AND AR.TIPE_REKENING IN ('ASET_OPR', 'BIAYA', 'PENDAPATAN')";

        MapSqlParameterSource map = new MapSqlParameterSource();
//        map.addValue("KODE_THNBUKU", budgetReview.getKodeThnBuku().getKodeTahunBuku());
        map.addValue("TGL_SALDO", budgetReview.getTglTransaksi());
        //        map.addValue("SATKER", satker);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<DaftarRekeningDTO>() {
            @Override
            public DaftarRekeningDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                DaftarRekeningDTO daftarRekeningDTO = new DaftarRekeningDTO();
                RekeningDTO rekening = new RekeningDTO();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                daftarRekeningDTO.setIdRekening(rekening);
                daftarRekeningDTO.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN"));
                daftarRekeningDTO.setRealisasi(resultSet.getBigDecimal("REALISASI"));
                daftarRekeningDTO.setSaldo(resultSet.getBigDecimal("SALDO"));
                daftarRekeningDTO.setPersen(resultSet.getFloat("PERSEN"));
                daftarRekeningDTO.setKeterangan("");
                return daftarRekeningDTO;
            }
        });
    }

    public List<BudgetReviewDTO> findForDataTableBudgetReviewDTO(DataTablesRequest<BudgetReviewDTO> params, String search) {
        //language=Oracle
        String query = "" +
                "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_BUDGETREVIEW,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TRIWULAN,\n" +
                "       KETERANGAN,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_BUDGETREVIEW\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesBudgetReviewQueryComparator queryComparator = new DatatablesBudgetReviewQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_BUDGETREVIEW");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<BudgetReviewDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<BudgetReviewDTO>() {
            @Override
            public BudgetReviewDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                BudgetReviewDTO value = new BudgetReviewDTO();

                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
//                List<BudgetReviewDetailDTO> budgetReviewDetails = budgetReviewDetailDao.findByNoBudgetReview(resultSet.getString("NO_BUDGETREVIEW"));

                value.setNoBudgetReview(resultSet.getString("NO_BUDGETREVIEW"));
                value.setKodeThnBuku(tahunBuku);
                value.setKodePeriode(periode);
                value.setTriwulan(resultSet.getString("TRIWULAN"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                value.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
                value.setUserValidasi(resultSet.getString("USER_VALIDASI"));
                value.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
                value.setStatusData(resultSet.getString("STATUS_DATA"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
//                if (budgetReviewDetails.size() > 0)
//                    value.setBudgetReviewDetails(budgetReviewDetails);
                return value;
            }
        });

        return list;
    }

    public Long findForDataTableBudgetReviewDTO(BudgetReviewDTO params, String search) {
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW FROM ACC_BUDGETREVIEW WHERE 1=1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();
//        map.addValue("statusData", statusData);

        DatatablesBudgetReviewQueryComparator queryComparator = new DatatablesBudgetReviewQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("VALUE_ROW"));
        return row;
    }

    private class DatatablesBudgetReviewQueryComparator implements QueryComparator<BudgetReviewDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesBudgetReviewQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(BudgetReviewDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(BudgetReviewDTO params, String value) {
            if (!params.getStatusData().isEmpty()) {
                builder.append(" AND STATUS_DATA = :STATUS_DATA ");
                map.addValue("STATUS_DATA", params.getStatusData());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(NO_BUDGETREVIEW) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KODE_THNBUKU) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KODE_PERIODE) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(TRIWULAN) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KETERANGAN) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(TGL_VALIDASI) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(USER_VALIDASI) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(CATATAN_VALIDASI) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(STATUS_DATA) like '%").append(value.toLowerCase()).append("%' escape ' ')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<BudgetReviewDetailDTO> findDetails(String noBudgetReview) {
        String finalQuery = "SELECT BRD.ID_BUDGETREVIEW_DTL,\n" +
                "       BRD.NO_BUDGETREVIEW,\n" +
                "       BRD.ID_REKENING,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(BRD.ANGGARAN_TAHUNAN, 0) AS ANGGARAN_TAHUNAN,\n" +
                "       COALESCE(BRD.REALISASI, 0) AS REALISASI,\n" +
                "       ROUND(BRD.PERSEN, 2) AS PERSEN,\n" +
                "       COALESCE(BRD.SALDO, 0) AS SALDO,\n" +
                "       COALESCE(BRD.KETERANGAN, '-') AS KETERANGAN,\n" +
                "       BRD.CREATED_BY,\n" +
                "       BRD.CREATED_DATE,\n" +
                "       BRD.UPDATED_BY,\n" +
                "       BRD.UPDATED_DATE\n" +
                "FROM ACC_BUDGETREVIEW_DTL BRD\n" +
                "         LEFT JOIN ACC_REKENING AR on BRD.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE NO_BUDGETREVIEW = :NO_BUDGETREVIEW \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("NO_BUDGETREVIEW", noBudgetReview);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<BudgetReviewDetailDTO>() {
            @Override
            public BudgetReviewDetailDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                BudgetReviewDetailDTO daftarRekeningDTO = new BudgetReviewDetailDTO();
                Rekening rekening = new Rekening();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                BudgetReview budgetReview = new BudgetReview();
                budgetReview.setNoBudgetReview(resultSet.getString("NO_BUDGETREVIEW"));
                daftarRekeningDTO.setIdRekening(rekening);
                daftarRekeningDTO.setNoBudgetReview(budgetReview);
                daftarRekeningDTO.setIdBudgetReviewDtl(resultSet.getInt("ID_BUDGETREVIEW_DTL"));
                daftarRekeningDTO.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
                daftarRekeningDTO.setRealisasi(resultSet.getBigDecimal("REALISASI"));
                daftarRekeningDTO.setSaldo(resultSet.getBigDecimal("SALDO"));
                daftarRekeningDTO.setPersen(resultSet.getFloat("PERSEN"));
                daftarRekeningDTO.setKeterangan(resultSet.getString("KETERANGAN"));
                return daftarRekeningDTO;
            }
        });
    }

    public int merge(BudgetReview value) {
        String query = "MERGE INTO ACC_BUDGETREVIEW \n" +
                "USING DUAL\n" +
                "ON (KODE_PERIODE = :kodePeriode AND KODE_THNBUKU = :kodeThnbuku)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET NO_BUDGETREVIEW  = :noBudgetreview,\n" +
                "        TRIWULAN         = :triwulan,\n" +
                "        KETERANGAN       = :keterangan,\n" +
                "        TGL_VALIDASI     = :tglValidasi,\n" +
                "        USER_VALIDASI    = :userValidasi,\n" +
                "        CATATAN_VALIDASI = :catatanValidasi,\n" +
                "        STATUS_DATA      = :statusData,\n" +
                "        CREATED_BY       = :createdBy,\n" +
                "        CREATED_DATE     = :createdDate,\n" +
                "        UPDATED_BY       = :updatedBy,\n" +
                "        UPDATED_DATE     = :updatedDate\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (NO_BUDGETREVIEW, KODE_THNBUKU, KODE_PERIODE, TRIWULAN, KETERANGAN, TGL_VALIDASI, USER_VALIDASI,\n" +
                "            CATATAN_VALIDASI, STATUS_DATA, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)\n" +
                "    VALUES (:noBudgetreview, :kodeThnbuku, :kodePeriode, :triwulan, :keterangan, :tglValidasi, :userValidasi,\n" +
                "            :catatanValidasi, :statusData, :createdBy, :createdDate, :updatedBy, :updatedDate)\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("noBudgetreview", value.getNoBudgetReview());
        params.addValue("kodeThnbuku", value.getKodeThnBuku());
        params.addValue("kodePeriode", value.getKodePeriode());
        params.addValue("triwulan", value.getTriwulan());
        params.addValue("keterangan", value.getKeterangan());
        params.addValue("tglValidasi", value.getTglValidasi());
        params.addValue("userValidasi", value.getUserValidasi());
        params.addValue("catatanValidasi", value.getCatatanValidasi());
        params.addValue("statusData", value.getStatusData());
        params.addValue("createdBy", value.getCreatedBy());
        params.addValue("createdDate", value.getCreatedDate());
        params.addValue("updatedBy", value.getUpdatedBy());
        params.addValue("updatedDate", value.getUpdatedDate());
        return namedParameterJdbcTemplate.update(query, params);
    }

    public String findByTahunBukuAndPeriode(String kodeTahunBuku, String kodePeriode) {
        String query = "SELECT NO_BUDGETREVIEW\n" +
                "FROM ACC_BUDGETREVIEW\n" +
                "WHERE KODE_THNBUKU = :KODE_THNBUKU\n" +
                "  AND KODE_PERIODE = :KODE_PERIODE";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("KODE_THNBUKU", kodeTahunBuku);
        params.addValue("KODE_PERIODE", kodePeriode);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> resultSet.getString("NO_BUDGETREVIEW"));
    }

}

package id.co.dapenbi.accounting.dao.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDTO;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDetailDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PenyusunanAnggaranAkuntingDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> getDaftarRekening(String kodeTahunBuku) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String finalQuery = "SELECT \n" +
                "   AR.*, \n" +
                "   COALESCE((\n" +
                "       SELECT aa.TOTAL_ANGGARAN FROM ACC_AT_DTL aa \n" +
                "       WHERE aa.ID_REKENING = ar.ID_REKENING AND\n" +
                "             aa.KODE_THNBUKU = :kodeTahunBuku AND\n" +
                "             aa.STATUS_AKTIF = '1'\n" +
                "   ), 0) AS ANGGARANLALU \n" +
                "FROM ACC_REKENING AR \n" +
                "WHERE \n" +
                "   AR.TIPE_REKENING IN ('ASET_OPR', 'BIAYA', 'PENDAPATAN') AND \n" +
                "   IS_SUMMARY = 'N' \n" +
                "ORDER BY AR.KODE_REKENING ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO>() {
            @Override
            public PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO daftarRekeningDTO = new PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO();
                RekeningDTO rekening = new RekeningDTO();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setIdParent(resultSet.getInt("ID_PARENT"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                rekening.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                rekening.setUrutan(resultSet.getInt("URUTAN"));
                daftarRekeningDTO.setIdRekening(rekening);
                daftarRekeningDTO.setAnggaranLalu(resultSet.getBigDecimal("ANGGARANLALU"));
                daftarRekeningDTO.setRealisasi(new BigDecimal(0));
                daftarRekeningDTO.setPerkiraan(new BigDecimal(0));
                daftarRekeningDTO.setTotalAnggaran(new BigDecimal(0));
                daftarRekeningDTO.setProyeksi1(new BigDecimal(0));
                daftarRekeningDTO.setProyeksi2(new BigDecimal(0));
                return daftarRekeningDTO;
            }
        });
    }

    public List<PenyusunanAnggaranAkuntingDTO> findForDataTableBudgetReviewDTO(DataTablesRequest<PenyusunanAnggaranAkuntingDTO> params, String search) {
        //language=Oracle
        String query = "" +
                "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_ANGGARAN,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       VERSI,\n" +
                "       KETERANGAN,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_AT_HDR\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesBudgetReviewQueryComparator queryComparator = new DatatablesBudgetReviewQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns(
                "NO_ANGGARAN", "NO_ANGGARAN", "KODE_THNBUKU", "KODE_PERIODE", "VERSI",
                "CREATED_DATE", "CREATED_BY", "STATUS_DATA");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<PenyusunanAnggaranAkuntingDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<PenyusunanAnggaranAkuntingDTO>() {
            @Override
            public PenyusunanAnggaranAkuntingDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PenyusunanAnggaranAkuntingDTO value = new PenyusunanAnggaranAkuntingDTO();

                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));

                value.setNoAnggaran(resultSet.getString("NO_ANGGARAN"));
                value.setKodeThnBuku(tahunBuku);
                value.setKodePeriode(periode);
                value.setVersi(resultSet.getString("VERSI"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                value.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
                value.setUserValidasi(resultSet.getString("USER_VALIDASI"));
                value.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
                value.setStatusData(resultSet.getString("STATUS_DATA"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });

        return list;
    }

    public Long findForDataTableBudgetReviewDTO(PenyusunanAnggaranAkuntingDTO params, String search) {
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW FROM ACC_AT_HDR WHERE 1=1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();
//        map.addValue("statusData", statusData);

        DatatablesBudgetReviewQueryComparator queryComparator = new DatatablesBudgetReviewQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("VALUE_ROW"));
        return row;
    }

    private class DatatablesBudgetReviewQueryComparator implements QueryComparator<PenyusunanAnggaranAkuntingDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesBudgetReviewQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(PenyusunanAnggaranAkuntingDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(PenyusunanAnggaranAkuntingDTO params, String value) {
            if (!params.getStatusData().isEmpty()) {
                builder.append(" AND STATUS_DATA = :STATUS_DATA ");
                map.addValue("STATUS_DATA", params.getStatusData());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(NO_ANGGARAN) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KODE_THNBUKU) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(KODE_PERIODE) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
                        .append("or lower(VERSI) like '%").append(value.toLowerCase()).append("%' escape ' '\n")
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

    public List<PenyusunanAnggaranAkuntingDetailDTO> findDetails(String noAnggaran) {
        String finalQuery = "SELECT AAD.ID_ANGGARAN,\n" +
                "       AAD.NO_ANGGARAN,\n" +
                "       AAD.ID_REKENING,\n" +
                "       AAD.KODE_THNBUKU,\n" +
                "       (\n" +
                "           SELECT AR.KODE_REKENING FROM ACC_REKENING AR WHERE AR.ID_REKENING = AAD.ID_REKENING \n" +
                "       ) AS KODE_REKENING,\n" +
                "       (\n" +
                "           SELECT AR.NAMA_REKENING FROM ACC_REKENING AR WHERE AR.ID_REKENING = AAD.ID_REKENING \n" +
                "       ) AS NAMA_REKENING,\n" +
                "       (\n" +
                "           SELECT AR.TIPE_REKENING FROM ACC_REKENING AR WHERE AR.ID_REKENING = AAD.ID_REKENING \n" +
                "       ) AS TIPE_REKENING,\n" +
                "       COALESCE(AAD.ANGGARAN_LALU, 0) AS ANGGARAN_LALU,\n" +
                "       COALESCE(AAD.REALISASI, 0) AS REALISASI,\n" +
                "       COALESCE(AAD.PERKIRAAN, 0) AS PERKIRAAN,\n" +
                "       COALESCE(AAD.TOTAL_ANGGARAN, 0) AS TOTAL_ANGGARAN,\n" +
                "       COALESCE(AAD.PROYEKSI1, 0) AS PROYEKSI1,\n" +
                "       COALESCE(AAD.PROYEKSI2, 0) AS PROYEKSI2,\n" +
                "       COALESCE(AAD.KETERANGAN, '-') AS KETERANGAN,\n" +
                "       AAD.CREATED_BY,\n" +
                "       AAD.CREATED_DATE,\n" +
                "       AAD.UPDATED_BY,\n" +
                "       AAD.UPDATED_DATE\n" +
                "FROM ACC_AT_DTL AAD\n" +
                "WHERE AAD.NO_ANGGARAN = :NO_ANGGARAN \n" +
                "ORDER BY KODE_REKENING";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("NO_ANGGARAN", noAnggaran);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<PenyusunanAnggaranAkuntingDetailDTO>() {
            @Override
            public PenyusunanAnggaranAkuntingDetailDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PenyusunanAnggaranAkuntingDetailDTO daftarRekeningDTO = new PenyusunanAnggaranAkuntingDetailDTO();
                Rekening rekening = new Rekening();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                PenyusunanAnggaranAkunting penyusunanAnggaranAkunting = new PenyusunanAnggaranAkunting();
                penyusunanAnggaranAkunting.setNoAnggaran(resultSet.getString("NO_ANGGARAN"));
                daftarRekeningDTO.setIdRekening(rekening);
                daftarRekeningDTO.setNoAnggaran(penyusunanAnggaranAkunting);
                daftarRekeningDTO.setIdAnggaranDtl(resultSet.getInt("ID_ANGGARAN"));
                daftarRekeningDTO.setKodeThnBuku(resultSet.getString("KODE_THNBUKU"));
                daftarRekeningDTO.setAnggaranLalu(resultSet.getBigDecimal("ANGGARAN_LALU"));
                daftarRekeningDTO.setRealisasi(resultSet.getBigDecimal("REALISASI"));
                daftarRekeningDTO.setPerkiraan(resultSet.getBigDecimal("PERKIRAAN"));
                daftarRekeningDTO.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
                daftarRekeningDTO.setProyeksi1(resultSet.getBigDecimal("PROYEKSI1"));
                daftarRekeningDTO.setProyeksi2(resultSet.getBigDecimal("PROYEKSI2"));
                daftarRekeningDTO.setKeterangan(resultSet.getString("KETERANGAN"));
                return daftarRekeningDTO;
            }
        });
    }

    public int merge(PenyusunanAnggaranAkunting value) {
        String query = "MERGE INTO ACC_AT_HDR A\n" +
                "USING DUAL\n" +
                "ON (KODE_PERIODE = :kodePeriode AND KODE_THNBUKU = :kodeThnbuku)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET NO_ANGGARAN      = :noAnggaran,\n" +
                "        VERSI            = :versi,\n" +
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
                "    INSERT (NO_ANGGARAN, KODE_THNBUKU, KODE_PERIODE, VERSI, KETERANGAN, TGL_VALIDASI, USER_VALIDASI,\n" +
                "            CATATAN_VALIDASI, STATUS_DATA, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)\n" +
                "    VALUES (:noAnggaran, :kodeThnbuku, :kodePeriode, :versi, :keterangan, :tglValidasi, :userValidasi,\n" +
                "            :catatanValidasi, :statusData, :createdBy, :createdDate, :updatedBy, :updatedDate)\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("noAnggaran", value.getNoAnggaran());
        params.addValue("kodeThnbuku", value.getKodeThnBuku());
        params.addValue("kodePeriode", value.getKodePeriode());
        params.addValue("versi", value.getVersi());
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

    public List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> getDataForExport(String noAnggaran, String tipeRekening) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String finalQuery = "SELECT \n" +
                "   AR.*, \n" +
                "   COALESCE((\n" +
                "       SELECT aa.TOTAL_ANGGARAN FROM ACC_AT_DTL aa \n" +
                "       WHERE aa.ID_REKENING = ar.ID_REKENING AND\n" +
                "             aa.NO_ANGGARAN = :noAnggaran\n" +
                "   ), 0) AS TOTAL_ANGGARAN \n" +
                "FROM ACC_REKENING AR \n" +
                "WHERE \n" +
                "   AR.TIPE_REKENING = :tipeRekening AND \n" +
                "   IS_SUMMARY = 'N' \n" +
                "ORDER BY AR.KODE_REKENING ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("noAnggaran", noAnggaran);
        map.addValue("tipeRekening", tipeRekening);
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO>() {
            @Override
            public PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO daftarRekeningDTO = new PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO();
                RekeningDTO rekening = new RekeningDTO();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setIdParent(resultSet.getInt("ID_PARENT"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                rekening.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                rekening.setUrutan(resultSet.getInt("URUTAN"));
                daftarRekeningDTO.setIdRekening(rekening);
                daftarRekeningDTO.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
                return daftarRekeningDTO;
            }
        });
    }

}

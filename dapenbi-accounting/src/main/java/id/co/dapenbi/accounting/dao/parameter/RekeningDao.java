package id.co.dapenbi.accounting.dao.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.repository.transaksi.SaldoCurrentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class RekeningDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Rekening> findAll() {
        String query = "SELECT " +
                "       R.ID_REKENING,\n" +
                "       R.ID_PARENT,\n" +
                "       R.KODE_REKENING,\n" +
                "       R.NAMA_REKENING,\n" +
                "       R.LEVEL_REKENING,\n" +
                "       R.TIPE_REKENING,\n" +
                "       R.SALDO_NORMAL,\n" +
                "       R.STATUS_NERACA_ANGGARAN,\n" +
                "       R.STATUS_PEMILIK_ANGGARAN,\n" +
                "       R.STATUS_AKTIF,\n" +
                "       R.CREATED_BY,\n" +
                "       R.CREATED_DATE,\n" +
                "       R.UPDATED_BY,\n" +
                "       R.UPDATED_DATE,\n" +
                "       R.IS_SUMMARY,\n" +
                "       SC.SALDO_DEBET,\n" +
                "       SC.SALDO_ANGGARAN,\n" +
                "       SC.SALDO_AKHIR,\n" +
                "       SC.SALDO_KREDIT,\n" +
                "       SC.SALDO_AWAL,\n" +
                "       SC.NILAI_ANGGARAN,\n" +
                "       SC.SERAP_TAMBAH,\n" +
                "       SC.SERAP_KURANG\n" +
                "FROM ACC_REKENING R LEFT JOIN ACC_SALDO_CURRENT SC on R.ID_REKENING = SC.ID_REKENING ";

        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<Rekening>() {
                @Override
                public Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                    Rekening value = new Rekening();
                    SaldoCurrent saldoCurrent = new SaldoCurrent(
                            resultSet.getBigDecimal("SALDO_AWAL"),
                            resultSet.getBigDecimal("SALDO_DEBET"),
                            resultSet.getBigDecimal("SALDO_KREDIT"),
                            resultSet.getBigDecimal("SALDO_AKHIR"),
                            resultSet.getBigDecimal("NILAI_ANGGARAN"),
                            resultSet.getBigDecimal("SERAP_TAMBAH"),
                            resultSet.getBigDecimal("SERAP_KURANG"),
                            resultSet.getBigDecimal("SALDO_ANGGARAN")
                    );

                    value.setIdRekening(resultSet.getInt("ID_REKENING"));
                    value.setIdParent(resultSet.getInt("ID_PARENT"));
                    value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                    value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                    value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                    value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                    value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                    value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                    value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                    value.setCreatedBy(resultSet.getString("CREATED_BY"));
                    value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                    value.setSaldoCurrent(saldoCurrent);
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Rekening> findAllRekening() {
        String query = "SELECT " +
                "       ID_REKENING,\n" +
                "       ID_PARENT,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       LEVEL_REKENING,\n" +
                "       TIPE_REKENING,\n" +
                "       SALDO_NORMAL,\n" +
                "       STATUS_NERACA_ANGGARAN,\n" +
                "       STATUS_PEMILIK_ANGGARAN,\n" +
                "       STATUS_AKTIF,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       IS_SUMMARY\n" +
                "FROM ACC_REKENING ORDER BY LPAD(KODE_REKENING, 10) ";
//                "ORDER BY order by LPAD(KODE_REKENING, 10) ";
        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<Rekening>() {
                @Override
                public Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                    Rekening value = new Rekening();

                    value.setIdRekening(resultSet.getInt("ID_REKENING"));
                    value.setIdParent(resultSet.getInt("ID_PARENT"));
                    value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                    value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                    value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                    value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                    value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                    value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                    value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                    value.setCreatedBy(resultSet.getString("CREATED_BY"));
                    value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
//                    if (resultSet.getString("IS_SUMMARY").equals("Y")) {
//                        value.setExpanded(true);
//                    }
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Iterable<RekeningDTO> findAllDTO() {
        String query = "SELECT \n" +
                "       ID_REKENING,\n" +
                "       ID_PARENT,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       LEVEL_REKENING,\n" +
                "       TIPE_REKENING,\n" +
                "       SALDO_NORMAL,\n" +
                "       STATUS_NERACA_ANGGARAN,\n" +
                "       STATUS_PEMILIK_ANGGARAN,\n" +
                "       STATUS_AKTIF,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       IS_SUMMARY\n" +
                "FROM ACC_REKENING";

        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<RekeningDTO>() {
                @Override
                public RekeningDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                    RekeningDTO value = new RekeningDTO();
                    value.setIdRekening(resultSet.getInt("ID_REKENING"));
                    value.setIdParent(resultSet.getInt("ID_PARENT"));
                    value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                    value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                    value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                    value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                    value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                    value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                    value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                    value.setCreatedBy(resultSet.getString("CREATED_BY"));
                    value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
//                    value.setSaldoCurrent(saldoCurrentDTO);
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Integer updateParentByParent(String newParent, String oldParent) {
        String query = "UPDATE ACC_REKENING SET ID_PARENT = :newParent WHERE ID_PARENT = :oldParent";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("newParent", newParent);
        params.addValue("oldParent", oldParent);
        try {

            return namedParameterJdbcTemplate.update(query, params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Integer deleteByKodeRekening(String kodeRekening) {
        String query = "DELETE ACC_REKENING WHERE KODE_REKENING = :kodeRekening";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("kodeRekening", kodeRekening);
        try {
            return namedParameterJdbcTemplate.update(query, params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<String> getAllIdRekening() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String query = "SELECT ID_REKENING FROM ACC_REKENING";
        try {

            return namedParameterJdbcTemplate.query(query, params, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString("ID_REKENING");
                }
            });
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Rekening findById(String idRekening) {
        String query = "SELECT \n" +
                "       ID_REKENING,\n" +
                "       ID_PARENT,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       IS_SUMMARY,\n" +
                "       LEVEL_REKENING,\n" +
                "       TIPE_REKENING,\n" +
                "       URUTAN,\n" +
                "       SALDO_NORMAL,\n" +
                "       STATUS_NERACA_ANGGARAN,\n" +
                "       STATUS_PEMILIK_ANGGARAN,\n" +
                "       SALDO_MIN,\n" +
                "       SALDO_MAX,\n" +
                "       KODE_ORG,\n" +
                "       STATUS_AKTIF,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_REKENING\n" +
                "WHERE ID_REKENING = :ID_REKENING\n";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_REKENING", idRekening);
        Rekening rekening = namedParameterJdbcTemplate.queryForObject(query, params, new RowMapper<Rekening>() {
            @Override
            public Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                Rekening value = new Rekening();
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setIdParent(resultSet.getInt("ID_PARENT"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });
        if (rekening != null) {
            return rekening;
        } else {
            return null;
        }
    }

    public List<RekeningDTO> findForDataTableRekening(DataTablesRequest<RekeningDTO> params, String search) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       AR.ID_REKENING,\n" +
                "       AR.ID_PARENT,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.IS_SUMMARY,\n" +
                "       AR.LEVEL_REKENING,\n" +
                "       AR.TIPE_REKENING,\n" +
                "       AR.URUTAN,\n" +
                "       AR.SALDO_NORMAL,\n" +
                "       AR.STATUS_NERACA_ANGGARAN,\n" +
                "       AR.STATUS_PEMILIK_ANGGARAN,\n" +
                "       AR.SALDO_MIN,\n" +
                "       AR.SALDO_MAX,\n" +
                "       AR.KODE_ORG,\n" +
                "       AR.STATUS_AKTIF,\n" +
                "       AR.CREATED_BY,\n" +
                "       AR.CREATED_DATE,\n" +
                "       AR.UPDATED_BY,\n" +
                "       AR.UPDATED_DATE,\n" +
                "       SC.SALDO_DEBET,\n" +
                "       SC.SALDO_ANGGARAN,\n" +
                "       (SELECT SALDO\n" +
                "        FROM (SELECT SALDO, SALDO_AWAL, KODE_REKENING FROM V_BUKU_BESAR ORDER BY SALDO DESC )\n" +
                "        WHERE ROWNUM = 1 AND KODE_REKENING = AR.KODE_REKENING)                  AS SALDO_AKHIR,\n" +
                "       SC.SALDO_KREDIT,\n" +
                "       (SELECT SALDO_AWAL\n" +
                "        FROM (SELECT SALDO, SALDO_AWAL, KODE_REKENING FROM V_BUKU_BESAR ORDER BY SALDO DESC )\n" +
                "        WHERE ROWNUM = 1 AND KODE_REKENING = AR.KODE_REKENING)                  AS SALDO_AWAL,\n" +
                "       SC.NILAI_ANGGARAN,\n" +
                "       SC.SERAP_TAMBAH,\n" +
                "       SC.SERAP_KURANG\n" +
                "FROM ACC_REKENING AR\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on AR.ID_REKENING = SC.ID_REKENING\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        RekeningDao.DatatablesRekeningQueryComparator queryComparator = new RekeningDao.DatatablesRekeningQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("URUTAN");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<RekeningDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<RekeningDTO>() {
            @Override
            public RekeningDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                RekeningDTO value = new RekeningDTO();
                SaldoCurrent saldoCurrent = new SaldoCurrent(
                        resultSet.getBigDecimal("SALDO_AWAL"),
                        resultSet.getBigDecimal("SALDO_DEBET"),
                        resultSet.getBigDecimal("SALDO_KREDIT"),
                        resultSet.getBigDecimal("SALDO_AKHIR"),
                        resultSet.getBigDecimal("NILAI_ANGGARAN"),
                        resultSet.getBigDecimal("SERAP_TAMBAH"),
                        resultSet.getBigDecimal("SERAP_KURANG"),
                        resultSet.getBigDecimal("SALDO_ANGGARAN")
                );

                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setIdParent(resultSet.getInt("ID_PARENT"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setUrutan(resultSet.getInt("URUTAN"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                value.setSaldoMin(resultSet.getBigDecimal("SALDO_MIN"));
                value.setSaldoMax(resultSet.getBigDecimal("SALDO_MAX"));
                value.setKodeOrg(resultSet.getString("KODE_ORG"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                value.setSaldoCurrent(saldoCurrent);
                return value;
            }
        });

        return list;
    }

    public Long findForDataTableRekening(RekeningDTO value, String search) {
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW\n" +
                "FROM ACC_REKENING AR\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on AR.ID_REKENING = SC.ID_REKENING\n" +
                "WHERE 1 = 1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        RekeningDao.DatatablesRekeningQueryComparator queryComparator = new RekeningDao.DatatablesRekeningQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(value, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesRekeningQueryComparator implements QueryComparator<RekeningDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesRekeningQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(RekeningDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(RekeningDTO params, String value) {
            if (params.getLevelRekening() != null) {
                builder.append(" AND LEVEL_REKENING = :LEVEL_REKENING ");
                map.addValue("LEVEL_REKENING", params.getLevelRekening());
            }
            if (!params.getIsSummary().isEmpty()) {
                builder.append(" AND IS_SUMMARY = :IS_SUMMARY ");
                map.addValue("IS_SUMMARY", params.getIsSummary());
            }
            if (!value.isEmpty()) {
                builder.append("and (lower(AR.KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(AR.NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(AR.LEVEL_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(AR.SALDO_NORMAL) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<Rekening> findByListTipeRekening(String tipeRekening) throws IncorrectResultSizeDataAccessException {
        String query = "SELECT ID_REKENING,\n" +
                "       ID_PARENT,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       IS_SUMMARY,\n" +
                "       LEVEL_REKENING,\n" +
                "       TIPE_REKENING,\n" +
                "       URUTAN,\n" +
                "       SALDO_NORMAL,\n" +
                "       STATUS_NERACA_ANGGARAN,\n" +
                "       STATUS_PEMILIK_ANGGARAN,\n" +
                "       SALDO_MIN,\n" +
                "       SALDO_MAX,\n" +
                "       KODE_ORG,\n" +
                "       STATUS_AKTIF,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       IS_EDIT\n" +
                "FROM ACC_REKENING\n" +
                "WHERE TIPE_REKENING IN (:TIPE_REKENING)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (tipeRekening.isEmpty()) {
            List<String> tipeRekenings = new ArrayList<>();
            tipeRekenings.add("ASET_OPR");
            tipeRekenings.add("BIAYA");
            params.addValue("TIPE_REKENING", tipeRekenings);
        } else {
            params.addValue("TIPE_REKENING", tipeRekening);
        }
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<Rekening>() {
            @Override
            public Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                Rekening value = new Rekening();
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setIdParent(resultSet.getInt("ID_PARENT"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setIsSummary(resultSet.getString("IS_SUMMARY"));
                value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setUrutan(resultSet.getInt("URUTAN"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setStatusNeracaAnggaran(resultSet.getString("STATUS_NERACA_ANGGARAN"));
                value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                value.setStatusPemilikAnggaran(resultSet.getString("STATUS_PEMILIK_ANGGARAN"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });
    }

    public SaldoDTO findSaldoAwalAkhir(String kodeRekening, String startDate, String endDate) throws EmptyResultDataAccessException {
        String query = "SELECT KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       :START_DATE AS START_DATE,\n" +
                "       :END_DATE AS END_DATE,\n" +
                "       F_SALDO_AWAL(:START_DATE, KODE_REKENING) AS SALDO_AWAL,\n" +
                "       F_SALDO_AKHIR(:END_DATE, KODE_REKENING) AS SALDO_AKHIR\n" +
                "FROM V_BUKU_BESAR WHERE TGL_TRANSAKSI BETWEEN :START_DATE AND :END_DATE AND KODE_REKENING = :KODE_REKENING AND ROWNUM = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("KODE_REKENING", kodeRekening);
        params.addValue("START_DATE", startDate);
        params.addValue("END_DATE", endDate);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> {
            SaldoDTO value = new SaldoDTO();
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
            value.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
            return value;
        });
    }
}

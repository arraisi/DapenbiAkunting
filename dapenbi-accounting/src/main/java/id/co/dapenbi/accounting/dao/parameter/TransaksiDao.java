package id.co.dapenbi.accounting.dao.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransaksiDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private TransaksiJurnalDao transaksiJurnalDao;

    public List<Transaksi> findAll() {
        String query = "" +
                "SELECT\n" +
                "    KODE_TRANSAKSI,\n" +
                "    NAMA_TRANSAKSI,\n" +
                "    STATUS_AKTIF,\n" +
                "    CREATED_BY,\n" +
                "    CREATED_DATE,\n" +
                "    UPDATED_BY,\n" +
                "    UPDATED_DATE\n" +
                "FROM ACC_JNS_TRANSAKSI\n";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> getTransaksi(resultSet));
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<TransaksiDTO> findAllWithJurnals() {
        String query = "" +
                "SELECT\n" +
                "    KODE_TRANSAKSI,\n" +
                "    NAMA_TRANSAKSI,\n" +
                "    STATUS_AKTIF,\n" +
                "    JENIS_WARKAT,\n" +
                "    CREATED_BY,\n" +
                "    CREATED_DATE,\n" +
                "    UPDATED_BY,\n" +
                "    UPDATED_DATE\n" +
                "FROM ACC_JNS_TRANSAKSI\n";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> getTransaksiAndJurnals(resultSet));
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    private Transaksi getTransaksi(ResultSet resultSet) throws SQLException {
        Transaksi value = new Transaksi();
//        List<TransaksiJurnal> transaksiJurnals = transaksiJurnalDao.findByKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));

        value.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
        value.setNamaTransaksi(resultSet.getString("NAMA_TRANSAKSI"));
        value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
        value.setCreatedBy(resultSet.getString("CREATED_BY"));
        value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
        value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
        value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
//        value.setTransaksiJurnals(transaksiJurnals);
        return value;
    }

    private TransaksiDTO getTransaksiAndJurnals(ResultSet resultSet) throws SQLException {
        TransaksiDTO value = new TransaksiDTO();
        List<TransaksiJurnalDTO> transaksiJurnals = transaksiJurnalDao.findByKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));

        value.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
        value.setNamaTransaksi(resultSet.getString("NAMA_TRANSAKSI"));
        value.setJenisWarkat(resultSet.getString("JENIS_WARKAT"));
        value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
        value.setCreatedBy(resultSet.getString("CREATED_BY"));
        value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
        value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
        value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
        value.setTransaksiJurnals(transaksiJurnals);
        return value;
    }

    public Transaksi findByKodeTransaksi(String kodeTransaksi) {
        String query = "" +
                "SELECT " +
                "KODE_TRANSAKSI, " +
                "NAMA_TRANSAKSI, " +
                "STATUS_AKTIF, " +
                "CREATED_BY, " +
                "CREATED_DATE, " +
                "UPDATED_BY, " +
                "UPDATED_DATE\n" +
                "FROM ACC_JNS_TRANSAKSI\n" +
                "WHERE KODE_TRANSAKSI = :KODE_TRANSAKSI \n";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_TRANSAKSI", kodeTransaksi);
            return namedParameterJdbcTemplate.queryForObject(query, params, new RowMapper<Transaksi>() {
                @Override
                public Transaksi mapRow(ResultSet resultSet, int i) throws SQLException {
                    return getTransaksi(resultSet);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<TransaksiDTO> datatables(DataTablesRequest<TransaksiDTO> params, String search) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       NAMA_TRANSAKSI,\n" +
                "       STATUS_AKTIF,\n" +
                "       JENIS_WARKAT,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_JNS_TRANSAKSI\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiDao.DatatablesTransaksiQueryComparator queryComparator = new TransaksiDao.DatatablesTransaksiQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("KODE_TRANSAKSI", "NAMA_TRANSAKSI", "STATUS_AKTIF");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<TransaksiDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<TransaksiDTO>() {
            @Override
            public TransaksiDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                TransaksiDTO value = new TransaksiDTO();
                List<TransaksiJurnalDTO> transaksiJurnals;
                if (params.getValue().getJenisWarkat().equals("JURNAL_TRANSAKSI")) {
                    transaksiJurnals = transaksiJurnalDao.findByKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                } else {
                    transaksiJurnals = transaksiJurnalDao.findByKodeTransaksiForJurnalTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                }

                value.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                value.setNamaTransaksi(resultSet.getString("NAMA_TRANSAKSI"));
                value.setJenisWarkat(resultSet.getString("JENIS_WARKAT"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                value.setTransaksiJurnals(transaksiJurnals);
                return value;
            }
        });
        return list;
    }

    public Long datatables(TransaksiDTO params, String search) {
        //language=Oracle
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW\n" +
                "FROM SAAKUNTING.ACC_JNS_TRANSAKSI\n" +
                "WHERE 1 = 1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiDao.DatatablesTransaksiQueryComparator queryComparator = new TransaksiDao.DatatablesTransaksiQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesTransaksiQueryComparator implements QueryComparator<Transaksi> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesTransaksiQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Transaksi params) {
            return null;
        }

        public StringBuilder getQuerySearch(TransaksiDTO params, String value) {
            if (params != null) {
                if (!params.getStatusAktif().isEmpty()) {
                    builder.append(" and STATUS_AKTIF = :STATUS_AKTIF ");
                    map.addValue("STATUS_AKTIF", params.getStatusAktif());
                }
                if (!params.getJenisWarkat().isEmpty()) {
                    builder.append(" and JENIS_WARKAT = :JENIS_WARKAT ");
                    map.addValue("JENIS_WARKAT", params.getJenisWarkat());
                }
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(KODE_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(NAMA_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(STATUS_AKTIF) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<Transaksi> datatablesForJurnalBiaya(DataTablesRequest<TransaksiDTO> params, String search) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       NAMA_TRANSAKSI,\n" +
                "       STATUS_AKTIF,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_JNS_TRANSAKSI\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiDao.DatatablesTransaksiQueryComparator queryComparator = new TransaksiDao.DatatablesTransaksiQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("KODE_TRANSAKSI", "NAMA_TRANSAKSI", "STATUS_AKTIF");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<Transaksi> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<Transaksi>() {
            @Override
            public Transaksi mapRow(ResultSet resultSet, int i) throws SQLException {
                Transaksi value = new Transaksi();
                List<TransaksiJurnal> transaksiJurnals = transaksiJurnalDao.findByKodeTransaksiDRI2(resultSet.getString("KODE_TRANSAKSI"));

                value.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                value.setNamaTransaksi(resultSet.getString("NAMA_TRANSAKSI"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                value.setTransaksiJurnals(transaksiJurnals);
                return value;
            }
        });
        return list;
    }

    public Long datatablesForJurnalBiaya(TransaksiDTO params, String search) {
        //language=Oracle
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW\n" +
                "FROM SAAKUNTING.ACC_JNS_TRANSAKSI\n" +
                "WHERE 1 = 1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiDao.DatatablesTransaksiQueryComparator queryComparator = new TransaksiDao.DatatablesTransaksiQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

}

package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MonitoringTransaksiDao {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    private WarkatJurnalDao warkatJurnalDao;

    public List<Warkat> list(DataTablesRequest<Warkat> params, String search) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_WARKAT,\n" +
                "       NUWP,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       TOTAL_TRANSAKSI,\n" +
                "       TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT WHERE 1=1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        WarkatQueryComparator queryComparator = new WarkatQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());
        return namedJdbcTemplate.query(finalQuery, map, new RowMapperWarkat());
    }

    public List<Warkat> list(Warkat params) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_WARKAT,\n" +
                "       NUWP,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       TOTAL_TRANSAKSI,\n" +
                "       TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT WHERE STATUS_DATA in ('DRAFT','SUBMIT','VALID','PA','FA','REJECT') \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        WarkatQueryComparator queryComparator = new WarkatQueryComparator(query, map);
        StringBuilder finalQuery = queryComparator.getQuery(params);
        map = queryComparator.getParameters();

        return namedJdbcTemplate.query(finalQuery.toString(), map, new RowMapperWarkat());
    }

    public List<MSTLookUp> findAllStatusData() {
        //language=Oracle
        String query = "SELECT KODE_LOOKUP, JENIS_LOOKUP, NAMA_LOOKUP, KETERANGAN, STATUS_DATA\n" +
                "FROM MST_LOOKUP\n" +
                "WHERE JENIS_LOOKUP = 'STATUS_WARKAT'\n" +
                "ORDER BY KETERANGAN ASC ";
        return namedJdbcTemplate.query(query, new MapSqlParameterSource(), (resultSet, i) -> {
            MSTLookUp mstLookUp = new MSTLookUp();
            mstLookUp.setKodeLookUp(resultSet.getString("KODE_LOOKUP"));
            mstLookUp.setJenisLookUp(resultSet.getString("JENIS_LOOKUP"));
            mstLookUp.setNamaLookUp(resultSet.getString("NAMA_LOOKUP"));
            mstLookUp.setKeterangan(resultSet.getString("KETERANGAN"));
            mstLookUp.setStatusData(resultSet.getString("STATUS_DATA"));
            return mstLookUp;
        });
    }

    public Long listCount(String search) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT WHERE STATUS_DATA in ('DRAFT','SUBMIT','VALID','PA','FA','REJECT') \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        WarkatQueryComparator queryComparator = new WarkatQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        return namedJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
    }

    public class RowMapperWarkat implements RowMapper<Warkat> {
        @Override
        public Warkat mapRow(ResultSet resultSet, int i) throws SQLException {
            Warkat value = new Warkat();
            TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
            Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
            List<WarkatJurnal> warkatJurnals = warkatJurnalDao.findByNoWarkat(resultSet.getString("NO_WARKAT"));

            value.setNoWarkat(resultSet.getString("NO_WARKAT"));
            Transaksi transaksi = new Transaksi();
            transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
            value.setKodeTransaksi(transaksi);
            value.setTahunBuku(tahunBuku);
            value.setKodePeriode(periode);
            value.setTglBuku(resultSet.getTimestamp("TGL_BUKU"));
            value.setTglTransaksi(resultSet.getTimestamp("TGL_TRANSAKSI"));
            value.setKeterangan(resultSet.getString("KETERANGAN"));
            value.setTotalTransaksi(resultSet.getBigDecimal("TOTAL_TRANSAKSI"));
            value.setTerbilang(resultSet.getString("TERBILANG"));
            value.setNoPengantarWarkat(resultSet.getString("NO_PENGANTARWARKAT"));
            value.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
            value.setUserValidasi(resultSet.getString("USER_VALIDASI"));
            value.setStatusData(resultSet.getString("STATUS_DATA"));
            value.setCreatedBy(resultSet.getString("CREATED_BY"));
            value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            value.setWarkatJurnals(warkatJurnals);
            return value;
        }
    }

    private class WarkatQueryComparator implements QueryComparator<Warkat> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public WarkatQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Warkat params) {
            if (params.getIdOrg() != 0) {
                this.builder.append("   AND ID_ORG = :idOrg\n");
                this.map.addValue("idOrg", params.getIdOrg());
            }

            if (params.getTglTransaksi() != null) {
                this.builder.append("   AND to_char(TGL_TRANSAKSI,'dd-mm-yyyy') = to_char(:tglTransaksi,'dd-mm-yyyy')\n");
                this.map.addValue("tglTransaksi", params.getTglTransaksi());
            }
            return builder;
        }

        public StringBuilder getQuerySearch(String value) {
            if (!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like lower('%").append(value).append("%') escape ' '\n")
                        .append("or lower(KODE_TRANSAKSI) like lower('%").append(value).append("%') escape ' '\n")
                        .append("or lower(KETERANGAN) like lower('%").append(value).append("%') escape ' '\n")
                        .append("or lower(TOTAL_TRANSAKSI) like lower('%").append(value).append("%') escape ' '\n")
                        .append("or lower(TGL_BUKU) like lower('%").append(value).append("%') escape ' '\n")
                        .append("or lower(STATUS_DATA) like lower('%").append(value).append("%') escape ' ')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

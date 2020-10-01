package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.dto.transaksi.ValidasiWarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static java.math.BigDecimal.*;

@Repository
public class JurnalBiayaDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<JurnalBiayaDTO> datatable(DataTablesRequest<JurnalBiayaDTO> params, String search) {
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
                "       JENIS_WARKAT,\n" +
                "       ARUSKAS,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT WHERE JENIS_WARKAT = 'JBIAYA' \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalBiayaDao.DatatablesJurnalBiayaQueryComparator queryComparator = new JurnalBiayaDao.DatatablesJurnalBiayaQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT", "KODE_TRANSAKSI");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<JurnalBiayaDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<JurnalBiayaDTO>() {
            @Override
            public JurnalBiayaDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                JurnalBiayaDTO value = new JurnalBiayaDTO();
                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                return getJurnalBiaya(resultSet, value, tahunBuku, periode, transaksi);
            }
        });

        return list;
    }

    public Long datatable(JurnalBiayaDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT WHERE JENIS_WARKAT = 'JBIAYA' \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalBiayaDao.DatatablesJurnalBiayaQueryComparator queryComparator = new JurnalBiayaDao.DatatablesJurnalBiayaQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesJurnalBiayaQueryComparator implements QueryComparator<JurnalBiayaDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesJurnalBiayaQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(JurnalBiayaDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(JurnalBiayaDTO params, String value) {
            if (params != null) {
                if (!params.getStatusData().isEmpty()) {
                    builder.append(" AND STATUS_DATA = :STATUS_DATA ");
                    map.addValue("STATUS_DATA", params.getStatusData());
                }
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like '%").append(value).append("%'\n")
                        .append("or lower(KODE_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%'\n")
                        .append("or lower(STATUS_DATA) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    private JurnalBiayaDTO getJurnalBiaya(ResultSet resultSet, JurnalBiayaDTO value, TahunBuku tahunBuku, Periode periode, Transaksi transaksi) throws SQLException {
        value.setNoWarkat(resultSet.getString("NO_WARKAT"));
        value.setNuwp(resultSet.getString("NUWP"));
        value.setKodeTransaksi(transaksi);
        value.setIdOrg(resultSet.getInt("ID_ORG"));
        value.setKodeOrg(resultSet.getString("KODE_ORG"));
        value.setNamaOrg(resultSet.getString("NAMA_ORG"));
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
        value.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
        value.setJenisWarkat(resultSet.getString("JENIS_WARKAT"));
        value.setArusKas(resultSet.getString("ARUSKAS"));
        value.setStatusData(resultSet.getString("STATUS_DATA"));
        value.setCreatedBy(resultSet.getString("CREATED_BY"));
        value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
        value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
        value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
        return value;
    }

    public List<WarkatJurnal> findJurnalBiayaDebits() {
        String query =
                "SELECT ROW_NUMBER() over (order by ROWNUM) as no, \n" +
                        "       R.ID_REKENING, \n" +
                        "       R.KODE_REKENING, \n" +
                        "       R.NAMA_REKENING, \n" +
                        "       R.SALDO_NORMAL, \n" +
                        "       SC.SALDO_DEBET, \n" +
                        "       SC.SALDO_KREDIT, \n" +
                        "       SC.SALDO_AKHIR\n" +
                        "FROM ACC_SALDO_CURRENT SC\n" +
                        "LEFT JOIN ACC_REKENING R on R.ID_REKENING = SC.ID_REKENING\n" +
                        "WHERE R.TIPE_REKENING = 'BIAYA' AND SC.STATUS_DATA = 'FA' AND SC.SALDO_DEBET > 0 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<WarkatJurnal>() {
            @Override
            public WarkatJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatJurnal value = new WarkatJurnal();
                Rekening rekening = new Rekening();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setNoUrut(resultSet.getInt("no"));
                value.setIdRekening(rekening);
                value.setSaldoNormal("K");
                value.setJumlahDebit(resultSet.getBigDecimal("SALDO_KREDIT") != null ? resultSet.getBigDecimal("SALDO_KREDIT") : ZERO);
                value.setJumlahKredit(resultSet.getBigDecimal("SALDO_DEBET") != null ? resultSet.getBigDecimal("SALDO_DEBET") : ZERO);
                return value;
            }
        });
    }
}

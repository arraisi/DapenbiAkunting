package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPendapatanDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPendapatanDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPendapatanDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@Repository
public class JurnalPendapatanDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private RekeningService rekeningService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    public Optional<Warkat> findById(String id) {
        String query = "SELECT \n" +
                "   aw.NO_WARKAT         AS noWarkat,\n" +
                "   aw.TGL_TRANSAKSI     AS tglTransaksi,\n" +
                "   aw.TGL_BUKU          AS tglBuku,\n" +
                "   aw.KETERANGAN        AS keterangan,\n" +
                "   aw.TOTAL_TRANSAKSI   AS totalTransaksi,\n" +
                "   aw.CREATED_DATE      AS createdDate,\n" +
                "   aw.CREATED_BY        AS createdBy,\n" +
                "   aw.STATUS_DATA       AS statusData\n" +
                "FROM ACC_WARKAT aw \n" +
                "WHERE aw.NO_WARKAT = :noWarkat\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("noWarkat", id);
        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<Warkat>>() {
                @Override
                public Optional<Warkat> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Warkat value = new Warkat();
                    value.setNoWarkat(resultSet.getString("noWarkat"));
                    List<WarkatJurnal> warkatJurnals = findWarkatJurnalByNoWarkat(value.getNoWarkat());
                    value.setTglTransaksi(resultSet.getTimestamp("tglTransaksi"));
                    value.setTglBuku(resultSet.getTimestamp("tglBuku"));
                    value.setKeterangan(resultSet.getString("keterangan"));
                    value.setTotalTransaksi(resultSet.getBigDecimal("totalTransaksi"));
                    value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                    value.setCreatedBy(resultSet.getString("createdBy"));
                    value.setStatusData(resultSet.getString("statusData"));
                    value.setWarkatJurnals(warkatJurnals);
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public List<WarkatJurnal> findWarkatJurnalByNoWarkat(String noWarkat) {
        String query = "SELECT \n" +
                "   awj.ID_WARKAT_JURNAL    AS idWarkatjurnal," +
                "   awj.ID_REKENING         AS idRekening,\n" +
                "   awj.JUMLAH_DEBIT        AS debit,\n" +
                "   awj.JUMLAH_KREDIT       AS kredit,\n" +
                "   awj.NO_WARKAT           AS noWarkat,\n" +
                "   awj.NO_URUT             AS noUrut,\n" +
                "   awj.CREATED_BY          AS createdBy,\n" +
                "   awj.CREATED_DATE        AS createdDate,\n" +
                "   awj.UPDATED_BY          AS updatedBy,\n" +
                "   awj.UPDATED_DATE        AS updatedDate\n" +
                "FROM ACC_WARKAT_JURNAL awj \n" +
                "WHERE awj.NO_WARKAT = :noWarkat\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("noWarkat", noWarkat);

        List<WarkatJurnal> list = this.namedParameterJdbcTemplate.query(query, map, new RowMapper<WarkatJurnal>() {
            @Override
            public WarkatJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                Warkat warkat = new Warkat();
                warkat.setNoWarkat(resultSet.getString("noWarkat"));

                Optional<SaldoCurrent> saldoCurrent = saldoCurrentService.findByIdRekening(resultSet.getInt("idRekening"));
                Rekening rekening = rekeningService.findById(resultSet.getInt("idRekening")).get();
                rekening.setSaldoCurrent(saldoCurrent.get());

                WarkatJurnal value = new WarkatJurnal();
                value.setNoWarkat(warkat);
                value.setIdWarkatJurnal(resultSet.getInt("idWarkatJurnal"));
                value.setNoUrut(resultSet.getInt("noUrut"));
                value.setIdRekening(rekening);
                value.setJumlahDebit(resultSet.getBigDecimal("debit"));
                value.setJumlahKredit(resultSet.getBigDecimal("kredit"));
                value.setCreatedBy(resultSet.getString("createdBy"));
                value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                value.setUpdatedBy(resultSet.getString("updatedBy"));
                value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                return value;
            }
        });
        return list;
    }

    public List<JurnalPendapatanDTO> datatables(DataTablesRequest<JurnalPendapatanDTO> params, String search) {
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
                "FROM ACC_WARKAT WHERE JENIS_WARKAT = 'JPENDAPATAN' \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalPendapatanDao.DatatablesJurnalPendapatanQueryComparator queryComparator = new JurnalPendapatanDao.DatatablesJurnalPendapatanQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT", "KODE_TRANSAKSI");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<JurnalPendapatanDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<JurnalPendapatanDTO>() {
            @Override
            public JurnalPendapatanDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                JurnalPendapatanDTO value = new JurnalPendapatanDTO();
                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                return getJurnalPendapatan(resultSet, value, tahunBuku, periode, transaksi);
            }
        });

        return list;
    }
    public Long datatables(JurnalPendapatanDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT WHERE JENIS_WARKAT = 'JPENDAPATAN' \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalPendapatanDao.DatatablesJurnalPendapatanQueryComparator queryComparator = new JurnalPendapatanDao.DatatablesJurnalPendapatanQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesJurnalPendapatanQueryComparator implements QueryComparator<JurnalPendapatanDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesJurnalPendapatanQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(JurnalPendapatanDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(JurnalPendapatanDTO params, String value) {
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

    private JurnalPendapatanDTO getJurnalPendapatan(ResultSet resultSet, JurnalPendapatanDTO value, TahunBuku tahunBuku, Periode periode, Transaksi transaksi) throws SQLException {
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

    public List<WarkatJurnal> findJurnalPendapatanKredits() {
        String query =
                "SELECT ROW_NUMBER() over (order by ROWNUM) as no, \n" +
                        "       R.ID_REKENING, \n" +
                        "       R.KODE_REKENING, \n" +
                        "       R.NAMA_REKENING, \n" +
                        "       R.SALDO_NORMAL, \n" +
                        "       SC.SALDO_DEBET, \n" +
                        "       SC.SALDO_KREDIT, \n" +
                        "       SC.SALDO_AKHIR\n" +
                        "FROM ACC_SALDO_FA SC\n" +
                        "LEFT JOIN ACC_REKENING R on R.ID_REKENING = SC.ID_REKENING\n" +
                        "WHERE R.TIPE_REKENING = 'PENDAPATAN' AND SC.SALDO_KREDIT > 0 ";
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
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setJumlahDebit(resultSet.getBigDecimal("SALDO_DEBET") != null ? resultSet.getBigDecimal("SALDO_DEBET") : ZERO);
                value.setJumlahKredit(resultSet.getBigDecimal("SALDO_KREDIT") != null ? resultSet.getBigDecimal("SALDO_KREDIT") : ZERO);
                return value;
            }
        });
    }
}

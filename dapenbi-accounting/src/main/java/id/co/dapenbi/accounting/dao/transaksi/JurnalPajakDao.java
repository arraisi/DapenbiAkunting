package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.entity.parameter.*;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.repository.transaksi.WarkatJurnalRepository;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import id.co.dapenbi.accounting.service.impl.parameter.TransaksiService;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
import org.apache.commons.collections4.IterableUtils;
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

@Repository
public class JurnalPajakDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private WarkatJurnalRepository warkatJurnalRepository;

    @Autowired
    private RekeningService rekeningService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private TransaksiService transaksiService;

    public List<JurnalPajakDTO> datatables(DataTablesRequest<JurnalPajakDTO> params, String search) {
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
                "FROM ACC_WARKAT WHERE JENIS_WARKAT = 'PAJAK' \n";

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT");
        MapSqlParameterSource map = new MapSqlParameterSource();
        JurnalPajakDatatablesQueryComparator queryComparator = new JurnalPajakDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<JurnalPajakDTO>() {
            @Override
            public JurnalPajakDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                JurnalPajakDTO value = new JurnalPajakDTO();
                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
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
        });
    }

    public Long datatables(JurnalPajakDTO params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_WARKAT \n" +
                "WHERE JENIS_WARKAT = 'PAJAK' \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        JurnalPajakDatatablesQueryComparator queryComparator = new JurnalPajakDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    public class JurnalPajakDatatablesQueryComparator implements QueryComparator<JurnalPajakDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public JurnalPajakDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(JurnalPajakDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(JurnalPajakDTO params, String value) {
            if(!params.getStatusData().isEmpty()) {
                builder.append("and STATUS_DATA = :statusData\n");
                map.addValue("statusData", params.getStatusData());
            }

            if(!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like '%").append(value).append("%'\n")
                        .append("or lower(TGL_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%'\n")
                        .append("or lower(TOTAL_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(CREATED_DATE) like '%").append(value).append("%'\n")
                        .append("or lower(CREATED_BY) like '%").append(value).append("%'\n")
                        .append("or lower(STATUS_DATA) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

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
                "   awj.CREATED_DATE        AS createdDate\n" +
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
                return value;
            }
        });
        return list;
    }

    public List<TransaksiJurnal> transaksiJurnalDatatables(DataTablesRequest<TransaksiJurnal> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   ajtj.ID_TRANSAKSI_JURNAL     AS idTransaksiJurnal,\n" +
                "   ajtj.KODE_TRANSAKSI          AS kodeTransaksi,\n" +
                "   ajtj.ID_REKENING             AS idRekening,\n" +
                "   ajtj.SALDO_NORMAL            AS saldoNormal,\n" +
                "   ajtj.NO_URUT                 AS noUrut,\n" +
                "   ajtj.CREATED_BY              AS createdBy,\n" +
                "   ajtj.CREATED_DATE            AS createdDate,\n" +
                "   ajtj.UPDATED_BY              AS updatedBy,\n" +
                "   ajtj.UPDATED_DATE            AS updatedDate\n" +
                "FROM ACC_JNS_TRANSAKSI_JURNAL ajtj \n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = ajtj.ID_REKENING\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("ar.KODE_REKENING", "ar.KODE_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        TransaksiJurnalDatatablesQueryComparator queryComparator = new TransaksiJurnalDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params.getValue());
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<TransaksiJurnal> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<TransaksiJurnal>() {
            @Override
            public TransaksiJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                TransaksiJurnal value = new TransaksiJurnal();
                Optional<Transaksi> transaksi = transaksiService.findById(resultSet.getString("kodeTransaksi"));
                Optional<Rekening> rekening = rekeningService.findById(resultSet.getInt("idRekening"));
                Optional<SaldoCurrent> saldoCurrent = saldoCurrentService.findByIdRekening(resultSet.getInt("idRekening"));
                Rekening rekeningEntity = rekening.isPresent() ? rekening.get() : new Rekening();
                rekeningEntity.setSaldoCurrent(saldoCurrent.isPresent() ? saldoCurrent.get() : new SaldoCurrent());
                value.setIdTransaksiJurnal(resultSet.getInt("idTransaksiJurnal"));
                value.setKodeTransaksi(transaksi.isPresent() ? transaksi.get() : new Transaksi());
                value.setIdRekening(rekeningEntity);
                value.setSaldoNormal(resultSet.getString("saldoNormal"));
                value.setNoUrut(resultSet.getString("noUrut"));
                value.setCreatedBy(resultSet.getString("createdBy"));
                value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                value.setUpdatedBy(resultSet.getString("updatedBy"));
                value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                return value;
            }
        });
        return list;
    }

    public Long transaksiJurnalDatatables(TransaksiJurnal params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "from ACC_JNS_TRANSAKSI_JURNAL ajtj\n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = ajtj.ID_REKENING\n" +
                "where 1 = 1\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        TransaksiJurnalDatatablesQueryComparator queryComparator = new TransaksiJurnalDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, params);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class TransaksiJurnalDatatablesQueryComparator implements QueryComparator<TransaksiJurnal> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public TransaksiJurnalDatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(TransaksiJurnal params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, TransaksiJurnal params) {
            if(!params.getKodeTransaksi().getKodeTransaksi().isEmpty()) {
                builder.append("and ajtj.KODE_TRANSAKSI = :kodeTransaksi\n");
                map.addValue("kodeTransaksi", params.getKodeTransaksi().getKodeTransaksi());
            }
            if(!value.isEmpty()) {
                builder.append("and (lower(ar.KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ar.NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(ajtj.SALDO_NORMAL) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

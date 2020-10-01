package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.laporan.LaporanRekening;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class MasterLaporanDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<LaporanDetail> laporanRumusDatatables(DataTablesRequest<LaporanDetail> params, String search) {
        String query = "select" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   ald.*\n" +
                "FROM ACC_LAPORAN_DTL ald\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("TO_NUMBER(ald.URUTAN)", "ald.JUDUL");
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRumusDatatablesQueryComparator queryComparator = new LaporanRumusDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanDetail>() {
            @Override
            public LaporanDetail mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanDetail value = new LaporanDetail();
                value.setIdLaporanDtl(resultSet.getInt("ID_LAPORAN_DTL"));
                value.setKodeRumus(resultSet.getString("KODE_RUMUS"));
                value.setJudul(resultSet.getString("JUDUL"));
                value.setLevelAkun(resultSet.getInt("LEVEL_AKUN"));
                value.setRumus(resultSet.getString("RUMUS"));
                value.setCetakJudul(resultSet.getString("CETAK_JUDUL"));
                value.setCetakGaris(resultSet.getString("CETAK_GARIS"));
                value.setSpi(resultSet.getString("SPI"));
                value.setStatusData(resultSet.getString("STATUS_DATA"));
                value.setUrutan(resultSet.getString("URUTAN"));
                value.setSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                value.setStatusRumus(resultSet.getString("STATUS_RUMUS"));
                return value;
            }
        });
    }

    public Long laporanRumusDatatables(LaporanDetail params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_LAPORAN_DTL ald\n" +
                "where 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRumusDatatablesQueryComparator queryComparator = new LaporanRumusDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params,search);
        map = queryComparator.getParameters();

        return this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
    }

    private class LaporanRumusDatatablesQueryComparator implements QueryComparator<LaporanDetail> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public LaporanRumusDatatablesQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        public StringBuilder getQuerySearch(LaporanDetail params, String value) {
            if (params.getLaporanHeader() != null) {
                builder.append("and ald.ID_LAPORAN_HDR = :idLaporanHdr\n");
                map.addValue("idLaporanHdr", params.getLaporanHeader());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(ald.JUDUL) like '").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public StringBuilder getQuery(LaporanDetail params) {
            return null;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<LaporanRekening> laporanRekeningDatatables(DataTablesRequest<LaporanRekening> params, String search) {
        String query = "select\n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   alr.*\n" +
                "FROM ACC_LAPORAN_REK alr\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("alr.RUMUS_URUTAN");
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRekeningDatatablesQueryComparator queryComparator = new LaporanRekeningDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanRekening>() {
            @Override
            public LaporanRekening mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanRekening value = new LaporanRekening();
                value.setIdLaporanRek(resultSet.getInt("ID_LAPORAN_REK"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setRumusUrutan(resultSet.getInt("RUMUS_URUTAN"));
                value.setRumusOperator(resultSet.getString("RUMUS_OPERATOR"));
                value.setStatusData(resultSet.getString("STATUS_DATA"));
                value.setIdLaporanDtl(resultSet.getInt("ID_LAPORAN_DTL"));
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                return value;
            }
        });
    }

    public Long laporanRekeningDatatables(LaporanRekening params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_LAPORAN_REK alr\n" +
                "where 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanRekeningDatatablesQueryComparator queryComparator = new LaporanRekeningDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params,search);
        map = queryComparator.getParameters();

        return this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
    }

    private class LaporanRekeningDatatablesQueryComparator implements QueryComparator<LaporanRekening> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public LaporanRekeningDatatablesQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        public StringBuilder getQuerySearch(LaporanRekening params, String value) {
            if (params.getIdLaporanDtl() != null) {
                builder.append("and alr.ID_LAPORAN_DTL = :idLaporanDtl\n");
                map.addValue("idLaporanDtl", params.getIdLaporanDtl());
            }
            return builder;
        }

        @Override
        public StringBuilder getQuery(LaporanRekening params) {
            return null;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public void updateLaporanRekening(LaporanRekening laporanRekening) {
        String query = "update\n" +
                "   ACC_LAPORAN_REK\n" +
                "SET\n" +
                "   ID_REKENING = :idRekening, \n" +
                "   KODE_REKENING = :kodeRekening, \n" +
                "   NAMA_REKENING = :namaRekening, \n" +
                "   RUMUS_URUTAN = :rumusUrutan, \n" +
                "   RUMUS_OPERATOR = :rumusOperator, \n" +
                "   STATUS_DATA = :statusData, \n" +
                "   UPDATED_BY = :updatedBy, \n" +
                "   UPDATED_DATE = :updatedDate\n" +
                "WHERE ID_LAPORAN_REK = :idLaporanRek\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", laporanRekening.getIdRekening());
        map.addValue("kodeRekening", laporanRekening.getKodeRekening());
        map.addValue("namaRekening", laporanRekening.getNamaRekening());
        map.addValue("rumusUrutan", laporanRekening.getRumusUrutan());
        map.addValue("rumusOperator", laporanRekening.getRumusOperator());
        map.addValue("statusData", laporanRekening.getStatusData());
        map.addValue("updatedBy", laporanRekening.getUpdatedBy());
        map.addValue("updatedDate", laporanRekening.getUpdatedDate());
        map.addValue("idLaporanRek", laporanRekening.getIdLaporanRek());

        this.namedParameterJdbcTemplate.update(query, map);
    }

    public BigDecimal sumSaldoAkhirFromSaldoByIdRekeningAndTglSaldoAndDRI(String idRekening, String tglSaldo, String kodeDRI) {
        String query = "select\n" +
                "   sum(COALESCE(saldo.SALDO_AKHIR, 0))  as saldoAkhir\n" +
                "from ACC_SALDO saldo\n" +
                "where saldo.KODE_REKENING like :idRekening\n" +
                "and to_char(saldo.TGL_SALDO, 'yyyy-MM-dd') = :tglSaldo\n" +
                "and saldo.KODE_DRI = :kodeDRI\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening + "%");
        map.addValue("tglSaldo", tglSaldo);
        map.addValue("kodeDRI", kodeDRI);
        BigDecimal saldoakhir = this.namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);

        if (saldoakhir == null)
            return BigDecimal.ZERO;
        else
            return saldoakhir;
    }

    public BigDecimal sumNilaiWajarFromSPIDtlByTahunBukuAndPeriode(String kodeInvestasi, String kodeTahunBuku, String kodePeriode) {
        String query = "select\n" +
                "   sum(COALESCE(detail.NILAI_WAJAR, 0))  as nilaiWajar\n" +
                "from ACC_SPI_DTL detail\n" +
                "where detail.ID_INVESTASI = :idInvestasi\n" +
                "and detail.KODE_THNBUKU = :kodeTahunBuku\n" +
                "and detail.KODE_PERIODE = :kodePeriode\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idInvestasi", kodeInvestasi);
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        BigDecimal nilaiWajar = this.namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);

        if (nilaiWajar == null)
            return BigDecimal.ZERO;
        else
            return nilaiWajar;
    }

    public BigDecimal getSaldoSebelum(String kodeTahunBuku, String kodePeriode, Integer idLaporanDtl, String kodeDRI) {
//        log.info("Testing Get Saldo Sebelum: {}, {}, {}, {}", kodeTahunBuku, kodePeriode, idLaporanDtl, kodeDRI);
        String query = "SELECT\n" +
                "   lk.SALDO_BERJALAN as SALDO_SEBELUM \n" +
                "FROM LAP_KEU lk \n" +
                "WHERE lk.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND lk.KODE_PERIODE = (\n" +
                "   SELECT \n" +
                "       MAX(lk2.KODE_PERIODE)\n" +
                "   FROM LAP_KEU lk2 \n" +
                "   WHERE lk2.KODE_THNBUKU = :kodeTahunBuku\n" +
                "   AND lk2.ID_LAPORAN_DTL = :idLaporanDtl\n" +
                "   AND lk2.KODE_DRI = :kodeDRI\n" +
                ")\n" +
                "AND lk.ID_LAPORAN_DTL = :idLaporanDtl\n" +
                "AND lk.KODE_DRI = :kodeDRI\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
//        map.addValue("kodeRumus", kodeRumus);
        map.addValue("idLaporanDtl", idLaporanDtl);
//        map.addValue("idLaporanHdr", idLaporanHdr);
        map.addValue("kodeDRI", kodeDRI);

        try {
            BigDecimal saldoSebelum = this.namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);

            if (saldoSebelum == null)
                return BigDecimal.ZERO;
            else
                return saldoSebelum;
        } catch (EmptyResultDataAccessException erdae) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal sumSaldoFromArusKasBulanan(String kodeArusKas, String tanggal, String flagGroup) {
        String query = "SELECT \n" +
                "   COALESCE(SUM(aab.SALDO), 0)\n" +
                "FROM ACC_ARUSKAS_BULANAN aab \n" +
                "WHERE aab.KODE_ARUSKAS = :kodeArusKas\n" +
                "AND TO_CHAR(aab.TANGGAL, 'YYYY-MM-DD') = :tanggal\n" +
                "AND aab.FLAG_GROUP = :flagGroup\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeArusKas", kodeArusKas);
        map.addValue("tanggal", tanggal);
        map.addValue("flagGroup", flagGroup);

        return this.namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);
    }
}

package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuanganDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.repository.laporan.LaporanDetailRepository;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class LaporanKeuanganDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private LaporanDetailRepository laporanDetailRepository;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private SaldoService saldoService;

    public List<LaporanKeuanganDTO.LaporanKeuangan> datatables(DataTablesRequest<LaporanKeuanganDTO.LaporanKeuangan> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   keu.*, \n" +
                "   TO_CHAR(keu.TGL_LAPORAN, 'DD fmMONTH YYYY','nls_date_language = INDONESIAN') AS TANGGALLAPORAN\n" +
                "FROM LAP_KEU keu\n" +
                "WHERE 1 = 1\n";

        OrderingByColumns columns = new OrderingByColumns("TO_NUMBER(keu.URUTAN)");
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<LaporanKeuanganDTO.LaporanKeuangan> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<LaporanKeuanganDTO.LaporanKeuangan>() {
            @Override
            public LaporanKeuanganDTO.LaporanKeuangan mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanKeuanganDTO.LaporanKeuangan value = new LaporanKeuanganDTO.LaporanKeuangan();
                value.setIdLaporanHeader(resultSet.getInt("ID_LAPORAN_HDR"));
                value.setJudul(resultSet.getString("URAIAN"));
                value.setSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                value.setSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                value.setUrutan(resultSet.getString("URUTAN"));
                value.setCetakJudul(resultSet.getString("CETAK_JUDUL"));
                value.setCetakGaris(resultSet.getString("CETAK_GARIS"));
                value.setCetakMiring(resultSet.getString("CETAK_MIRING"));
                value.setCetakTebal(resultSet.getString("CETAK_TEBAL"));
                value.setLevelAkun(resultSet.getInt("LEVEL_AKUN"));
                value.setWarna("#000000");
                value.setTanggalLaporan(resultSet.getString("TANGGALLAPORAN"));
                return value;
            }
        });
        return list;
    }

    public Long datatables(LaporanKeuanganDTO.LaporanKeuangan params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM LAP_KEU keu\n" +
                "WHERE 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        DatatablesQueryComparator queryComparator = new DatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
        return row;
    }

    private class DatatablesQueryComparator implements QueryComparator<LaporanKeuanganDTO.LaporanKeuangan> {
        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(LaporanKeuanganDTO.LaporanKeuangan params) {
            return null;
        }

        public StringBuilder getQuerySearch(LaporanKeuanganDTO.LaporanKeuangan params, String value) {
            if (params.getIdLaporanHeader() != null) {
                builder.append("and keu.ID_LAPORAN_HDR = :idLaporanHDR\n");
                map.addValue("idLaporanHDR", params.getIdLaporanHeader());
            }

            if (!params.getKodeTahunBuku().isEmpty()) {
                builder.append("and keu.KODE_THNBUKU = :kodeTahunBuku\n");
                map.addValue("kodeTahunBuku", params.getKodeTahunBuku());
            }

            if (!params.getKodePeriode().isEmpty()) {
                builder.append("and keu.KODE_PERIODE = :kodePeriode\n");
                map.addValue("kodePeriode", params.getKodePeriode());
            }

            if (!params.getKodeDRI().isEmpty()) {
                builder.append("and keu.KODE_DRI = :kodeDRI\n");
                map.addValue("kodeDRI", params.getKodeDRI());
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<LaporanKeuanganDTO.PajakPenghasilanBadan> findPajakPenghasilanBadan(String periode, String kodeDri) {
        String query = "SELECT \n" +
                "    " +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 32 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS pendapatanObjekPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 33 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS pendapatanBukanObjekPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 34 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS jumlahPendapatan, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 35 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS biayaOperasional, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 36 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS biayaYangTidakBolehDikurangkan, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 37 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS jumlahBiaya, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 38 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS biayaYangDiperkenankan, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 39 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS pendapatanKenaPajak, \n" +
                "    (SELECT RUMUS FROM ACC_LAPORAN_DTL WHERE ID_LAPORAN_DTL = 40) AS rumusPctPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 40 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS pajakPenghasilanBadanTerutang, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 41 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS jumlahPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 42 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS kreditPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 43 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS uangMukaPPhPasal23, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 44 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS uangMukaPPhPasal25, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 45 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS jumlahKreditPajak, \n" +
                "    (SELECT SALDO_BERJALAN FROM LAP_KEU WHERE ID_LAPORAN_DTL = 102 AND KODE_PERIODE = :KODE_PERIODE AND KODE_DRI = :KODE_DRI) AS pajakPenghasilanBadan \n" +
                "FROM DUAL \n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("KODE_PERIODE", periode);
        params.addValue("KODE_DRI", kodeDri);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            LaporanKeuanganDTO.PajakPenghasilanBadan value = new LaporanKeuanganDTO.PajakPenghasilanBadan();
            final BigDecimal pendapatanObjekPajak = resultSet.getBigDecimal("pendapatanObjekPajak");
            final BigDecimal pendapatanBukanObjekPajak = resultSet.getBigDecimal("pendapatanBukanObjekPajak");
            final BigDecimal jumlahPendapatan = resultSet.getBigDecimal("jumlahPendapatan");
            value.setPendapatanObjectPajak(pendapatanObjekPajak);
            value.setPendapatanBukanObjectPajak(pendapatanBukanObjekPajak);
            value.setJumlahPendapatan(jumlahPendapatan);
            value.setBiayaOperasional(resultSet.getBigDecimal("biayaOperasional"));
            value.setBiayaYangTidakBolehDikurangkan(resultSet.getBigDecimal("biayaYangTidakBolehDikurangkan"));
            value.setJumlahBiaya(resultSet.getBigDecimal("jumlahBiaya"));
            value.setBiayaYangDiperkenankan(resultSet.getBigDecimal("biayaYangDiperkenankan"));
            value.setPendapatanKenaPajak(resultSet.getBigDecimal("pendapatanKenaPajak"));
            value.setPajakPenghasilanBadanTerhutang(resultSet.getBigDecimal("pajakPenghasilanBadanTerutang"));
            value.setJumlahPajak(resultSet.getBigDecimal("pajakPenghasilanBadanTerutang"));
            value.setKreditPajak(resultSet.getBigDecimal("kreditPajak"));
            value.setUangMukaPPhPasal23(resultSet.getBigDecimal("uangMukaPPhPasal23"));
            value.setUangMukaPPhPasal25(resultSet.getBigDecimal("uangMukaPPhPasal25"));
            value.setJumlahKreditPajak(resultSet.getBigDecimal("jumlahKreditPajak"));
            value.setPajakPenghasilanBadan(resultSet.getBigDecimal("pajakPenghasilanBadan"));

            value.setPctObjectPajak(pendapatanObjekPajak
                            .divide(jumlahPendapatan, MathContext.DECIMAL128)
                            .multiply(BigDecimal.valueOf(100)));
            value.setPctBukanObjectPajak(pendapatanBukanObjekPajak.divide(jumlahPendapatan, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100)));
            value.setPctPendapatan(value.getPctObjectPajak().add(value.getPctBukanObjectPajak()));

            final String[] rumusPctPajak = resultSet.getString("rumusPctPajak").split("\\*");
            final String pctPajak = rumusPctPajak[0].replace("#", "");
            final Float finalPctPajak = Float.valueOf(pctPajak.substring(2, 4));
            value.setPctPajak(finalPctPajak);
            return value;
        });
    }

    public List<LaporanKeuanganDTO.LaporanKeuangan> findNeraca(String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI, Integer start, Integer end) {
        String query = "SELECT \n" +
                "   keu.*,\n" +
                "   TO_CHAR(keu.TGL_LAPORAN, 'DD fmMONTH YYYY','nls_date_language = INDONESIAN') AS TANGGALLAPORAN\n" +
                "FROM LAP_KEU keu\n" +
                "WHERE keu.ID_LAPORAN_HDR = :idLaporanHDR\n" +
                "   and keu.KODE_THNBUKU = :kodeTahunBuku\n" +
                "   and keu.KODE_PERIODE = :kodePeriode\n" +
                "   and keu.KODE_DRI = :kodeDRI\n" +
                "   and (keu.URUTAN BETWEEN :start AND :end)\n" +
                "ORDER BY keu.URUTAN ASC";

        OrderingByColumns columns = new OrderingByColumns("TO_NUMBER(keu.URUTAN)");
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idLaporanHDR", idLaporanHeader);
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeDRI", kodeDRI);
        map.addValue("start", start);
        map.addValue("end", end);

        List<LaporanKeuanganDTO.LaporanKeuangan> list = this.namedParameterJdbcTemplate.query(query, map, new RowMapper<LaporanKeuanganDTO.LaporanKeuangan>() {
            @Override
            public LaporanKeuanganDTO.LaporanKeuangan mapRow(ResultSet resultSet, int i) throws SQLException {
                LaporanKeuanganDTO.LaporanKeuangan value = new LaporanKeuanganDTO.LaporanKeuangan();
                value.setIdLaporanHeader(resultSet.getInt("ID_LAPORAN_HDR"));
                value.setJudul(resultSet.getString("URAIAN"));
                value.setSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                value.setSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                value.setUrutan(resultSet.getString("URUTAN"));
                value.setCetakJudul(resultSet.getString("CETAK_JUDUL"));
                value.setCetakGaris(resultSet.getString("CETAK_GARIS"));
                value.setCetakMiring(resultSet.getString("CETAK_MIRING"));
                value.setCetakTebal(resultSet.getString("CETAK_TEBAL"));
                value.setLevelAkun(resultSet.getInt("LEVEL_AKUN"));
                value.setWarna("#000000");
                value.setTanggalLaporan(resultSet.getString("TANGGALLAPORAN"));
                value.setTanggalLaporan(resultSet.getString("TANGGALLAPORAN"));
                if (value.getLevelAkun().equals(1))
                    value.setTabs("");
                else if (value.getLevelAkun().equals(2))
                    value.setTabs("   ");
                else
                    value.setTabs("      ");
                return value;
            }
        });
        return list;
    }

    public List<String> findTanggalLaporan(String idLaporanHeader, String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        String baseQuery = "SELECT \n" +
                "   TO_CHAR(keu.TGL_LAPORAN, 'DD fmMONTH YYYY','nls_date_language = INDONESIAN') AS TANGGALLAPORAN\n" +
                "FROM LAP_KEU keu\n" +
                "WHERE keu.ID_LAPORAN_HDR = :idLaporanHDR\n" +
                "   and keu.KODE_THNBUKU = :kodeTahunBuku\n" +
                "   and keu.KODE_PERIODE = :kodePeriode\n" +
                "   and keu.KODE_DRI = :kodeDRI\n" +
                "   and keu.TGL_LAPORAN is not null \n" +
                "ORDER BY KEU.TGL_LAPORAN DESC ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idLaporanHDR", idLaporanHeader);
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeDRI", kodeDRI);

        List<String> list = this.namedParameterJdbcTemplate.query(baseQuery, map, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("TANGGALLAPORAN");
            }
        });
        return list;
    }

    public List<LaporanKeuanganDTO.LaporanKeuangan> listPerhitunganAngsuranPPH(String kodeDRI, String kodePeriode, String kodeTahunBuku) {
        StringBuilder query = new StringBuilder("SELECT \n" +
                "   lk.*\n" +
                "FROM LAP_KEU lk\n" +
                "WHERE lk.KODE_RUMUS IN ('D07', 'D14', 'D15', 'D23', 'D30', 'D31', 'D32', 'D33', 'F02', 'F07', 'F08', 'F12', 'F13')\n");

        MapSqlParameterSource map = new MapSqlParameterSource();
        if (!kodeDRI.isEmpty()) {
            query.append("AND lk.KODE_DRI = :kodeDRI\n");
            map.addValue("kodeDRI", kodeDRI);
        }

        if (!kodePeriode.isEmpty()) {
            query.append("AND lk.KODE_PERIODE = :kodePeriode\n");
            map.addValue("kodePeriode", kodePeriode);
        }

        if (!kodeTahunBuku.isEmpty()) {
            query.append("AND lk.KODE_THNBUKU = :kodeTahunBuku\n");
            map.addValue("kodeTahunBuku", kodeTahunBuku);
        }

        query.append("order by lk.KODE_RUMUS asc\n");

        return this.namedParameterJdbcTemplate.query(query.toString(), map, (resultSet, i) -> {
            LaporanKeuanganDTO.LaporanKeuangan value = new LaporanKeuanganDTO.LaporanKeuangan();
            value.setJudul(resultSet.getString("URAIAN").replace("Total ", "").toUpperCase());
            value.setSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
            value.setRumus(resultSet.getString("KODE_RUMUS"));
            value.setKodeDRI(resultSet.getString("KODE_DRI"));
            value.setKodePeriode(resultSet.getString("KODE_PERIODE"));
            value.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
            return value;
        });
    }
}

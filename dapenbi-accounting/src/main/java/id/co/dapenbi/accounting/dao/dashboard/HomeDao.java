package id.co.dapenbi.accounting.dao.dashboard;

import id.co.dapenbi.accounting.dto.dashboard.HomeDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanKeuangan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class HomeDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<HomeDTO.HomeChart> listAsetNetto(HomeDTO.HomeParameter params) {
        String query = "SELECT\n" +
                "   vda.URAIAN                  AS uraian,\n" +
                "   vda.KODE_PERIODE            AS kodePeriode,\n" +
                "   vda.KODE_THNBUKU            AS kodeTahunBuku,\n" +
                "   vda.KODE_DRI                AS kodedDRI,\n" +
                "   sum(vda.SALDO_BERJALAN)     AS totalSaldoBerjalan,\n" +
                "   sum(vda.SALDO_SEBELUM)      AS totalSaldoSebelum\n" +
                "FROM V_DASHBOARD_AN vda \n" +
                "WHERE vda.KODE_DRI = :kodeDRI\n" +
                "AND vda.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vda.KODE_THNBUKU = :kodeTahunBuku\n" +
                "GROUP BY vda.KODE_RUMUS, vda.URAIAN, vda.KODE_PERIODE, vda.KODE_THNBUKU, vda.KODE_DRI\n" +
                "order BY vda.KODE_PERIODE, vda.KODE_RUMUS asc\n";

//        log.info("Testing Periode: {}, Tahun Buku: {}, DRI: {}", params.getKodePeriode(), params.getKodeTahunBuku(), params.getKodeDRI());

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("kodePeriode"));
                    value.setPeriode(resultSet.getString("kodePeriode"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("totalSaldoBerjalan"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("totalSaldoSebelum"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listPerubahanAsetNetto(HomeDTO.HomeParameter params) {
        String query = "SELECT\n" +
                "   vdp.URAIAN                  AS uraian,\n" +
                "   vdp.KODE_PERIODE            AS kodePeriode,\n" +
                "   vdp.KODE_THNBUKU            AS kodeTahunBuku,\n" +
                "   vdp.KODE_DRI                AS kodedDRI,\n" +
                "   sum(vdp.SALDO_BERJALAN)     AS totalSaldoBerjalan,\n" +
                "   sum(vdp.SALDO_SEBELUM)      AS totalSaldoSebelum\n" +
                "FROM V_DASHBOARD_PAN vdp \n" +
                "WHERE vdp.KODE_DRI = :kodeDRI\n" +
                "AND vdp.KODE_PERIODE = :kodePeriode\n" +
                "AND vdp.KODE_THNBUKU = :kodeTahunBuku\n" +
                "GROUP BY vdp.KODE_RUMUS, vdp.URAIAN, vdp.KODE_PERIODE, vdp.KODE_THNBUKU, vdp.KODE_DRI\n" +
                "order BY vdp.KODE_PERIODE, vdp.KODE_RUMUS asc\n";

//        log.info("Testing Periode: {}, Tahun Buku: {}, DRI: {}", params.getKodePeriode(), params.getKodeTahunBuku(), params.getKodeDRI());

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("uraian"));
                    value.setPeriode(resultSet.getString("kodePeriode"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("totalSaldoBerjalan"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("totalSaldoSebelum"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listPerhitunganHasilUsaha(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdp.*\n" +
                "FROM V_DASHBOARD_PHU vdp \n" +
                "WHERE vdp.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdp.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdp.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdp.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setTotalPendapatan(resultSet.getBigDecimal("TOTAL_PENDAPATAN"));
                    value.setTotalBeban(resultSet.getBigDecimal("TOTAL_BEBAN"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listPerhitunganHasilUsahaPendapatan(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdpp.*\n" +
                "FROM V_DASHBOARD_PHU_PENDAPATAN vdpp \n" +
                "WHERE vdpp.KODE_PERIODE = :kodePeriode\n" +
                "AND vdpp.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdpp.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdpp.KODE_RUMUS ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listPerhitunganHasilUsahaBeban(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdpb.*\n" +
                "FROM V_DASHBOARD_PHU_BEBAN vdpb \n" +
                "WHERE vdpb.KODE_PERIODE = :kodePeriode\n" +
                "AND vdpb.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdpb.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdpb.KODE_RUMUS ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listNeraca(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdn.*\n" +
                "FROM V_DASHBOARD_NEC vdn \n" +
                "WHERE vdn.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdn.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdn.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdn.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listArusKas(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vda.*\n" +
                "FROM V_DASHBOARD_AK vda \n" +
                "WHERE vda.KODE_PERIODE = :kodePeriode\n" +
                "AND vda.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vda.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vda.KODE_RUMUS ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalSaldoBerjalan(resultSet.getBigDecimal("SALDO_BERJALAN"));
                    value.setTotalSaldoSebelum(resultSet.getBigDecimal("SALDO_SEBELUM"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listAnggaranTahunanAsetOperasional(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdaa.*\n" +
                "FROM V_DASHBOARD_AT_ASETOPR vdaa \n" +
                "WHERE vdaa.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdaa.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdaa.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdaa.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
                    value.setTotalRealisasi(resultSet.getBigDecimal("TOTAL_REALISASI"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listAnggaranTahunanBiaya(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdaa.*\n" +
                "FROM V_DASHBOARD_AT_BIAYA vdaa \n" +
                "WHERE vdaa.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdaa.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdaa.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdaa.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
                    value.setTotalRealisasi(resultSet.getBigDecimal("TOTAL_REALISASI"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listAnggaranTahunanPendapatan(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdaa.*\n" +
                "FROM V_DASHBOARD_AT_PENDAPATAN vdaa \n" +
                "WHERE vdaa.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdaa.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdaa.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdaa.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
                    value.setTotalRealisasi(resultSet.getBigDecimal("TOTAL_REALISASI"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }

    public List<HomeDTO.HomeChart> listROIROA(HomeDTO.HomeParameter params) {
        String query = "SELECT \n" +
                "   vdrr.*\n" +
                "FROM V_DASHBOARD_ROIROA vdrr \n" +
                "WHERE vdrr.KODE_PERIODE BETWEEN '01' AND :kodePeriode\n" +
                "AND vdrr.KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND vdrr.KODE_DRI = :kodeDRI\n" +
                "ORDER BY vdrr.KODE_PERIODE ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", params.getKodeDRI());
        map.addValue("kodePeriode", params.getKodePeriode());
        map.addValue("kodeTahunBuku", params.getKodeTahunBuku());

        try {
            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<HomeDTO.HomeChart>() {
                @Override
                public HomeDTO.HomeChart mapRow(ResultSet resultSet, int i) throws SQLException {
                    HomeDTO.HomeChart value = new HomeDTO.HomeChart();
                    value.setUraian(resultSet.getString("URAIAN"));
                    value.setPeriode(resultSet.getString("KODE_PERIODE"));
                    value.setTotalROI(resultSet.getBigDecimal("TOTAL_ROI"));
                    value.setTotalROA(resultSet.getBigDecimal("TOTAL_ROA"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return new ArrayList<>();
        }
    }
}

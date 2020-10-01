package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.laporan.ArusKasBulananDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
import id.co.dapenbi.accounting.entity.parameter.ArusKas;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LaporanPerincianArusKasDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ArusKasBulananDTO> datatables(DataTablesRequest<ArusKasBulananDTO> params, String search) {
        String query = "SELECT \n" +
                "    ROW_NUMBER() over (%s) as no,\n" +
                "    ID_ARUSKAS_BULANAN,\n" +
                "    TANGGAL,\n" +
                "    KODE_ARUSKAS,\n" +
                "    ID_ARUSKAS,\n" +
                "    KODE_THNBUKU,\n" +
                "    KODE_PERIODE, \n" +
                "    ID_REKENING,\n" +
                "    KODE_REKENING,\n" +
                "    NAMA_REKENING,\n" +
                "    KETERANGAN,\n" +
                "    KODE_DRI,\n" +
                "    FLAG_GROUP,\n" +
                "    COALESCE(SALDO, 0) AS SALDO,\n" +
                "    CREATED_BY,\n" +
                "    CREATED_DATE,\n" +
                "    COALESCE(SALDO_AWALTAHUN, 0) AS SALDO_AWALTAHUN,\n" +
                "    UPDATED_DATE,\n" +
                "    COALESCE(SALDO_SEBELUM, 0) AS SALDO_SEBELUM\n" +
                "FROM ACC_ARUSKAS_BULANAN\n" +
                "WHERE 1 = 1 \n";

        OrderingByColumns columns = new OrderingByColumns("KODE_REKENING", "NAMA_REKENING", "SALDO");
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanPerincianArusKasDao.ArusKasRincianDatatablesQueryComparator queryComparator = new LaporanPerincianArusKasDao.ArusKasRincianDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            TahunBuku tahunBuku = new TahunBuku();
            tahunBuku.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
            Periode periode = new Periode();
            periode.setKodePeriode(resultSet.getString("KODE_PERIODE"));
            ArusKasBulananDTO value = new ArusKasBulananDTO();
            value.setIdArusKasBulanan(resultSet.getInt("ID_ARUSKAS_BULANAN"));
            value.setTanggal(resultSet.getTimestamp("TANGGAL"));
            value.setKodeArusKas(resultSet.getString("KODE_ARUSKAS"));
            value.setIdArusKas(resultSet.getString("ID_ARUSKAS"));
            value.setKodeTahunBuku(tahunBuku);
            value.setKodePeriode(periode);
            value.setIdRekening(resultSet.getInt("ID_REKENING"));
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setKeterangan(resultSet.getString("KETERANGAN"));
            value.setKodeDRI(resultSet.getString("KODE_DRI"));
            value.setFlagGroup(resultSet.getString("FLAG_GROUP"));
            value.setSaldo(resultSet.getBigDecimal("SALDO"));
            value.setSaldoAwaltahun(resultSet.getBigDecimal("SALDO_AWALTAHUN"));
            value.setSaldoSebelumnya(resultSet.getBigDecimal("SALDO_SEBELUM"));
            value.setCreatedBy(resultSet.getString("CREATED_BY"));
            value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            return value;
        });
    }

    public Long datatables(ArusKasBulananDTO params, String search) {
        String baseQuery = "select count(*) as value_row\n" +
                "FROM ACC_ARUSKAS_BULANAN \n" +
                "where 1 = 1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        LaporanPerincianArusKasDao.ArusKasRincianDatatablesQueryComparator queryComparator = new LaporanPerincianArusKasDao.ArusKasRincianDatatablesQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        return this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
    }

    private class ArusKasRincianDatatablesQueryComparator implements QueryComparator<ArusKasBulananDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public ArusKasRincianDatatablesQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        public StringBuilder getQuerySearch(ArusKasBulananDTO params, String value) {
            if (params.getKodeTahunBuku() != null) {
                builder.append("and KODE_THNBUKU = :KODE_THNBUKU\n");
                map.addValue("KODE_THNBUKU", params.getKodeTahunBuku().getKodeTahunBuku());
            }

            if (params.getKodePeriode() != null) {
                builder.append("and KODE_PERIODE = :KODE_PERIODE\n");
                map.addValue("KODE_PERIODE", params.getKodePeriode().getKodePeriode());
            }

            if (!params.getKodeArusKas().isEmpty()) {
                builder.append("and KODE_ARUSKAS = :KODE_ARUSKAS\n");
                map.addValue("KODE_ARUSKAS", params.getKodeArusKas());
            }

            if (!params.getKodeDRI().isEmpty()) {
                builder.append("and KODE_DRI = :KODE_DRI\n");
                map.addValue("KODE_DRI", params.getKodeDRI());
            }

            if (params.getTanggal() != null) {
                builder.append("and TO_CHAR(TANGGAL, 'YYYY-MM-DD') = TO_CHAR(:TANGGAL, 'YYYY-MM-DD')\n");
                map.addValue("TANGGAL", params.getTanggal());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(SALDO) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public StringBuilder getQuery(ArusKasBulananDTO params) {
            return null;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<ArusKasDTO> findAllForExport(String kodePeriode) throws SQLException {
        String query = "" +
                "SELECT AK.KODE_ARUSKAS,\n" +
                "       AK.KETERANGAN,\n" +
                "       AK.STATUS_AKTIF,\n" +
                "       AK.CREATED_BY,\n" +
                "       AK.CREATED_DATE,\n" +
                "       AK.UPDATED_BY,\n" +
                "       AK.UPDATED_DATE,\n" +
                "       CASE\n" +
                "           WHEN lag(AK.ARUSKAS_AKTIVITAS) over (order by NULL) = AK.ARUSKAS_AKTIVITAS\n" +
                "               THEN NULL\n" +
                "           ELSE AK.ARUSKAS_AKTIVITAS\n" +
                "           END                                                        AS ARUSKAS_AKTIVITAS,\n" +
                "\n" +
                "       COALESCE((SELECT SUM(SALDO)\n" +
                "                 FROM ACC_ARUSKAS_BULANAN\n" +
                "                 WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                   AND FLAG_GROUP = '1'\n" +
                "                   AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggal), 0) AS TOTAL_PENAMBAHAN,\n" +
                "\n" +
                "       COALESCE((SELECT SUM(SALDO)\n" +
                "                 FROM ACC_ARUSKAS_BULANAN\n" +
                "                 WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                   AND FLAG_GROUP = '2'\n" +
                "                   AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggal), 0) AS TOTAL_PENGURANGAN,\n" +
                "\n" +
                "       (SELECT COUNT(*) ROWCOUNT\n" +
                "        FROM (SELECT AAR.KODE_ARUSKAS\n" +
                "              FROM ACC_ARUSKAS K\n" +
                "                       LEFT JOIN (SELECT KODE_ARUSKAS, FLAG_GROUP\n" +
                "                                  FROM ACC_ARUSKAS_BULANAN\n" +
                "                                  GROUP BY KODE_ARUSKAS, FLAG_GROUP) AAR\n" +
                "                                 on K.KODE_ARUSKAS = AAR.KODE_ARUSKAS) X\n" +
                "        WHERE X.KODE_ARUSKAS = AK.KODE_ARUSKAS)                       AS COUNTFLAG\n" +
                "FROM ACC_ARUSKAS AK\n" +
                "WHERE 1 = 1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("tanggal", kodePeriode);
        return namedParameterJdbcTemplate.query(query, map, (resultSet, i) -> {
            final List<ArusKasDTO.RincianArusKas> dataPenambahan = findRincianArusKas(1, resultSet.getString("KODE_ARUSKAS"), kodePeriode);
            final List<ArusKasDTO.RincianArusKas> dataPengurangan = findRincianArusKas(2, resultSet.getString("KODE_ARUSKAS"), kodePeriode);

            ArusKasDTO value = new ArusKasDTO();
            value.setKodeArusKas(resultSet.getString("KODE_ARUSKAS"));
            value.setArusKasAktivitas(resultSet.getString("ARUSKAS_AKTIVITAS"));
            value.setKeterangan(resultSet.getString("KETERANGAN"));
            value.setTotalPenambahan(resultSet.getBigDecimal("TOTAL_PENAMBAHAN"));
            value.setTotalPengurangan(resultSet.getBigDecimal("TOTAL_PENGURANGAN"));
            value.setTotal(value.getTotalPenambahan().subtract(value.getTotalPengurangan()));
            value.setDataPenambahan(dataPenambahan);
            value.setDataPengurangan(dataPengurangan);
            return value;
        });
    }

    public List<ArusKasDTO.RincianArusKas> findRincianArusKas(Integer flagGroup, String kodeArusKas, String periode) throws SQLException {
        String query = "SELECT COALESCE(AR.KODE_REKENING, '-') AS KODE_REKENING,\n" +
                "       COALESCE(AR.NAMA_REKENING, '-') AS NAMA_REKENING,\n" +
                "       AAR.KETERANGAN,\n" +
                "       AAR.ID_REKENING,\n" +
                "       AAR.KODE_ARUSKAS,\n" +
                "       AAR.SALDO\n" +
                "FROM ACC_ARUSKAS_BULANAN AAR\n" +
                "         LEFT JOIN ACC_REKENING AR on AAR.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE AAR.FLAG_GROUP = :FLAG_GROUP\n" +
                "  AND AAR.KODE_ARUSKAS = :KODE_ARUSKAS\n" +
                "  AND TO_CHAR(AAR.TANGGAL, 'YYYY-MM-DD') = :tglTransaksi\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("FLAG_GROUP", flagGroup);
        params.addValue("KODE_ARUSKAS", kodeArusKas);
        params.addValue("tglTransaksi", periode);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            ArusKasDTO.RincianArusKas rincian = new ArusKasDTO.RincianArusKas();
            rincian.setIdRekening(resultSet.getInt("ID_REKENING"));
            rincian.setKodeRekening(resultSet.getString("KODE_REKENING"));
            rincian.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            rincian.setKeterangan(resultSet.getString("KETERANGAN"));
            rincian.setSaldo(resultSet.getBigDecimal("SALDO"));
            return rincian;
        });
    }

    public ArusKasDTO.TotalKas findTotalKas(String tanggal) {
        String query = "SELECT (SELECT SUM(SALDO_BERJALAN)\n" +
                "        FROM LAP_KEU\n" +
                "        WHERE KODE_RUMUS = 'A26'\n" +
                "          AND TO_CHAR(TGL_LAPORAN, 'YYYY-MM-DD') = :tanggalLaporan) AS kasAkhirPeriode,\n" +
                "\n" +
                "       (SELECT SUM(SALDO_SEBELUM)\n" +
                "        FROM LAP_KEU\n" +
                "        WHERE KODE_RUMUS = 'A26'\n" +
                "          AND TO_CHAR(TGL_LAPORAN, 'YYYY-MM-DD') = :tanggalLaporan) AS kasAwalPeriode,\n" +
                "\n" +
                "       (SELECT (SUM(AKB.TOTAL_PENAMBAHAN) - SUM(AKB.TOTAL_PENGURANGAN)) AS TOTAL_SALDO\n" +
                "        FROM (SELECT " +
                "AK.KODE_ARUSKAS,\n" +
                "                     AK.ARUSKAS_AKTIVITAS,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '1'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENAMBAHAN,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '2'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENGURANGAN\n" +
                "              FROM ACC_ARUSKAS AK) AKB\n" +
                "        WHERE AKB.ARUSKAS_AKTIVITAS = 'INVESTASI')                  AS TOTAL_ARUS_KAS_INVESTASI,\n" +
                "\n" +
                "       (SELECT (SUM(AKB.TOTAL_PENAMBAHAN) - SUM(AKB.TOTAL_PENGURANGAN)) AS TOTAL_SALDO\n" +
                "        FROM (SELECT AK.KODE_ARUSKAS,\n" +
                "                     AK.ARUSKAS_AKTIVITAS,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '1'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENAMBAHAN,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '2'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENGURANGAN\n" +
                "              FROM ACC_ARUSKAS AK) AKB\n" +
                "        WHERE AKB.ARUSKAS_AKTIVITAS = 'OPERASIONAL')                  AS TOTAL_ARUS_KAS_OPERASIONAL,\n" +
                "\n" +
                "       (SELECT (SUM(AKB.TOTAL_PENAMBAHAN) - SUM(AKB.TOTAL_PENGURANGAN)) AS TOTAL_SALDO\n" +
                "        FROM (SELECT AK.KODE_ARUSKAS,\n" +
                "                     AK.ARUSKAS_AKTIVITAS,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '1'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENAMBAHAN,\n" +
                "                     COALESCE((SELECT SUM(SALDO)\n" +
                "                               FROM ACC_ARUSKAS_BULANAN\n" +
                "                               WHERE KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "                                 AND FLAG_GROUP = '2'\n" +
                "                                 AND TO_CHAR(TANGGAL, 'YYYY-MM-DD') = :tanggalLaporan), 0) AS TOTAL_PENGURANGAN\n" +
                "              FROM ACC_ARUSKAS AK) AKB\n" +
                "        WHERE AKB.ARUSKAS_AKTIVITAS = 'PENDANAAN')                  AS TOTAL_ARUS_KAS_PENDANAAN\n" +
                "FROM dual \n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tanggalLaporan", tanggal);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> {
            ArusKasDTO.TotalKas value = new ArusKasDTO.TotalKas();
            final BigDecimal totalArusKasInvestasi = resultSet.getBigDecimal("TOTAL_ARUS_KAS_INVESTASI");
            final BigDecimal totalArusKasOperasional = resultSet.getBigDecimal("TOTAL_ARUS_KAS_OPERASIONAL");
            final BigDecimal totalArusKasPendanaan = resultSet.getBigDecimal("TOTAL_ARUS_KAS_PENDANAAN");
            value.setKasAktivitasInvestasi(totalArusKasInvestasi);
            value.setKasAktivitasOperasional(totalArusKasOperasional);
            value.setKasAktivitasPendanaan(totalArusKasPendanaan);
            value.setTotalKasAktivitas(totalArusKasInvestasi.add(totalArusKasOperasional).add(totalArusKasPendanaan));
            value.setKasAkhirPeriode(resultSet.getBigDecimal("kasAkhirPeriode"));
            value.setKasAwalPeriode(resultSet.getBigDecimal("kasAwalPeriode"));
            return value;
        });
    }
}

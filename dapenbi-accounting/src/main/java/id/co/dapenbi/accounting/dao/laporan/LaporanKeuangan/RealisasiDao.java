package id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RealisasiDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RealisasiDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class RealisasiDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<RealisasiDTO> findRealisasi(String jenisRealisasi, String month) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT x.*\n" +
                "     , x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG AS NILAI_AT\n" +
                "     , (x.TOTAL_REALISASI - x.REALISASI_BULAN_LALU)         as REALISASI_BULAN_INI\n" +
                "     , CASE\n" +
                "           WHEN x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG > 0\n" +
                "               THEN x.TOTAL_REALISASI / (x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG) * 100\n" +
                "           ELSE 0\n" +
                "    END                                                     AS PERSEN_REALISASI\n" +
                "FROM" +
                " (\n" +
                "         SELECT AR.KODE_REKENING               AS KODE_REKENING,\n" +
                "                AR.NAMA_REKENING               AS NAMA_REKENING,\n" +
                "                (\n" +
                "                    SELECT COALESCE(MAX(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                "                    FROM ACC_SALDO\n" +
                "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGALMIN -- pamater inputan:PTANGGAL bulan -1\n" +
                "                )                              AS REALISASI_BULAN_LALU,\n" +
                "                (\n" +
                "                    SELECT COALESCE(MAX(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                "                    FROM ACC_SALDO\n" +
                "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                )                              AS TOTAL_REALISASI,\n" +
                "                (\n" +
                "                    SELECT COALESCE(MAX(ACC_SALDO.SERAP_TAMBAH), 0)\n" +
                "                    FROM ACC_SALDO\n" +
                "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                )                              AS SERAP_TAMBAH,\n" +
                "                (\n" +
                "                    SELECT COALESCE(MAX(ACC_SALDO.SERAP_KURANG), 0)\n" +
                "                    FROM ACC_SALDO\n" +
                "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                )                              AS SERAP_KURANG,\n" +
                "                (\n" +
                "                    SELECT COALESCE(MAX(ACC_SALDO.NILAI_ANGGARAN), 0)\n" +
                "                    FROM ACC_SALDO\n" +
                "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                )                             " +
                " AS ANGGARAN_TAHUNAN\n" +
                "         FROM ACC_REKENING AR\n" +
                "                  LEFT JOIN ACC_ANGGARAN AA ON AR.ID_REKENING = AA.ID_REKENING AND AA.STATUS_AKTIF = '1'\n" +
                "         WHERE AR.LEVEL_REKENING IN (3, 6) \n");
        if (jenisRealisasi.equalsIgnoreCase("PENDAPATAN")) {
            builder.append(" AND AR.TIPE_REKENING IN ('BIAYA', 'ASET_OPR') \n");
        } else builder.append(" AND AR.TIPE_REKENING = 'PENDAPATAN' \n");
        builder.append(" ORDER BY AR.KODE_REKENING) x");
        String queryFinal = builder.toString();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("PTANGGALMIN", month);
        params.addValue("PTANGGAL", month);
        return namedParameterJdbcTemplate.query(queryFinal, params, (resultSet, i) -> {
            RealisasiDTO value = new RealisasiDTO();
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setRealisasiBulanIni(resultSet.getBigDecimal("REALISASI_BULAN_INI"));
            value.setRealisasiBulanLalu(resultSet.getBigDecimal("REALISASI_BULAN_LALU"));
            value.setTotalRealisasi(resultSet.getBigDecimal("TOTAL_REALISASI"));
            value.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
            value.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
            value.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
            value.setNilaiAT(resultSet.getBigDecimal("NILAI_AT"));
            value.setPersen(resultSet.getFloat("PERSEN_REALISASI"));
            return value;
        });
    }

    public RealisasiDTO.Summary findSummeryRealisasi(
            String periode,
            String periodeMin1,
            Integer idRekening,
            String jenisRealisasi,
            String tipeRekening) {
        String queryPendapatan = "SELECT SUM(Y.REALISASI_BULAN_LALU) AS REALISASI_BULAN_LALU,\n" +
                "       SUM(Y.NILAI_AT)             AS NILAI_AT,\n" +
                "       SUM(Y.REALISASI_BULAN_INI)  AS REALISASI_BULAN_INI,\n" +
                "       SUM(Y.SERAP_KURANG)         AS SERAP_KURANG,\n" +
                "       SUM(Y.SERAP_TAMBAH)         AS SERAP_TAMBAH,\n" +
                "       SUM(Y.ANGGARAN_TAHUNAN)     AS ANGGARAN_TAHUNAN,\n" +
                "       SUM(Y.SALDO_ANGGARAN)       AS SALDO_ANGGARAN,\n" +
                "       SUM(Y.TOTAL_REALISASI)      AS TOTAL_REALISASI,\n" +
                "       SUM(Y.TOTAL_REALISASI) / SUM(Y.NILAI_AT) * 100 AS PERSEN\n" +
                "FROM (SELECT x.*,\n" +
                "             x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG AS NILAI_AT,\n" +
                "             (x.TOTAL_REALISASI - x.REALISASI_BULAN_LALU)         AS REALISASI_BULAN_INI\n" +
                "      from (\n" +
                "               SELECT AR.KODE_REKENING  AS KODE_REKENING,\n" +
                "                      AR.NAMA_REKENING  AS NAMA_REKENING,\n" +
                "                      AR.LEVEL_REKENING AS LEVEL_REKENING,\n" +
                "                      (CASE\n" +
                "                           WHEN LEVEL_REKENING = 3 THEN TO_CHAR(ar.ID_REKENING)\n" +
                "                           ELSE\n" +
                "                               (SELECT (SELECT arlvl4.ID_PARENT\n" +
                "                                        FROM ACC_REKENING arlvl4\n" +
                "                                        WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT)\n" +
                "                                FROM ACC_REKENING arlvl5\n" +
                "                                WHERE arlvl5.ID_REKENING = AR.ID_PARENT\n" +
                "                               )\n" +
                "                          END)          AS IDREKENINGLVL3,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGALMIN1 -- pamater inputan:PTANGGAL bulan -1\n" +
                "                      )                 AS REALISASI_BULAN_LALU,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS TOTAL_REALISASI,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SERAP_TAMBAH), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SERAP_TAMBAH,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SERAP_KURANG), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SERAP_KURANG,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.NILAI_ANGGARAN), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS ANGGARAN_TAHUNAN,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SALDO_ANGGARAN), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SALDO_ANGGARAN\n" +
                "               FROM ACC_REKENING AR\n" +
                "               WHERE AR.LEVEL_REKENING IN (6)\n" +
                "                 AND (:ID_REKENING = -1 OR AR.ID_REKENING = :ID_REKENING)\n" +
                "                 AND AR.TIPE_REKENING IN (:TIPE_REKENING)\n" +
                "           ) x\n" +
                "      WHERE 1 = 1) Y ";
        String queryPengeluaran = "SELECT SUM(Y.REALISASI_BULAN_LALU)                    AS REALISASI_BULAN_LALU,\n" +
                "       SUM(Y.NILAI_AT)                                AS NILAI_AT,\n" +
                "       SUM(Y.REALISASI_BULAN_INI)                     AS REALISASI_BULAN_INI,\n" +
                "       SUM(Y.SERAP_KURANG)                            AS SERAP_KURANG,\n" +
                "       SUM(Y.SERAP_TAMBAH)                            AS SERAP_TAMBAH,\n" +
                "       SUM(Y.ANGGARAN_TAHUNAN)                        AS ANGGARAN_TAHUNAN,\n" +
                "       SUM(Y.SALDO_ANGGARAN)                          AS SALDO_ANGGARAN,\n" +
                "       SUM(Y.TOTAL_REALISASI)                         AS TOTAL_REALISASI,\n" +
                "       SUM(Y.TOTAL_REALISASI) / SUM(Y.NILAI_AT) * 100 AS PERSEN\n" +
                "FROM (SELECT x.*,\n" +
                "             x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG AS NILAI_AT,\n" +
                "             (x.TOTAL_REALISASI - x.REALISASI_BULAN_LALU)         AS REALISASI_BULAN_INI\n" +
                "      from (\n" +
                "               SELECT AR.KODE_REKENING  AS KODE_REKENING,\n" +
                "                      AR.NAMA_REKENING  AS NAMA_REKENING,\n" +
                "                      AR.LEVEL_REKENING AS LEVEL_REKENING,\n" +
                "                      (CASE\n" +
                "                           WHEN LEVEL_REKENING = 3 THEN TO_CHAR(ar.ID_REKENING)\n" +
                "                           ELSE\n" +
                "                               (SELECT (SELECT arlvl4.ID_PARENT\n" +
                "                                        FROM ACC_REKENING arlvl4\n" +
                "                                        WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT)\n" +
                "                                FROM ACC_REKENING arlvl5\n" +
                "                                WHERE arlvl5.ID_REKENING = AR.ID_PARENT\n" +
                "                               )\n" +
                "                          END)          AS IDREKENINGLVL3,\n" +
                "                      (\n" +
                "                          SELECT CASE\n" +
                "                                     WHEN AR.SALDO_NORMAL = 'D' AND AR.TIPE_REKENING = 'ASET_OPR' THEN\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) +\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_JUAL), 0)\n" +
                "                                     WHEN AR.SALDO_NORMAL = 'D' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                "                                                                     COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                "                                     ELSE COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) -\n" +
                "                                          COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) END\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGALMIN1 -- pamater inputan:PTANGGAL bulan -1\n" +
                "                      )                 AS REALISASI_BULAN_LALU,\n" +
                "                      (\n" +
                "                          SELECT CASE\n" +
                "                                     WHEN AR.SALDO_NORMAL = 'D' AND AR.TIPE_REKENING = 'ASET_OPR' THEN\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) +\n" +
                "                                                 COALESCE(SUM(ACC_SALDO.SALDO_JUAL), 0)\n" +
                "                                     WHEN AR.SALDO_NORMAL = 'D' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                "                                                                     COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                "                                     ELSE COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) -\n" +
                "                                          COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) END\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS TOTAL_REALISASI,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SERAP_TAMBAH), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SERAP_TAMBAH,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SERAP_KURANG), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SERAP_KURANG,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.NILAI_ANGGARAN), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS ANGGARAN_TAHUNAN,\n" +
                "                      (\n" +
                "                          SELECT COALESCE(SUM(ACC_SALDO.SALDO_ANGGARAN), 0)\n" +
                "                          FROM ACC_SALDO\n" +
                "                          WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                "                            AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                "                      )                 AS SALDO_ANGGARAN\n" +
                "               FROM ACC_REKENING AR\n" +
                "               WHERE AR.LEVEL_REKENING IN (6)\n" +
                "                 AND (:ID_REKENING = -1 OR AR.ID_REKENING = :ID_REKENING)\n" +
                "                 AND AR.TIPE_REKENING IN (:TIPE_REKENING)\n" +
                "           ) x\n" +
                "      WHERE 1 = 1) Y ";
        String query = jenisRealisasi.equalsIgnoreCase("PENGELUARAN") ? queryPengeluaran : queryPendapatan;
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("ID_REKENING", (idRekening == null) ? -1 : idRekening);
        map.addValue("PTANGGALMIN1", periodeMin1);
        map.addValue("PTANGGAL", periode);
        ArrayList<String> tipeRekenings = new ArrayList<String>();
        if (tipeRekening.equalsIgnoreCase("PENDAPATAN")) {
            tipeRekenings.add("PENDAPATAN");
            map.addValue("TIPE_REKENING", tipeRekenings);
        } else {
            if (tipeRekening.isEmpty()) {
                tipeRekenings.add("BIAYA");
                tipeRekenings.add("ASET_OPR");
            } else {
                tipeRekenings.add(tipeRekening);
            }
            map.addValue("TIPE_REKENING", tipeRekenings);
        }
        return namedParameterJdbcTemplate.queryForObject(query, map, (resultSet, i) -> {
            RealisasiDTO.Summary value = new RealisasiDTO.Summary();
            final BigDecimal totalTotalRealisasi = resultSet.getBigDecimal("TOTAL_REALISASI");
            final BigDecimal totalNilaiAt = resultSet.getBigDecimal("NILAI_AT");
            value.setTotalAnggaran(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
            value.setTotalNilaiAT(totalNilaiAt);
            value.setTotalRealisasiBulanIni(resultSet.getBigDecimal("REALISASI_BULAN_INI"));
            value.setTotalRealisasiBulanLalu(resultSet.getBigDecimal("REALISASI_BULAN_LALU"));
            value.setTotalRealisasiSum(totalTotalRealisasi);
            value.setTotalPercentage(resultSet.getBigDecimal("PERSEN"));
            return value;
        });
    }

    public List<RealisasiDTO> datatable(DataTablesRequest<RealisasiDTO> params, String search) {
        //language=Oracle
        String queryRealisasiPendapatan =
                "SELECT ROW_NUMBER() over (order by ROWNUM)                  as no,\n" +
                        "       x.*,\n" +
                        "       x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG AS NILAI_AT,\n" +
                        "       (x.TOTAL_REALISASI - x.REALISASI_BULAN_LALU)         AS REALISASI_BULAN_INI,\n" +
                        "       case\n" +
                        "           when x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG > 0\n" +
                        "               then x.TOTAL_REALISASI / (x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG) * 100\n" +
                        "           else 0\n" +
                        "           end                                              AS PERSEN_REALISASI\n" +
                        "from (\n" +
                        "         SELECT AR.KODE_REKENING               AS KODE_REKENING,\n" +
                        "                AR.NAMA_REKENING               AS NAMA_REKENING,\n" +
                        "                AR.LEVEL_REKENING              AS LEVEL_REKENING,\n" +
                        "                AR.SALDO_NORMAL                AS SALDO_NORMAL,\n" +
                        "                (CASE \n" +
                        "                   WHEN LEVEL_REKENING = 3 THEN TO_CHAR(ar.ID_REKENING)\n" +
                        "                   ELSE \n" +
                        "                       (SELECT \n" +
                        "                           (SELECT arlvl4.ID_PARENT FROM ACC_REKENING arlvl4 WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT)\n" +
                        "                            FROM ACC_REKENING arlvl5 WHERE arlvl5.ID_REKENING = AR.ID_PARENT\n" +
                        "                       )\n" +
                        "                 END ) AS IDREKENINGLVL3," +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGALMIN1 -- pamater inputan:PTANGGAL bulan -1\n" +
                        "                )                              AS REALISASI_BULAN_LALU,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_AKHIR), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                              AS TOTAL_REALISASI,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SERAP_TAMBAH), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                              AS SERAP_TAMBAH,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SERAP_KURANG), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                              AS SERAP_KURANG,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.NILAI_ANGGARAN), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                              AS ANGGARAN_TAHUNAN,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_ANGGARAN), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                              AS SALDO_ANGGARAN,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_DEBET,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_KREDIT\n" +
                        "         FROM ACC_REKENING AR\n" +
                        "         WHERE AR.LEVEL_REKENING IN (3, 6) AND (:ID_REKENING = -1 OR AR.ID_REKENING=:ID_REKENING) \n" +
                        "           AND AR.TIPE_REKENING IN (:TIPE_REKENING)\n" +
                        "     ) x WHERE 1=1 ";

        //language=Oracle
        String queryRealisasiPengeluaran =
                "SELECT ROW_NUMBER() over (order by ROWNUM)                  as no,\n" +
                        "       x.*,\n" +
                        "       x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG AS NILAI_AT,\n" +
                        "       (x.TOTAL_REALISASI - x.REALISASI_BULAN_LALU)         AS REALISASI_BULAN_INI,\n" +
                        "       case\n" +
                        "           when x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG > 0\n" +
                        "               then x.TOTAL_REALISASI / (x.ANGGARAN_TAHUNAN + x.SERAP_TAMBAH - x.SERAP_KURANG) * 100\n" +
                        "           else 0\n" +
                        "           end                                              AS PERSEN_REALISASI\n" +
                        "from (\n" +
                        "         SELECT AR.KODE_REKENING  AS KODE_REKENING,\n" +
                        "                AR.NAMA_REKENING  AS NAMA_REKENING,\n" +
                        "                AR.LEVEL_REKENING AS LEVEL_REKENING,\n" +
                        "                AR.SALDO_NORMAL                AS SALDO_NORMAL,\n" +
                        "                (CASE\n" +
                        "                     WHEN LEVEL_REKENING = 3 THEN TO_CHAR(ar.ID_REKENING)\n" +
                        "                     ELSE\n" +
                        "                         (SELECT (SELECT arlvl4.ID_PARENT\n" +
                        "                                  FROM ACC_REKENING arlvl4\n" +
                        "                                  WHERE arlvl4.ID_REKENING = arlvl5.ID_PARENT)\n" +
                        "                          FROM ACC_REKENING arlvl5\n" +
                        "                          WHERE arlvl5.ID_REKENING = AR.ID_PARENT\n" +
                        "                         )\n" +
                        "                    END)          AS IDREKENINGLVL3,\n" +
                        "                (\n" +
                        "                    SELECT CASE\n" +
                        "                                     WHEN AR.SALDO_NORMAL = 'D' AND AR.TIPE_REKENING = 'ASET_OPR' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                        "                                                                             COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) +\n" +
                        "                                                                             COALESCE(SUM(ACC_SALDO.SALDO_JUAL), 0)\n" +
                        "                                     WHEN AR.SALDO_NORMAL = 'D' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                        "                                                                     COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                        "                               ELSE COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) -\n" +
                        "                                    COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) END\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMM') = :PTANGGALMIN1 -- pamater inputan:PTANGGAL bulan -1\n" +
                        "                )                 AS REALISASI_BULAN_LALU,\n" +
                        "                (\n" +
                        "                    SELECT CASE \n" +
                        "                                     WHEN AR.SALDO_NORMAL = 'D' AND AR.TIPE_REKENING = 'ASET_OPR' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                        "                                                                             COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) +\n" +
                        "                                                                             COALESCE(SUM(ACC_SALDO.SALDO_JUAL), 0)\n" +
                        "                                     WHEN AR.SALDO_NORMAL = 'D' THEN COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) -\n" +
                        "                                                                     COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                        "                               ELSE COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0) -\n" +
                        "                                    COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0) END\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS TOTAL_REALISASI,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SERAP_TAMBAH), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SERAP_TAMBAH,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SERAP_KURANG), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SERAP_KURANG,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.NILAI_ANGGARAN), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS ANGGARAN_TAHUNAN,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_ANGGARAN), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_ANGGARAN,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_DEBET), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_DEBET,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_KREDIT), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_KREDIT,\n" +
                        "                (\n" +
                        "                    SELECT COALESCE(SUM(ACC_SALDO.SALDO_JUAL), 0)\n" +
                        "                    FROM ACC_SALDO\n" +
                        "                    WHERE ACC_SALDO.KODE_REKENING LIKE AR.KODE_REKENING || '%'\n" +
                        "                      AND TO_CHAR(ACC_SALDO.TGL_SALDO, 'YYYYMMDD') = :PTANGGAL -- parameter inpurtan :PTANGGAL di format YYYYMM\n" +
                        "                )                 AS SALDO_JUAL\n" +
                        "         FROM ACC_REKENING AR\n" +
                        "         WHERE AR.LEVEL_REKENING IN (3, 6)\n" +
                        "           AND (:ID_REKENING = -1 OR AR.ID_REKENING = :ID_REKENING)\n" +
                        "           AND AR.TIPE_REKENING IN (:TIPE_REKENING)\n" +
                        "     ) x\n" +
                        "WHERE 1 = 1 ";

        String query = params.getValue().getJenisRealisasi().equalsIgnoreCase("PENGELUARAN") ? queryRealisasiPengeluaran : queryRealisasiPendapatan;
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("ID_REKENING", params.getValue().getIdRekening().isEmpty() ? -1 : params.getValue().getIdRekening());
        map.addValue("PTANGGALMIN1", params.getValue().getPeriodeMin1());
        map.addValue("PTANGGAL", params.getValue().getPeriode());
        ArrayList<String> tipeRekenings = new ArrayList<String>();
        if (params.getValue().getJenisRealisasi().equalsIgnoreCase("PENDAPATAN")) {
            tipeRekenings.add("PENDAPATAN");
            map.addValue("TIPE_REKENING", tipeRekenings);
        } else {
            if (params.getValue().getTipeRekening().isEmpty()) {
                tipeRekenings.add("BIAYA");
                tipeRekenings.add("ASET_OPR");
            } else {
                tipeRekenings.add(params.getValue().getTipeRekening());
            }
            map.addValue("TIPE_REKENING", tipeRekenings);
        }

        RealisasiDao.DatatablesRealisasiQueryComparator queryComparator = new RealisasiDao.DatatablesRealisasiQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

//        OrderingByColumns columns = new OrderingByColumns("KODE_REKENING", "NAMA_REKENING", "LEVEL_REKENING");
//        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder ()));
        stringBuilder.append("ORDER BY IDREKENINGLVL3 ASC, LEVEL_REKENING DESC, KODE_REKENING asc");

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        log.info("{}", finalQuery);
        return namedParameterJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            RealisasiDTO value = new RealisasiDTO();
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
            value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
            value.setRealisasiBulanIni(resultSet.getBigDecimal("REALISASI_BULAN_INI"));
            value.setRealisasiBulanLalu(resultSet.getBigDecimal("REALISASI_BULAN_LALU"));
            value.setTotalRealisasi(resultSet.getBigDecimal("TOTAL_REALISASI"));
            value.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
            value.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
            value.setAnggaranTahunan(resultSet.getBigDecimal("ANGGARAN_TAHUNAN"));
            value.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
            value.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
            value.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
            value.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
            value.setNilaiAT(resultSet.getBigDecimal("NILAI_AT"));
            value.setPersen(resultSet.getFloat("PERSEN_REALISASI"));
            value.setSaldoJual(resultSet.getBigDecimal("SALDO_JUAL"));
            return value;
        });
    }

    public Long datatable(RealisasiDTO params, String search) {
        //language=Oracle
        String query =
                "SELECT COUNT(*) value_row\n" +
                        "FROM (\n" +
                        "         SELECT AR.KODE_REKENING               AS KODE_REKENING,\n" +
                        "                AR.NAMA_REKENING               AS NAMA_REKENING\n" +
                        "         FROM ACC_REKENING AR\n" +
                        "         WHERE AR.LEVEL_REKENING IN (3, 6) \n" +
                        "           AND AR.TIPE_REKENING IN (:TIPE_REKENING)\n" +
                        "         ORDER BY AR.KODE_REKENING\n" +
                        "     ) x WHERE 1=1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("ID_REKENING", params.getIdRekening());
        ArrayList<String> tipeRekenings = new ArrayList<String>();
        if (params.getJenisRealisasi().equalsIgnoreCase("PENDAPATAN")) {
            tipeRekenings.add("PENDAPATAN");
            map.addValue("TIPE_REKENING", tipeRekenings);
        } else {
            tipeRekenings.add("BIAYA");
            tipeRekenings.add("ASET_OPR");
            map.addValue("TIPE_REKENING", "PENDAPATAN");
        }

        RealisasiDao.DatatablesRealisasiQueryComparator queryComparator = new RealisasiDao.DatatablesRealisasiQueryComparator(query.toString(), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesRealisasiQueryComparator implements QueryComparator<RealisasiDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesRealisasiQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(RealisasiDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(RealisasiDTO params, String value) {
            if (!value.isEmpty()) {
                builder.append("and (lower(KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(NAMA_REKENING) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }
}

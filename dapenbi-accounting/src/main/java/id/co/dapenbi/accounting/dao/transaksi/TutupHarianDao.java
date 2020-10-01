package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.TutupHarianDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Repository
public class TutupHarianDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TutupHarianDTO getTutupHarian(Timestamp tglTransaksi) {
        //language=Oracle
        String query = "SELECT a.totalWarkat, b.totalWarkatValid, c.totalWarkatPA, d.totalWarkatFA\n" +
                "FROM (select COUNT(*) as totalWarkat\n" +
                "      from ACC_WARKAT\n" +
                "      WHERE TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') = :tglTransaksi\n" +
                "        AND STATUS_DATA IN ('VALID', 'FA', 'PA', 'SUBMIT') AND JENIS_WARKAT = 'WARKAT') a,\n" +
                "     (select COUNT(*) as totalWarkatValid\n" +
                "      from ACC_WARKAT\n" +
                "      WHERE TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') = :tglTransaksi\n" +
                "        AND STATUS_DATA IN ('VALID', 'FA', 'PA') AND JENIS_WARKAT = 'WARKAT') b,\n" +
                "     (select COUNT(*) as totalWarkatPA\n" +
                "      from ACC_WARKAT\n" +
                "      WHERE TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') = :tglTransaksi\n" +
                "        AND STATUS_DATA IN ('PA', 'FA') AND JENIS_WARKAT = 'WARKAT') c,\n" +
                "     (select COUNT(*) as totalWarkatFA\n" +
                "      from ACC_WARKAT\n" +
                "      WHERE TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') = :tglTransaksi\n" +
                "        AND STATUS_DATA = 'FA' AND JENIS_WARKAT = 'WARKAT') d";

        MapSqlParameterSource map = new MapSqlParameterSource();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        map.addValue("tglTransaksi", formatter.format(tglTransaksi));
        return namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<TutupHarianDTO>() {
            @Override
            public TutupHarianDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                TutupHarianDTO tutupHarianDTO = new TutupHarianDTO();
                tutupHarianDTO.setTotalWarkat(resultSet.getInt("TOTALWARKAT"));
                tutupHarianDTO.setTotalWarkatValid(resultSet.getInt("TOTALWARKATVALID"));
                tutupHarianDTO.setTotalWarkatPA(resultSet.getInt("TOTALWARKATPA"));
                tutupHarianDTO.setTotalWarkatFA(resultSet.getInt("TOTALWARKATFA"));
                return tutupHarianDTO;
            }
        });
    }

    public int updateACCParameter() throws Exception {
        //language=Oracle
        String query = "UPDATE ACC_PARAMETER SET STATUS_OPEN = 'C' WHERE STATUS_AKTIF = '1'";

        MapSqlParameterSource map = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.update(query, map);
    }

    public int retrieveSaldoFAToSaldo(TutupHarianDTO request, String user) throws Exception {
        //language=Oracle
        String query = "INSERT INTO ACC_SALDO(\n" +
                "    ID_REKENING,\n" +
                "    KODE_REKENING,\n" +
                "    NAMA_REKENING,\n" +
                "    KODE_THNBUKU,\n" +
                "    KODE_PERIODE,\n" +
                "    TGL_SALDO,\n" +
                "    SALDO_AWAL,\n" +
                "    SALDO_DEBET,\n" +
                "    SALDO_KREDIT,\n" +
                "    SALDO_AKHIR,\n" +
                "    NILAI_ANGGARAN,\n" +
                "    SERAP_TAMBAH,\n" +
                "    SERAP_KURANG,\n" +
                "    SALDO_ANGGARAN,\n" +
                "    KODE_DRI,\n" +
                "    CREATED_BY,\n" +
                "    CREATED_DATE)\n" +
                "SELECT ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       :KODE_THNBUKU AS KODE_THNBUKU,\n" +
                "       :KODE_PERIODE AS KODE_PERIODE,\n" +
                "       (SELECT TGL_TRANSAKSI FROM ACC_PARAMETER WHERE STATUS_AKTIF = '1') AS TGL_SALDO,\n" +
                "       SALDO_AWAL,\n" +
                "       SALDO_DEBET,\n" +
                "       SALDO_KREDIT,\n" +
                "       SALDO_AKHIR,\n" +
                "       NILAI_ANGGARAN,\n" +
                "       SERAP_TAMBAH,\n" +
                "       SERAP_KURANG,\n" +
                "       SALDO_ANGGARAN,\n" +
                "       '1' AS KODE_DRI,\n" +
                "       :CREATED_BY AS CREATED_BY,\n" +
                "       CURRENT_DATE AS CREATED_DATE\n" +
                "FROM ACC_SALDO_FA ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_THNBUKU", request.getKodeThnBuku());
        map.addValue("KODE_PERIODE", request.getKodePeriode());
        map.addValue("CREATED_BY", user);
        return namedParameterJdbcTemplate.update(query, map);
    }
    
    public int deleteByTglSaldo() {
        //language=Oracle
        String query = "DELETE\n" +
                "FROM ACC_SALDO\n" +
                "WHERE TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') =\n" +
                "      (SELECT TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') FROM ACC_PARAMETER WHERE STATUS_AKTIF = '1') ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.update(query, map);
    }
}

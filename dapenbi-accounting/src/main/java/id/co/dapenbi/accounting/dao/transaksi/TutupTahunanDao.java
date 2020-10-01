package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.TutupTahunanDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TutupTahunanDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TutupTahunanDTO getTutupTahunan(String jenisWarkat) {
        //language=Oracle
        String query = "SELECT a.totalWarkat, b.totalWarkatValid, c.totalWarkatPA, d.totalWarkatFA\n" +
                "FROM (select COUNT(*) as totalWarkat from ACC_WARKAT WHERE JENIS_WARKAT = :jenisWarkat AND STATUS_DATA IN ('VALID', 'FA', 'PA', 'SUBMIT')) a,\n" +
                "     (select COUNT(*) as totalWarkatValid from ACC_WARKAT WHERE JENIS_WARKAT = :jenisWarkat AND STATUS_DATA IN ('VALID', 'FA', 'PA')) b,\n" +
                "     (select COUNT(*) as totalWarkatPA from ACC_WARKAT WHERE JENIS_WARKAT = :jenisWarkat AND STATUS_DATA IN ('PA', 'FA')) c,\n" +
                "     (select COUNT(*) as totalWarkatFA from ACC_WARKAT WHERE JENIS_WARKAT = :jenisWarkat AND STATUS_DATA = 'FA') d";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("jenisWarkat", jenisWarkat);
        return namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<TutupTahunanDTO>() {
            @Override
            public TutupTahunanDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                TutupTahunanDTO tutupTahunanDTO = new TutupTahunanDTO();
                tutupTahunanDTO.setTotalWarkat(resultSet.getInt("TOTALWARKAT"));
                tutupTahunanDTO.setTotalWarkatValid(resultSet.getInt("TOTALWARKATVALID"));
                tutupTahunanDTO.setTotalWarkatPA(resultSet.getInt("TOTALWARKATPA"));
                tutupTahunanDTO.setTotalWarkatFA(resultSet.getInt("TOTALWARKATFA"));
                return tutupTahunanDTO;
            }
        });
    }

    public Integer checkTransaksiSaldo(String kodeDRI, Timestamp tglTransaksi) {
        String query = "SELECT \n" +
                "   saldo.totalTransaksiSaldo\n" +
                "FROM \n" +
                "(\n" +
                "   SELECT\n" +
                "       count(*)    AS totalTransaksiSaldo\n" +
                "   FROM ACC_SALDO saldo2\n" +
                "   WHERE TO_CHAR(saldo2.TGL_SALDO, 'yyyy-MM-dd') = TO_CHAR(:tglTransaksi, 'yyyy-MM-dd') \n" +
                "   AND saldo2.KODE_DRI = :kodeDRI\n" +
                ") saldo\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeDRI", kodeDRI);
        map.addValue("tglTransaksi", tglTransaksi);

        return this.namedParameterJdbcTemplate.queryForObject(query, map, Integer.class);
    }

    public void deleteSaldoByTglTransaksi(String tglTransaksi, String kodeDRI) {
        String query = "DELETE FROM ACC_SALDO WHERE TO_CHAR(TGL_SALDO, 'yyyy-MM-dd') = :tglTransaksi AND KODE_DRI = :kodeDRI\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("tglTransaksi", tglTransaksi);
        map.addValue("kodeDRI", kodeDRI);
        this.namedParameterJdbcTemplate.update(query, map);
    }

    public void copySaldoFromSaldoFA(String kodeTahunBuku, String kodePeriode, String createdBy, String kodeDRI) {
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
                "       :kodeTahunBuku AS KODE_THNBUKU,\n" +
                "       :kodePeriode AS KODE_PERIODE,\n" +
                "       (SELECT TGL_TRANSAKSI FROM ACC_PARAMETER WHERE STATUS_AKTIF = '1') AS TGL_SALDO,\n" +
                "       SALDO_AWAL,\n" +
                "       SALDO_DEBET,\n" +
                "       SALDO_KREDIT,\n" +
                "       SALDO_AKHIR,\n" +
                "       NILAI_ANGGARAN,\n" +
                "       SERAP_TAMBAH,\n" +
                "       SERAP_KURANG,\n" +
                "       SALDO_ANGGARAN,\n" +
                "       :kodeDRI AS KODE_DRI,\n" +
                "       :createdBy AS CREATED_BY,\n" +
                "       CURRENT_DATE AS CREATED_DATE\n" +
                "FROM ACC_SALDO_FA\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("createdBy", createdBy);
        map.addValue("kodeDRI", kodeDRI);

        this.namedParameterJdbcTemplate.update(query, map);
    }
}

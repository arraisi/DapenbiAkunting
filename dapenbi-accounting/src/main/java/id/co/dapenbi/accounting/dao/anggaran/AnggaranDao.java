package id.co.dapenbi.accounting.dao.anggaran;

import id.co.dapenbi.accounting.dto.transaksi.CheckAndBalanceDTO;
import id.co.dapenbi.accounting.entity.anggaran.Anggaran;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class AnggaranDao {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public BigDecimal getDataRelasi(Long mataAnggaran) {
        //language=Oracle
        String query = "SELECT COALESCE(SUM(SALDO_DEBET), 0) AS DATA_RELASI \n" +
                "FROM ACC_SALDO_CURRENT asc2\n" +
                "WHERE ID_REKENING = :mataAnggaran";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mataAnggaran", mataAnggaran);

        return namedJdbcTemplate.queryForObject(query, params, new RowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getBigDecimal("DATA_RELASI");
            }
        });
    }

    public BigDecimal getDataAT(Long mataAnggaran, String kodeTahunBuku) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String query = "SELECT COALESCE(TOTAL_ANGGARAN, 0) AS DATA_AT \n" +
                "FROM ACC_ANGGARAN \n" +
                "WHERE ID_REKENING = :mataAnggaran\n" +
                "AND KODE_THNBUKU = :kodeTahunBuku\n" +
                "AND STATUS_AKTIF = '1'\n";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mataAnggaran", mataAnggaran);
        params.addValue("kodeTahunBuku", kodeTahunBuku);
        return namedJdbcTemplate.queryForObject(query, params, new RowMapper<BigDecimal>() {
            @Override
            public BigDecimal mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getBigDecimal("DATA_AT");
            }
        });
    }

    public String getThnBukuPeriode() {
        //language=Oracle
        String query = "SELECT CONCAT(CONCAT(KODE_THNBUKU,' / '), KODE_PERIODE) AS THNBUKU_PERIODE\n" +
                "FROM ACC_PARAMETER\n" +
                "WHERE STATUS_AKTIF = 'Y'";

        return namedJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("THNBUKU_PERIODE");
            }
        });
    }

    public List<String> getSatuanKerja(String userLogin) {
        String query = "SELECT NAMA_ORG \n" +
                "FROM SASIAGA.REF_ORGANISASI\n" +
                "WHERE ID_ORG = (\n" +
                "\tSELECT ID_ORG\n" +
                "\tFROM SASIAGA.MST_PEGAWAI\n" +
                "\tWHERE nip = (\n" +
                "\t\tSELECT nip \n" +
                "\t\tFROM SASIAGA.RBAC_USER \n" +
                "\t\tWHERE USER_LOGIN = :userLogin))";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLogin", userLogin);

        return namedJdbcTemplate.query(query, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("NAMA_ORG");
            }
        });
    }

    public static class RowMapperCheckAndBalance implements RowMapper<CheckAndBalanceDTO.Rekening> {
        @Override
        public CheckAndBalanceDTO.Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
            return new CheckAndBalanceDTO.Rekening(
                    resultSet.getLong("ID_REKENING"),
                    resultSet.getString("NAMA_REKENING"),
                    resultSet.getBigDecimal("SALDO_WARKAT"),
                    resultSet.getBigDecimal("SALDO_PREAPPROVAL"),
                    resultSet.getBigDecimal("SALDO_FINALAPPROVAL"),
                    resultSet.getString("STATUS")
            );
        }
    }


    public Optional<Anggaran> findByIdRekening(Integer idRekening) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String query = "SELECT NO_ANGGARAN,\n" +
                "       ID_REKENING,\n" +
                "       (SELECT NAMA_REKENING FROM ACC_REKENING WHERE ID_REKENING = ACC_ANGGARAN.ID_REKENING) AS NAMA_REKENING,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       VERSI,\n" +
                "       KETERANGAN,\n" +
                "       ANGGARAN_LALU,\n" +
                "       REALISASI,\n" +
                "       PERKIRAAN,\n" +
                "       TOTAL_ANGGARAN,\n" +
                "       TERBILANG,\n" +
                "       PROYEKSI1,\n" +
                "       PROYEKSI2,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       FILE_LAMPIRAN,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       STATUS_AKTIF,\n" +
                "       REALISASI_BERJALAN\n" +
                "FROM ACC_ANGGARAN\n" +
                "WHERE CREATED_DATE = (SELECT MAX(CREATED_DATE) FROM ACC_ANGGARAN WHERE ID_REKENING = :ID_REKENING)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_REKENING", idRekening);

        return namedJdbcTemplate.queryForObject(query, params, (resultSet, i) -> {
            Anggaran anggaran = new Anggaran();
            Rekening rekening = new Rekening();
            rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
            rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));

            anggaran.setNoAnggaran(resultSet.getString("NO_ANGGARAN"));
            anggaran.setIdRekening(rekening);
//            anggaran.setTahunBuku(resultSet.getString(""));
//            anggaran.setKodePeriode(resultSet.getString(""));
            anggaran.setVersi(resultSet.getString("VERSI"));
            anggaran.setKeterangan(resultSet.getString("KETERANGAN"));
            anggaran.setAnggaranLalu(resultSet.getBigDecimal("ANGGARAN_LALU"));
            anggaran.setRealisasi(resultSet.getBigDecimal("REALISASI"));
            anggaran.setPerkiraan(resultSet.getBigDecimal("PERKIRAAN"));
            anggaran.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
            anggaran.setTerbilang(resultSet.getString("TERBILANG"));
            anggaran.setProyeksi1(resultSet.getBigDecimal("PROYEKSI1"));
            anggaran.setProyeksi2(resultSet.getBigDecimal("PROYEKSI2"));
            anggaran.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
            anggaran.setUserValidasi(resultSet.getString("USER_VALIDASI"));
            anggaran.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
            anggaran.setFileLampiran(resultSet.getString("FILE_LAMPIRAN"));
            anggaran.setStatusData(resultSet.getString("STATUS_DATA"));
            anggaran.setCreatedBy(resultSet.getString("CREATED_BY"));
            anggaran.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            anggaran.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            anggaran.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            anggaran.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
            anggaran.setRealisasiBerjalan(resultSet.getBigDecimal("REALISASI_BERJALAN"));
            return Optional.of(anggaran);
        });
    }

    public Optional<Anggaran> findByIdRekeningAndIsSatker(Integer idRekening, String idSatker) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String query = "SELECT NO_ANGGARAN,\n" +
                "       ID_REKENING,\n" +
                "       (SELECT NAMA_REKENING FROM ACC_REKENING WHERE ID_REKENING = ACC_ANGGARAN.ID_REKENING) AS NAMA_REKENING,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       VERSI,\n" +
                "       KETERANGAN,\n" +
                "       ANGGARAN_LALU,\n" +
                "       REALISASI,\n" +
                "       PERKIRAAN,\n" +
                "       TOTAL_ANGGARAN,\n" +
                "       TERBILANG,\n" +
                "       PROYEKSI1,\n" +
                "       PROYEKSI2,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       FILE_LAMPIRAN,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       STATUS_AKTIF,\n" +
                "       REALISASI_BERJALAN\n" +
                "FROM ACC_ANGGARAN\n" +
                "WHERE ID_REKENING = :ID_REKENING\n" +
                "   AND ID_SATKER = :ID_SATKER";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_REKENING", idRekening);
        params.addValue("ID_SATKER", idSatker);

        return namedJdbcTemplate.queryForObject(query, params, (resultSet, i) -> {
            Anggaran anggaran = new Anggaran();
            Rekening rekening = new Rekening();
            rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
            rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));

            anggaran.setNoAnggaran(resultSet.getString("NO_ANGGARAN"));
            anggaran.setIdRekening(rekening);
//            anggaran.setTahunBuku(resultSet.getString(""));
//            anggaran.setKodePeriode(resultSet.getString(""));
            anggaran.setVersi(resultSet.getString("VERSI"));
            anggaran.setKeterangan(resultSet.getString("KETERANGAN"));
            anggaran.setAnggaranLalu(resultSet.getBigDecimal("ANGGARAN_LALU"));
            anggaran.setRealisasi(resultSet.getBigDecimal("REALISASI"));
            anggaran.setPerkiraan(resultSet.getBigDecimal("PERKIRAAN"));
            anggaran.setTotalAnggaran(resultSet.getBigDecimal("TOTAL_ANGGARAN"));
            anggaran.setTerbilang(resultSet.getString("TERBILANG"));
            anggaran.setProyeksi1(resultSet.getBigDecimal("PROYEKSI1"));
            anggaran.setProyeksi2(resultSet.getBigDecimal("PROYEKSI2"));
            anggaran.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
            anggaran.setUserValidasi(resultSet.getString("USER_VALIDASI"));
            anggaran.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
            anggaran.setFileLampiran(resultSet.getString("FILE_LAMPIRAN"));
            anggaran.setStatusData(resultSet.getString("STATUS_DATA"));
            anggaran.setCreatedBy(resultSet.getString("CREATED_BY"));
            anggaran.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            anggaran.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            anggaran.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            anggaran.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
            anggaran.setRealisasiBerjalan(resultSet.getBigDecimal("REALISASI_BERJALAN"));
            return Optional.of(anggaran);
        });
    }
}

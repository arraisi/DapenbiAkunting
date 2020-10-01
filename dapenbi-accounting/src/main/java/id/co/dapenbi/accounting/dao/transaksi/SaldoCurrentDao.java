package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SaldoCurrentDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SaldoCurrent updateSaldoCurrentSerap(SaldoCurrent saldoCurrent) {
        String query = "UPDATE \n" +
                "   ACC_SALDO_CURRENT \n" +
                "SET\n" +
                "   SERAP_TAMBAH = :serapTambah,\n" +
                "   SERAP_KURANG = :serapKurang,\n" +
                "   SALDO_ANGGARAN = :saldoAnggaran\n" +
                "WHERE ID_REKENING = :idRekening\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("serapTambah", saldoCurrent.getSerapTambah());
        map.addValue("serapKurang", saldoCurrent.getSerapKurang());
        map.addValue("saldoAnggaran", saldoCurrent.getSaldoAnggaran());
        map.addValue("idRekening", saldoCurrent.getIdRekening());

        this.namedParameterJdbcTemplate.update(query, map);
        return saldoCurrent;
    }

    public List<SaldoCurrent> findAllSaldoCurrentUpdatedToday() {
        try {
            String query = "SELECT " +
                    "       ID_REKENING,\n" +
                    "       KODE_REKENING,\n" +
                    "       NAMA_REKENING,\n" +
                    "       SALDO_AWAL,\n" +
                    "       SALDO_DEBET,\n" +
                    "       SALDO_KREDIT,\n" +
                    "       SALDO_AKHIR,\n" +
                    "       NILAI_ANGGARAN,\n" +
                    "       SERAP_TAMBAH,\n" +
                    "       SERAP_KURANG,\n" +
                    "       SALDO_ANGGARAN,\n" +
                    "       STATUS_DATA,\n" +
                    "       CREATED_BY,\n" +
                    "       CREATED_DATE,\n" +
                    "       UPDATED_BY,\n" +
                    "       UPDATED_DATE\n" +
                    "FROM ACC_SALDO_CURRENT\n" +
                    "WHERE TO_CHAR(UPDATED_DATE, 'YYYY-MM-DD') = TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')";

            MapSqlParameterSource map = new MapSqlParameterSource();

            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<SaldoCurrent>() {
                @Override
                public SaldoCurrent mapRow(ResultSet resultSet, int i) throws SQLException {
                    SaldoCurrent saldoCurrent = new SaldoCurrent();
                    saldoCurrent.setIdRekening(resultSet.getInt("ID_REKENING"));
                    saldoCurrent.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    saldoCurrent.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    saldoCurrent.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                    saldoCurrent.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                    saldoCurrent.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                    saldoCurrent.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                    saldoCurrent.setNilaiAnggaran(resultSet.getBigDecimal("NILAI_ANGGARAN"));
                    saldoCurrent.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                    saldoCurrent.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                    saldoCurrent.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                    saldoCurrent.setStatusData(resultSet.getString("STATUS_DATA"));
                    saldoCurrent.setCreatedBy(resultSet.getString("CREATED_BY"));
                    saldoCurrent.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    saldoCurrent.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    saldoCurrent.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                    return saldoCurrent;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<SaldoDTO> findAllSaldoFAUpdatedToday() {
        try {
            String query = "SELECT " +
                    "       ID_REKENING,\n" +
                    "       KODE_REKENING,\n" +
                    "       NAMA_REKENING,\n" +
                    "       SALDO_AWAL,\n" +
                    "       SALDO_DEBET,\n" +
                    "       SALDO_KREDIT,\n" +
                    "       SALDO_AKHIR,\n" +
                    "       NILAI_ANGGARAN,\n" +
                    "       SERAP_TAMBAH,\n" +
                    "       SERAP_KURANG,\n" +
                    "       SALDO_ANGGARAN,\n" +
                    "       STATUS_DATA,\n" +
                    "       KODE_DRI,\n" +
                    "       CREATED_BY,\n" +
                    "       CREATED_DATE,\n" +
                    "       UPDATED_BY,\n" +
                    "       UPDATED_DATE\n" +
                    "FROM ACC_SALDO_FA ";

            MapSqlParameterSource map = new MapSqlParameterSource();

            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<SaldoDTO>() {
                @Override
                public SaldoDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                    SaldoDTO saldo = new SaldoDTO();
                    saldo.setIdRekening(resultSet.getInt("ID_REKENING"));
                    saldo.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    saldo.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    saldo.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                    saldo.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                    saldo.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                    saldo.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                    saldo.setNilaiAnggaran(resultSet.getBigDecimal("NILAI_ANGGARAN"));
                    saldo.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                    saldo.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                    saldo.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                    saldo.setStatusData(resultSet.getString("STATUS_DATA"));
                    saldo.setKodeDri(resultSet.getString("KODE_DRI"));
                    saldo.setCreatedBy(resultSet.getString("CREATED_BY"));
                    saldo.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    saldo.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    saldo.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                    return saldo;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<SaldoCurrent> findAllUpdatedThisYear() {
        try {
            String query = "SELECT " +
                    "       ID_REKENING,\n" +
                    "       KODE_REKENING,\n" +
                    "       NAMA_REKENING,\n" +
                    "       SALDO_AWAL,\n" +
                    "       SALDO_DEBET,\n" +
                    "       SALDO_KREDIT,\n" +
                    "       SALDO_AKHIR,\n" +
                    "       NILAI_ANGGARAN,\n" +
                    "       SERAP_TAMBAH,\n" +
                    "       SERAP_KURANG,\n" +
                    "       SALDO_ANGGARAN,\n" +
                    "       STATUS_DATA,\n" +
                    "       CREATED_BY,\n" +
                    "       CREATED_DATE,\n" +
                    "       UPDATED_BY,\n" +
                    "       UPDATED_DATE\n" +
                    "FROM ACC_SALDO_CURRENT\n" +
                    "WHERE TO_CHAR(UPDATED_DATE, 'YYYY') = TO_CHAR(CURRENT_DATE, 'YYYY')";

            MapSqlParameterSource map = new MapSqlParameterSource();

            return this.namedParameterJdbcTemplate.query(query, map, new RowMapper<SaldoCurrent>() {
                @Override
                public SaldoCurrent mapRow(ResultSet resultSet, int i) throws SQLException {
                    SaldoCurrent saldoCurrent = new SaldoCurrent();
                    saldoCurrent.setIdRekening(resultSet.getInt("ID_REKENING"));
                    saldoCurrent.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    saldoCurrent.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    saldoCurrent.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                    saldoCurrent.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                    saldoCurrent.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                    saldoCurrent.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                    saldoCurrent.setNilaiAnggaran(resultSet.getBigDecimal("NILAI_ANGGARAN"));
                    saldoCurrent.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                    saldoCurrent.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                    saldoCurrent.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                    saldoCurrent.setStatusData(resultSet.getString("STATUS_DATA"));
                    saldoCurrent.setCreatedBy(resultSet.getString("CREATED_BY"));
                    saldoCurrent.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    saldoCurrent.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    saldoCurrent.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                    return saldoCurrent;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

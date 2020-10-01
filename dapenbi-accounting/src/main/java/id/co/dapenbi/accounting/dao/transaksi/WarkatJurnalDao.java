package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WarkatJurnalDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<WarkatJurnal> findByNoWarkat(String noWarkat) {
        String query = "SELECT WJ.ID_WARKAT_JURNAL,\n" +
                "       WJ.NO_WARKAT,\n" +
                "       WJ.ID_REKENING,\n" +
                "       COALESCE(WJ.JUMLAH_DEBIT, 0) AS JUMLAH_DEBIT,\n" +
                "       COALESCE(WJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       WJ.NO_URUT,\n" +
                "       WJ.SALDO_NORMAL,\n" +
                "       WJ.CREATED_BY,\n" +
                "       WJ.CREATED_DATE,\n" +
                "       WJ.UPDATED_BY,\n" +
                "       WJ.UPDATED_DATE,\n" +
                "       AR.ID_REKENING,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.SALDO_NORMAL AS SALDO_NORMAL_R\n" +
                "FROM ACC_WARKAT_JURNAL WJ LEFT JOIN ACC_REKENING AR on WJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE WJ.NO_WARKAT = :NO_WARKAT\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<WarkatJurnal>() {
            @Override
            public WarkatJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatJurnal value = new WarkatJurnal();
                Warkat warkat = new Warkat();
                warkat.setNoWarkat(resultSet.getString("NO_WARKAT"));
                Rekening rekening = new Rekening(
                        resultSet.getInt("ID_REKENING"),
                        resultSet.getString("KODE_REKENING"),
                        resultSet.getString("NAMA_REKENING"),
                        resultSet.getString("SALDO_NORMAL_R"));

                value.setIdWarkatJurnal(resultSet.getInt("ID_WARKAT_JURNAL"));
                value.setNoWarkat(warkat);
                value.setIdRekening(rekening);
                value.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
                value.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
                value.setNoUrut(resultSet.getInt("NO_URUT"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });

    }

    public List<WarkatJurnal> findByNoWarkatDRI2(String noWarkat) {
        String query = "SELECT WJ.ID_WARKAT_JURNAL,\n" +
                "       WJ.NO_WARKAT,\n" +
                "       WJ.ID_REKENING,\n" +
                "       COALESCE(WJ.JUMLAH_DEBIT, 0) AS JUMLAH_DEBIT,\n" +
                "       COALESCE(WJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       WJ.NO_URUT,\n" +
                "       WJ.SALDO_NORMAL,\n" +
                "       WJ.CREATED_BY,\n" +
                "       WJ.CREATED_DATE,\n" +
                "       WJ.UPDATED_BY,\n" +
                "       WJ.UPDATED_DATE,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.SALDO_NORMAL AS SALDO_NORMAL_R\n" +
                "FROM ACC_WARKAT_JURNAL WJ\n" +
                "         LEFT JOIN ACC_REKENING AR on WJ.ID_REKENING = AR.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO S on WJ.ID_REKENING = S.ID_REKENING\n" +
                "WHERE WJ.NO_WARKAT = :NO_WARKAT AND S.KODE_DRI = 2";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<WarkatJurnal>() {
            @Override
            public WarkatJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatJurnal value = new WarkatJurnal();
                Warkat warkat = new Warkat();
                warkat.setNoWarkat(resultSet.getString("NO_WARKAT"));
                Rekening rekening = new Rekening(
                        resultSet.getInt("KODE_REKENING"),
                        resultSet.getString("KODE_REKENING"),
                        resultSet.getString("NAMA_REKENING"),
                        resultSet.getString("SALDO_NORMAL_R"));

                value.setIdWarkatJurnal(resultSet.getInt("ID_WARKAT_JURNAL"));
                value.setNoWarkat(warkat);
                value.setIdRekening(rekening);
                value.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
                value.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
                value.setNoUrut(resultSet.getInt("NO_URUT"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                return value;
            }
        });

    }

    public WarkatJurnal querySave(WarkatJurnal warkatJurnal) {
        String query = "insert\n" +
                "into ACC_WARKAT_JURNAL\n" +
                "(ID_WARKAT_JURNAL,NO_WARKAT,ID_REKENING,JUMLAH_DEBIT,JUMLAH_KREDIT,NO_URUT,SALDO_NORMAL,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)\n" +
                "values(:idWarkatJurnal, :noWarkat, :idRekening, :jmlDebit, :jmlKredit, :noUrut,:saldoNormal, :createdBy, :createdDate, :updatedBy, :updatedDate)\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idWarkatJurnal", warkatJurnal.getIdWarkatJurnal());
        map.addValue("noWarkat", warkatJurnal.getNoWarkat().getNoWarkat());
        map.addValue("idRekening", warkatJurnal.getIdRekening().getIdRekening());
        map.addValue("jmlDebit", warkatJurnal.getJumlahDebit());
        map.addValue("jmlKredit", warkatJurnal.getJumlahKredit());
        map.addValue("noUrut", warkatJurnal.getNoUrut());
        map.addValue("saldoNormal", warkatJurnal.getSaldoNormal());
        map.addValue("createdBy", warkatJurnal.getCreatedBy());
        map.addValue("createdDate", warkatJurnal.getCreatedDate());
        map.addValue("updatedBy", warkatJurnal.getUpdatedBy());
        map.addValue("updatedDate", warkatJurnal.getUpdatedDate());

        this.namedParameterJdbcTemplate.update(query, map);
        return warkatJurnal;
    }

    public int deleteByNoWarkat(String noWarkat) {
        String query = "DELETE FROM ACC_WARKAT_JURNAL WHERE NO_WARKAT = :NO_WARKAT";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("NO_WARKAT", noWarkat);

        return this.namedParameterJdbcTemplate.update(query, map);
    }

    public List<WarkatJurnalDTO> findByNoWarkatAndSaldoNormal(String noWarkat, String saldoNormal) {
        String query = "SELECT WJ.ID_WARKAT_JURNAL,\n" +
                "       WJ.NO_WARKAT,\n" +
                "       WJ.ID_REKENING,\n" +
                "       COALESCE(WJ.JUMLAH_DEBIT, 0) AS JUMLAH_DEBIT,\n" +
                "       COALESCE(WJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       WJ.NO_URUT,\n" +
                "       WJ.CREATED_BY,\n" +
                "       WJ.CREATED_DATE,\n" +
                "       WJ.UPDATED_BY,\n" +
                "       WJ.UPDATED_DATE,\n" +
                "       WJ.SALDO_NORMAL,\n" +
                "       WJ.KODE_REKENING_LAMA,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL WJ\n" +
                "         " +
                "LEFT JOIN ACC_REKENING AR on WJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE WJ.NO_WARKAT = :NO_WARKAT\n" +
                "  AND WJ.SALDO_NORMAL = :SALDO_NORMAL\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        params.addValue("SALDO_NORMAL", saldoNormal);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            WarkatJurnalDTO value = new WarkatJurnalDTO();
            Rekening rekening = new Rekening();
            rekening.setIdRekening(resultSet.getInt("ID_REKENING"));

            value.setIdWarkatJurnal(resultSet.getInt("ID_WARKAT_JURNAL"));
            value.setNoWarkat(resultSet.getString("NO_WARKAT"));
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setIdRekening(rekening);
            value.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
            value.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
            value.setNoUrut(resultSet.getInt("NO_URUT"));
            value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
            value.setCreatedBy(resultSet.getString("CREATED_BY"));
            value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            value.setJumlah(saldoNormal.equals("D") ? resultSet.getBigDecimal("JUMLAH_DEBIT") : resultSet.getBigDecimal("JUMLAH_KREDIT"));
            return value;
        });
    }
}

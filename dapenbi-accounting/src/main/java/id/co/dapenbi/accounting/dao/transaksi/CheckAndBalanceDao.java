package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.CheckAndBalanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
public class CheckAndBalanceDao {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public List<CheckAndBalanceDTO.Rekening> list() {
        //language=Oracle
        String query = "SELECT CB.ID_REKENING,\n" +
                "       CB.KODE_REKENING,\n" +
                "       CB.NAMA_REKENING,\n" +
                "       COALESCE(CB.SALDO_WARKAT, 0)        AS SALDO_WARKAT,\n" +
                "       COALESCE(CB.SALDO_PREAPPROVAL, 0)   AS SALDO_PREAPPROVAL,\n" +
                "       COALESCE(CB.SALDO_FINALAPPROVAL, 0) AS SALDO_FINALAPPROVAL,\n" +
                "       CASE\n" +
                "           WHEN COALESCE(CB.SALDO_WARKAT, 0) = COALESCE(CB.SALDO_PREAPPROVAL, 0)\n" +
                "               AND COALESCE(CB.SALDO_PREAPPROVAL, 0) = COALESCE(CB.SALDO_FINALAPPROVAL, 0)\n" +
                "               THEN 'Balance'\n" +
                "           ELSE 'Tidak Balance'\n" +
                "           END                             AS STATUS\n" +
                "FROM (\n" +
                "         SELECT AR.ID_REKENING,\n" +
                "                AR.KODE_REKENING,\n" +
                "                AR.NAMA_REKENING,\n" +
                "                SC.SALDO_AKHIR AS SALDO_WARKAT,\n" +
                "                (\n" +
                "                    SELECT SCPRE.SALDO_AKHIR\n" +
                "                    FROM ACC_SALDO_PA SCPRE\n" +
                "                    WHERE SCPRE.ID_REKENING = SC.ID_REKENING\n" +
                "                )              AS SALDO_PREAPPROVAL,\n" +
                "                (\n" +
                "                    SELECT SCFINAL.SALDO_AKHIR\n" +
                "                    FROM ACC_SALDO_FA SCFINAL\n" +
                "                    WHERE SCFINAL.ID_REKENING = SC.ID_REKENING\n" +
                "                )              AS SALDO_FINALAPPROVAL\n" +
                "         FROM ACC_SALDO_CURRENT SC,\n" +
                "              ACC_REKENING AR\n" +
                "         WHERE SC.ID_REKENING = AR.ID_REKENING\n" +
                "     ) CB";

        return namedJdbcTemplate.query(query, new HashMap<>(), new RowMapperCheckAndBalance());
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
                    resultSet.getLong("KODE_REKENING"),
                    resultSet.getString("NAMA_REKENING"),
                    resultSet.getBigDecimal("SALDO_WARKAT"),
                    resultSet.getBigDecimal("SALDO_PREAPPROVAL"),
                    resultSet.getBigDecimal("SALDO_FINALAPPROVAL"),
                    resultSet.getString("STATUS")
            );
        }
    }
}

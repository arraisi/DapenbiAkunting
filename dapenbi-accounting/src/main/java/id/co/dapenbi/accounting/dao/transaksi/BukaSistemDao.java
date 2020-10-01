package id.co.dapenbi.accounting.dao.transaksi;

import id.co.dapenbi.accounting.dto.StatusPemakai;
import id.co.dapenbi.accounting.dto.transaksi.BukaSistemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class BukaSistemDao {

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public List<StatusPemakai> findStatusPemakai() {
        String query = "SELECT ID_ORG, NAMA_ORG FROM SASIAGA.REF_ORGANISASI WHERE FLAG_AKTIF = 'Y'";

        return namedJdbcTemplate.query(query, new HashMap<>(), new RowMapper<StatusPemakai>() {
            @Override
            public StatusPemakai mapRow(ResultSet resultSet, int i) throws SQLException {
                return new StatusPemakai(
                        resultSet.getInt("ID_ORG"),
                        resultSet.getString("NAMA_ORG")
                );
            }
        });
    }

    public List<Date> getHariLibur() {
        String query = "SELECT CAST(TGL_LIBUR AS date) AS TGL_LIBUR\n" +
                "FROM SASIAGA.REF_KALENDER_LIBUR rkl\n" +
                "WHERE to_char(TGL_LIBUR,'dd-mm-yyyy') = TO_CHAR(SYSDATE, 'dd-mm-yyyy')\n" +
                "\tAND FLAG_AKTIF = 'Y'";

        return namedJdbcTemplate.query(query, new HashMap<>(), new RowMapper<Date>() {
            @Override
            public Date mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getDate("TGL_LIBUR");
            }
        });
    }

    public List<Integer> getIdOrg(String userLogin) {
        String query = "SELECT ID_ORG\n" +
                "FROM SASIAGA.MST_PEGAWAI \n" +
                "WHERE nip IN (\n" +
                "\tSELECT nip \n" +
                "\tFROM SASIAGA.RBAC_USER \n" +
                "\tWHERE USER_LOGIN = :userLogin)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userLogin", userLogin);

        return namedJdbcTemplate.query(query, params, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("ID_ORG");
            }
        });
    }

    public Date getMaxTglSaldo() {
        String query = "SELECT MAX(TGL_SALDO) as MAX_TGL FROM ACC_SALDO";

        return namedJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Date>() {
            @Override
            public Date mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getDate("MAX_TGL");
            }
        });
    }

    public Date getTglBuka() {
        String query = "SELECT MAX(TGL_BUKA) AS MAX_TGL FROM ACC_BUKASISTEM";

        return namedJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Date>() {
            @Override
            public Date mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getDate("MAX_TGL");
            }
        });
    }

    public Date getTglBukaMin1() {
        String query = "SELECT B.TGL_BUKA AS TGL_BUKA\n" +
                "FROM (\n" +
                "\tSELECT \n" +
                "\t\tROW_NUMBER() OVER (ORDER BY TGL_BUKA DESC) AS ROW_NUM, \n" +
                "\t\tTGL_BUKA \n" +
                "\tFROM ACC_BUKASISTEM AB\n" +
                "\tORDER BY TGL_BUKA DESC\n" +
                ") B\n" +
                "WHERE B.ROW_NUM = 2";

        return namedJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Date>() {
            @Override
            public Date mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getDate("TGL_BUKA");
            }
        });
    }

    public BukaSistemDto aktivaPasiva(String tglSaldo) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("tglSaldo", tglSaldo);
        String queryAktiva = "SELECT\n" +
                "   SUM(COALESCE(asc2.SALDO_AKHIR, 0))\n" +
                "   FROM ACC_SALDO asc2\n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = asc2.ID_REKENING \n" +
                "WHERE ar.SALDO_NORMAL = 'D'\n" +
                "AND TO_CHAR(asc2.TGL_SALDO, 'yyyy-MM-dd') = :tglSaldo\n";

        BigDecimal aktiva = this.namedJdbcTemplate.queryForObject(queryAktiva, map, BigDecimal.class);

        String queryPasiva = "SELECT\n" +
                "   SUM(COALESCE(asc2.SALDO_AKHIR, 0))\n" +
                "   FROM ACC_SALDO asc2\n" +
                "JOIN ACC_REKENING ar ON ar.ID_REKENING = asc2.ID_REKENING \n" +
                "WHERE ar.SALDO_NORMAL = 'K'\n" +
                "AND TO_CHAR(asc2.TGL_SALDO, 'yyyy-MM-dd') = :tglSaldo\n";

        BigDecimal pasiva = this.namedJdbcTemplate.queryForObject(queryPasiva, map, BigDecimal.class);
        BukaSistemDto aktivaPasiva = new BukaSistemDto();
        aktivaPasiva.setNilaiAktiva(aktiva);
        aktivaPasiva.setNilaiPasiva(pasiva);
        return aktivaPasiva;
    }

    public BukaSistemDto getAktivaPasivaSaldo(String tglSaldo) {
        String query = "SELECT SUM(SALDO_DEBET) AS activa,\n" +
                "\tSUM(SALDO_KREDIT) AS pasiva \n" +
                "FROM ACC_SALDO \n" +
                "WHERE TO_DATE(TGL_SALDO) = TO_DATE(:tglSaldo, 'YYYY-MM-DD')";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tglSaldo", tglSaldo);

        return namedJdbcTemplate.queryForObject(query, params, new RowMapper<BukaSistemDto>() {
            @Override
            public BukaSistemDto mapRow(ResultSet rs, int i) throws SQLException {
                BukaSistemDto bukaSistemDto = new BukaSistemDto();
                bukaSistemDto.setNilaiAktiva(rs.getBigDecimal("activa"));
                bukaSistemDto.setNilaiPasiva(rs.getBigDecimal("pasiva"));
                return bukaSistemDto;
            }
        });
    }

    public List<Integer> findIdParamCurrentDate() {
        String query = "SELECT AP.ID_PARAMETER\n" +
                "FROM ACC_PARAMETER AP\n" +
                "WHERE AP.KODE_THNBUKU = EXTRACT(year FROM sysdate)\n" +
                "\tAND AP.KODE_PERIODE = EXTRACT(month FROM sysdate)";

        return namedJdbcTemplate.query(query, new HashMap<>(), new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int i) throws SQLException {
                return rs.getInt("ID_PARAMETER");
            }
        });
    }

    public void updateParam(Integer idParam) {
        String query = "UPDATE ACC_PARAMETER \n" +
                "SET STATUS_AKTIF = 'Y',\n" +
                "\tTGL_TRANSAKSI = SYSDATE\n" +
                "WHERE ID_PARAMETER = :idParam";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idParam", idParam);

        namedJdbcTemplate.update(query, params);
    }

    public void saveParam(Long idParameter, String username) {
        String query = "INSERT INTO \n" +
                "ACC_PARAMETER(ID_PARAMETER, KODE_THNBUKU, KODE_PERIODE, STATUS_AKTIF, " +
                "TGL_TRANSAKSI, CREATED_BY, CREATED_DATE)\n" +
                "values(:idParameter, EXTRACT(year FROM sysdate), EXTRACT(month FROM sysdate), 'Y', " +
                "SYSDATE, :createdBy, SYSDATE)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idParameter", idParameter);
        params.addValue("createdBy", username);

        namedJdbcTemplate.update(query, params);
    }

    public Optional<Integer> getIdOrgByNip(String nip) {
        String query = "SELECT \n" +
                "   ID_ORG      AS idOrg\n" +
                "FROM SASIAGA.MST_PEGAWAI\n" +
                "WHERE NIP = :nip\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("nip", nip);

        try {
            return namedJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<Integer>>() {
                @Override
                public Optional<Integer> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Integer idOrg = resultSet.getInt("idOrg");
                    return Optional.of(idOrg);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.of(0);
        }
    }

    public Timestamp findTglTransaksiTerakhir() {
        String baseQuery = "select distinct max(TGL_SALDO) as TGL_SALDO from ACC_SALDO \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        return this.namedJdbcTemplate.queryForObject(baseQuery, map, (resultSet, i) -> resultSet.getTimestamp("TGL_SALDO"));
    }

    public String findLastDRI(String tglTransaksi) {
        String baseQuery = "select distinct max(KODE_DRI) as KODE_DRI from ACC_SALDO where TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') = :TGL_TRANSAKSI \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("TGL_TRANSAKSI", tglTransaksi);
        return this.namedJdbcTemplate.queryForObject(baseQuery, map, (resultSet, i) -> resultSet.getString("KODE_DRI"));
    }

    public int resetSaldoCurrent() {
        String query = "UPDATE ACC_SALDO_CURRENT SC\n" +
                "SET SALDO_AWAL     = SALDO_AKHIR,\n" +
                "    SALDO_DEBET    = 0,\n" +
                "    SALDO_KREDIT   = 0,\n" +
                "    NILAI_ANGGARAN = 0,\n" +
                "    SERAP_TAMBAH   = 0,\n" +
                "    SERAP_KURANG   = 0,\n" +
                "    SALDO_ANGGARAN = 0,\n" +
                "    SALDO_JUAL     = 0 \n";
        return namedJdbcTemplate.update(query, new HashMap<>());
    }

    public int resetSaldoPA() {
        String query = "UPDATE ACC_SALDO_PA SC\n" +
                "SET SALDO_AWAL     = SALDO_AKHIR,\n" +
                "    SALDO_DEBET    = 0,\n" +
                "    SALDO_KREDIT   = 0,\n" +
                "    NILAI_ANGGARAN = 0,\n" +
                "    SERAP_TAMBAH   = 0,\n" +
                "    SERAP_KURANG   = 0,\n" +
                "    SALDO_ANGGARAN = 0,\n" +
                "    SALDO_JUAL     = 0 \n";
        return namedJdbcTemplate.update(query, new HashMap<>());
    }

    public int resetSaldoFA() {
        String query = "UPDATE ACC_SALDO_FA SC\n" +
                "SET SALDO_AWAL     = SALDO_AKHIR,\n" +
                "    SALDO_DEBET    = 0,\n" +
                "    SALDO_KREDIT   = 0,\n" +
                "    NILAI_ANGGARAN = 0,\n" +
                "    SERAP_TAMBAH   = 0,\n" +
                "    SERAP_KURANG   = 0,\n" +
                "    SALDO_ANGGARAN = 0,\n" +
                "    SALDO_JUAL     = 0 \n";
        return namedJdbcTemplate.update(query, new HashMap<>());
    }
}

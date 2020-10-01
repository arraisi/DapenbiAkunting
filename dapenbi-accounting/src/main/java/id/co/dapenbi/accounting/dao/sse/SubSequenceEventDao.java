package id.co.dapenbi.accounting.dao.sse;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dao.transaksi.BudgetReviewDetailDao;
import id.co.dapenbi.accounting.dao.transaksi.WarkatDao;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO.DaftarRekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.dto.transaksi.ValidasiWarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class SubSequenceEventDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private WarkatDao warkatDao;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public List<Integer> findListIdRekeningByNoWarkat(String noWarkat) {
        String finalQuery = "SELECT ID_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL\n" +
                "WHERE NO_WARKAT = :NO_WARKAT\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.query(finalQuery, map, (resultSet, i) -> resultSet.getInt("ID_REKENING"));
    }

    public List<SaldoDTO> findSaldoByNoWarkat(List<Integer> idRekenings, Timestamp tglTransaksi) {
        //language=Oracle
        String finalQuery =  "SELECT ID_SALDO,\n" +
                "       ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       KODE_DRI,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_SALDO,\n" +
                "       SALDO_AWAL,\n" +
                "       SALDO_DEBET,\n" +
                "       SALDO_KREDIT,\n" +
                "       SALDO_AKHIR,\n" +
                "       NILAI_ANGGARAN,\n" +
                "       SERAP_TAMBAH,\n" +
                "       SERAP_KURANG,\n" +
                "       SALDO_ANGGARAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       SALDO_JUAL\n" +
                "FROM ACC_SALDO S \n" +
                "WHERE ID_REKENING IN (:ID_REKENING)\n" +
                "  AND TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') = :TGL_SALDO\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("ID_REKENING", idRekenings);

        Date date = new Date(tglTransaksi.getTime());
        map.addValue("TGL_SALDO", formatter.format(date));
        return getListEntitySaldo(finalQuery, map);
    }

    private List<SaldoDTO> getListEntitySaldo(String finalQuery, MapSqlParameterSource map) {
        return namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<SaldoDTO>() {
            @Override
            public SaldoDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                SaldoDTO value = new SaldoDTO();
                value.setIdSaldo(resultSet.getInt("ID_SALDO"));
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setKodeDri(resultSet.getString("KODE_DRI"));
                value.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
                value.setKodePeriode(resultSet.getString("KODE_PERIODE"));
                value.setTglSaldo(resultSet.getString("TGL_SALDO"));
                value.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                value.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                value.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                value.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                value.setNilaiAnggaran(resultSet.getBigDecimal("NILAI_ANGGARAN"));
                value.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                value.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                value.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                value.setSaldoJual(resultSet.getBigDecimal("SALDO_JUAL"));
                return value;
            }
        });
    }

    public SaldoDTO findSaldoSNKDRITiga(Timestamp tglTransaksi) throws IncorrectResultSizeDataAccessException {
        String finalQuery = "SELECT ID_SALDO,\n" +
                "       ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       KODE_DRI,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_SALDO,\n" +
                "       SALDO_AWAL,\n" +
                "       SALDO_DEBET,\n" +
                "       SALDO_KREDIT,\n" +
                "       SALDO_AKHIR,\n" +
                "       NILAI_ANGGARAN,\n" +
                "       SERAP_TAMBAH,\n" +
                "       SERAP_KURANG,\n" +
                "       SALDO_ANGGARAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       SALDO_JUAL\n" +
                "FROM ACC_SALDO\n" +
                "WHERE KODE_REKENING = (SELECT KODE_LOOKUP FROM MST_LOOKUP WHERE JENIS_LOOKUP = 'SNKA')\n" +
                "  AND KODE_DRI = '3'\n" +
                "AND TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') = :TGL_SALDO \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        Date date = new Date(tglTransaksi.getTime());
        map.addValue("TGL_SALDO", formatter.format(date));
        return getEntitySaldo(finalQuery, map);
    }

    public SaldoDTO findSaldoByKodeRekeningAndDRI(String kodeRekening, String kodeDRI, Timestamp tglTransaksi) throws IncorrectResultSizeDataAccessException {
        String finalQuery = "SELECT ID_SALDO,\n" +
                "       ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       KODE_DRI,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_SALDO,\n" +
                "       SALDO_AWAL,\n" +
                "       SALDO_DEBET,\n" +
                "       SALDO_KREDIT,\n" +
                "       SALDO_AKHIR,\n" +
                "       NILAI_ANGGARAN,\n" +
                "       SERAP_TAMBAH,\n" +
                "       SERAP_KURANG,\n" +
                "       SALDO_ANGGARAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       SALDO_JUAL\n" +
                "FROM ACC_SALDO\n" +
                "WHERE KODE_REKENING = :KODE_REKENING\n" +
                "AND KODE_DRI = :KODE_DRI\n" +
                "AND TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') = :TGL_SALDO \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_REKENING", kodeRekening);

        Date date = new Date(tglTransaksi.getTime());
        map.addValue("TGL_SALDO", formatter.format(date));
        map.addValue("KODE_DRI", kodeDRI);
        return getEntitySaldo(finalQuery, map);
    }

    public List<ValidasiWarkatDTO> findSaldoByDRI(List<Integer> listIdRekeningByNoWarkat, String kodeDRI, Timestamp tglTransaksi) throws IncorrectResultSizeDataAccessException {
        String finalQuery = "SELECT S.ID_SALDO,\n" +
                "       AR.SALDO_NORMAL,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       S.KODE_DRI,\n" +
                "       COALESCE(S.SALDO_DEBET, 0)    AS SALDO_DEBET,\n" +
                "       COALESCE(S.SALDO_KREDIT, 0)   AS SALDO_KREDIT,\n" +
                "       COALESCE(S.SALDO_ANGGARAN, 0) AS SALDO_ANGGARAN,\n" +
                "       COALESCE(S.SALDO_AKHIR, 0)    AS SALDO_AKHIR,\n" +
                "       COALESCE(S.SALDO_AWAL, 0)     AS SALDO_AWAL,\n" +
                "       COALESCE(S.SERAP_TAMBAH, 0)   AS SERAP_TAMBAH,\n" +
                "       COALESCE(S.SERAP_KURANG, 0)   AS SERAP_KURANG,\n" +
                "       COALESCE(S.SALDO_JUAL, 0)     AS SALDO_JUAL,\n" +
                "       AR.TIPE_REKENING\n" +
                "FROM ACC_SALDO S\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = S.ID_REKENING\n" +
                "WHERE S.ID_REKENING IN (:ID_REKENING)\n" +
                "AND S.KODE_DRI = :KODE_DRI\n" +
                "AND TO_CHAR(S.TGL_SALDO, 'YYYY-MM-DD') = :TGL_SALDO \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("ID_REKENING", listIdRekeningByNoWarkat);

        Date date = new Date(tglTransaksi.getTime());
        map.addValue("TGL_SALDO",  formatter.format(date));
        map.addValue("KODE_DRI", kodeDRI);
        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<ValidasiWarkatDTO>() {
            @Override
            public ValidasiWarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                ValidasiWarkatDTO value = new ValidasiWarkatDTO();
                value.setKodeDRI(resultSet.getString("KODE_DRI"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setKodeRekekning(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                value.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                value.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                value.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                value.setSaldoJual(resultSet.getBigDecimal("SALDO_JUAL"));
                value.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                value.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                value.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                return value;
            }
        });
    }

    private SaldoDTO getEntitySaldo(String finalQuery, MapSqlParameterSource map) {
        return namedParameterJdbcTemplate.queryForObject(finalQuery, map, new RowMapper<SaldoDTO>() {
            @Override
            public SaldoDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                SaldoDTO value = new SaldoDTO();
                value.setIdSaldo(resultSet.getInt("ID_SALDO"));
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setKodeDri(resultSet.getString("KODE_DRI"));
                value.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
                value.setKodePeriode(resultSet.getString("KODE_PERIODE"));
                value.setTglSaldo(resultSet.getString("TGL_SALDO"));
                value.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                value.setSaldoDebet(resultSet.getBigDecimal("SALDO_DEBET"));
                value.setSaldoKredit(resultSet.getBigDecimal("SALDO_KREDIT"));
                value.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                value.setNilaiAnggaran(resultSet.getBigDecimal("NILAI_ANGGARAN"));
                value.setSerapTambah(resultSet.getBigDecimal("SERAP_TAMBAH"));
                value.setSerapKurang(resultSet.getBigDecimal("SERAP_KURANG"));
                value.setSaldoAnggaran(resultSet.getBigDecimal("SALDO_ANGGARAN"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
                value.setSaldoJual(resultSet.getBigDecimal("SALDO_JUAL"));
                return value;
            }
        });
    }

    public int updateAccSaldo(ValidasiWarkatDTO saldo, String kodeDRI) {
        String finalQuery = "UPDATE ACC_SALDO\n" +
                "SET SALDO_AWAL     = :saldoAwal,\n" +
                "    SALDO_DEBET    = :saldoDebet,\n" +
                "    SALDO_KREDIT   = :saldoKredit,\n" +
                "    SALDO_AKHIR    = :saldoAkhir,\n" +
                "    NILAI_ANGGARAN = :nilaiAnggaran,\n" +
                "    SALDO_ANGGARAN = :saldoAnggaran\n" +
                "WHERE KODE_REKENING = :kodeRekening\n" +
                "  AND KODE_DRI = :KODE_DRI \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeRekening", saldo.getKodeRekekning());
        map.addValue("saldoAwal", saldo.getSaldoAwal());
        map.addValue("saldoDebet", saldo.getSaldoDebet());
        map.addValue("saldoKredit", saldo.getSaldoKredit());
        map.addValue("saldoAkhir", saldo.getSaldoAkhir());
        map.addValue("nilaiAnggaran", saldo.getNilaiAnggaran());
        map.addValue("saldoAnggaran", saldo.getSaldoAnggaran());
        map.addValue("KODE_DRI", kodeDRI);
        return this.namedParameterJdbcTemplate.update(finalQuery, map);
    }

    public int updateAccSaldoSNKA(SaldoDTO saldo) {
        String finalQuery = "UPDATE ACC_SALDO\n" +
                "SET SALDO_AWAL     = :saldoAwal,\n" +
                "    SALDO_ANGGARAN = :saldoAnggaran\n" +
                "WHERE ID_SALDO = :idSaldo\n" +
                "  AND TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') BETWEEN CONCAT(TO_CHAR(CURRENT_DATE, 'YYYY'), '-01-01')\n" +
                "    AND (SELECT TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI FROM ACC_PARAMETER) \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idSaldo", saldo.getIdSaldo());
        map.addValue("saldoAwal", saldo.getSaldoAkhir());
        map.addValue("saldoAnggaran", saldo.getSaldoAnggaran());
        return this.namedParameterJdbcTemplate.update(finalQuery, map);
    }

    public int updateAccSaldoKas(ValidasiWarkatDTO saldo) {
        String finalQuery = "UPDATE ACC_SALDO\n" +
                "SET SALDO_AWAL     = :saldoAwal,\n" +
                "    SALDO_ANGGARAN = :saldoAnggaran\n" +
                "WHERE KODE_REKENING = :KODE_REKENING\n" +
                "  AND TO_CHAR(TGL_SALDO, 'YYYY-MM-DD') BETWEEN CONCAT(TO_CHAR(CURRENT_DATE, 'YYYY'), '-01-01')\n" +
                "    AND (SELECT TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI FROM ACC_PARAMETER) \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_REKENING", saldo.getKodeRekekning());
        map.addValue("saldoAwal", saldo.getSaldoAkhir());
        map.addValue("saldoAnggaran", saldo.getSaldoAnggaran());
        return this.namedParameterJdbcTemplate.update(finalQuery, map);
    }
}

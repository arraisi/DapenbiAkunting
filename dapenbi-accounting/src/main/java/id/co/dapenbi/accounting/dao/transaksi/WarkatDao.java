package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.laporan.PengantarWarkatDTO;
import id.co.dapenbi.accounting.dto.transaksi.ValidasiWarkatDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class WarkatDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<WarkatDTO> findAll() {
        String query = "SELECT NO_WARKAT,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       COALESCE(TOTAL_TRANSAKSI, 0) AS TOTAL_TRANSAKSI,\n" +
                "       TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT \n";

        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<WarkatDTO>() {
                @Override
                public WarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                    WarkatDTO value = new WarkatDTO();
                    TahunBuku tahunBuku = new TahunBuku();
                    tahunBuku.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
                    value.setTahunBuku(tahunBuku);

                    Periode kodePeriode = new Periode();
                    kodePeriode.setKodePeriode(resultSet.getString("KODE_PERIODE"));
                    value.setKodePeriode(kodePeriode);

                    Transaksi transaksi = new Transaksi();
                    transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                    value.setKodeTransaksi(transaksi);

                    return getWarkat(resultSet, value, tahunBuku, kodePeriode, transaksi);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<MSTLookUp> findJenisWarkat() {
        String query = "SELECT KODE_LOOKUP,\n" +
                "       JENIS_LOOKUP,\n" +
                "       NAMA_LOOKUP,\n" +
                "       KETERANGAN,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM MST_LOOKUP\n" +
                "WHERE STATUS_DATA = 1\n" +
                "  AND JENIS_LOOKUP = 'JENIS_WARKAT'\n";

        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<MSTLookUp>() {
                @Override
                public MSTLookUp mapRow(ResultSet resultSet, int i) throws SQLException {
                    MSTLookUp value = new MSTLookUp();
                    value.setJenisLookUp(resultSet.getString("JENIS_LOOKUP"));
                    value.setKodeLookUp(resultSet.getString("KODE_LOOKUP"));
                    value.setNamaLookUp(resultSet.getString("NAMA_LOOKUP"));
                    value.setKeterangan(resultSet.getString("KETERANGAN"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<MSTLookUp> findJenisJurnal() {
        String query = "SELECT KODE_LOOKUP,\n" +
                "       JENIS_LOOKUP,\n" +
                "       NAMA_LOOKUP,\n" +
                "       KETERANGAN,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM MST_LOOKUP\n" +
                "WHERE STATUS_DATA = 1\n" +
                "  AND JENIS_LOOKUP = 'JENIS_JURNAL'\n";

        try {
            return namedParameterJdbcTemplate.query(query, new HashMap<>(), new RowMapper<MSTLookUp>() {
                @Override
                public MSTLookUp mapRow(ResultSet resultSet, int i) throws SQLException {
                    MSTLookUp value = new MSTLookUp();
                    value.setJenisLookUp(resultSet.getString("JENIS_LOOKUP"));
                    value.setKodeLookUp(resultSet.getString("KODE_LOOKUP"));
                    value.setNamaLookUp(resultSet.getString("NAMA_LOOKUP"));
                    value.setKeterangan(resultSet.getString("KETERANGAN"));
                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Optional<BigDecimal> findSaldoAnggaran(Rekening rekening) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String query = "SELECT SC.SALDO_ANGGARAN\n" +
                "FROM ACC_SALDO_CURRENT SC\n" +
                "         LEFT JOIN ACC_REKENING R ON R.ID_REKENING = SC.ID_REKENING\n" +
                "WHERE SC.ID_REKENING = :ID_REKENING\n" +
                "  AND R.TIPE_REKENING = 'BIAYA' ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_REKENING", rekening.getIdRekening());
        return this.namedParameterJdbcTemplate.queryForObject(query, params, new RowMapper<Optional<BigDecimal>>() {
            @Override
            public Optional<BigDecimal> mapRow(ResultSet resultSet, int i) throws SQLException {
                return Optional.of(resultSet.getBigDecimal("SALDO_ANGGARAN"));
            }
        });
    }

    public List<String> findTipeRekeningByNoWarkat(String noWarkat) {
        //language=Oracle
        String query = "SELECT DISTINCT AR.TIPE_REKENING AS TIPE_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on SC.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> resultSet.getString("TIPE_REKENING"));
    }

    public Integer findAsetCondition(String noWarkat) {
        //language=Oracle
        String query = "SELECT count(DISTINCT AR.TIPE_REKENING) AS ROWCOUNT\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on SC.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n" +
                "  AND ((AR.TIPE_REKENING = 'KAS' AND AWJ.SALDO_NORMAL = 'D')\n" +
                "    OR (AR.TIPE_REKENING = 'ASET_OPR' AND AWJ.SALDO_NORMAL = 'K'))";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> resultSet.getInt("ROWCOUNT"));
    }

    public Integer checkDuplicateJurnalByNoWarkat(String noWarkat) {
        //language=Oracle
        String query = "SELECT count(*) DUPLICATE_COUNT FROM (SELECT AWJ.NO_WARKAT,\n" +
                "       AR.KODE_REKENING,\n" +
                "       COUNT(*) as rowCount\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on SC.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n" +
                "GROUP BY AWJ.NO_WARKAT,\n" +
                "         AR.KODE_REKENING\n" +
                "HAVING COUNT(*) > 1) x";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> resultSet.getInt("DUPLICATE_COUNT"));
    }

    public List<ValidasiWarkatDTO> findSaldoCurrentByNoWarkat(String noWarkat) {
        //language=Oracle
        String query = "SELECT AWJ.NO_WARKAT,\n" +
                "       AWJ.ID_REKENING,\n" +
                "       AR.SALDO_NORMAL,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0)  AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       COALESCE(SC.SALDO_DEBET, 0)    AS SALDO_DEBET,\n" +
                "       COALESCE(SC.SALDO_KREDIT, 0)   AS SALDO_KREDIT,\n" +
                "       COALESCE(SC.SALDO_ANGGARAN, 0) AS SALDO_ANGGARAN,\n" +
                "       COALESCE(SC.SALDO_AKHIR, 0)    AS SALDO_AKHIR,\n" +
                "       COALESCE(SC.SALDO_AWAL, 0)     AS SALDO_AWAL,\n" +
                "       COALESCE(SC.SERAP_TAMBAH, 0)   AS SERAP_TAMBAH,\n" +
                "       COALESCE(SC.SERAP_KURANG, 0)   AS SERAP_KURANG,\n" +
                "       COALESCE(SC.SALDO_JUAL, 0)     AS SALDO_JUAL,\n" +
                "       AR.TIPE_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_CURRENT SC on SC.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n ";

        return getValidasiWarkatDTOS(noWarkat, query);
    }

    public List<ValidasiWarkatDTO> findSaldoPAByNoWarkat(String noWarkat) {
        //language=Oracle
        String query = "SELECT AWJ.NO_WARKAT,\n" +
                "       AWJ.ID_REKENING,\n" +
                "       AR.SALDO_NORMAL,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0) AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       COALESCE(SPA.SALDO_DEBET, 0) AS SALDO_DEBET,\n" +
                "       COALESCE(SPA.SALDO_KREDIT, 0) AS SALDO_KREDIT,\n" +
                "       COALESCE(SPA.SALDO_ANGGARAN, 0) AS SALDO_ANGGARAN,\n" +
                "       COALESCE(SPA.SALDO_AKHIR, 0) AS SALDO_AKHIR,\n" +
                "       COALESCE(SPA.SALDO_AWAL, 0) AS SALDO_AWAL,\n" +
                "       COALESCE(SPA.SERAP_TAMBAH, 0) AS SERAP_TAMBAH,\n" +
                "       COALESCE(SPA.SERAP_KURANG, 0) AS SERAP_KURANG,\n" +
                "       COALESCE(SPA.SALDO_JUAL, 0)     AS SALDO_JUAL,\n" +
                "       AR.TIPE_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_PA SPA on SPA.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n ";

        return getValidasiWarkatDTOS(noWarkat, query);
    }

    public List<ValidasiWarkatDTO> findSaldoFAByNoWarkat(String noWarkat) {
        //language=Oracle
        String query = "SELECT AWJ.NO_WARKAT,\n" +
                "       AWJ.ID_REKENING,\n" +
                "       AR.SALDO_NORMAL,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0) AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0) AS JUMLAH_KREDIT,\n" +
                "       COALESCE(SFA.SALDO_DEBET, 0) AS SALDO_DEBET,\n" +
                "       COALESCE(SFA.SALDO_KREDIT, 0) AS SALDO_KREDIT,\n" +
                "       COALESCE(SFA.SALDO_ANGGARAN, 0) AS SALDO_ANGGARAN,\n" +
                "       COALESCE(SFA.SALDO_AKHIR, 0) AS SALDO_AKHIR,\n" +
                "       COALESCE(SFA.SALDO_AWAL, 0) AS SALDO_AWAL,\n" +
                "       COALESCE(SFA.SERAP_TAMBAH, 0) AS SERAP_TAMBAH,\n" +
                "       COALESCE(SFA.SERAP_KURANG, 0) AS SERAP_KURANG,\n" +
                "       COALESCE(SFA.SALDO_JUAL, 0)     AS SALDO_JUAL,\n" +
                "       AR.TIPE_REKENING\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_REKENING AR on AR.ID_REKENING = AWJ.ID_REKENING\n" +
                "         LEFT JOIN ACC_SALDO_FA SFA on SFA.ID_REKENING = AWJ.ID_REKENING\n" +
                "WHERE AWJ.NO_WARKAT = :NO_WARKAT\n ";

        return getValidasiWarkatDTOS(noWarkat, query);
    }

    private List<ValidasiWarkatDTO> getValidasiWarkatDTOS(String noWarkat, String query) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return this.namedParameterJdbcTemplate.query(query, params, new RowMapper<ValidasiWarkatDTO>() {
            @Override
            public ValidasiWarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                ValidasiWarkatDTO value = new ValidasiWarkatDTO();
                value.setNoWarkat(resultSet.getString("NO_WARKAT"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
                value.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setIdRekening(resultSet.getString("ID_REKENING"));
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

    public int updateSaldoCurrent(ValidasiWarkatDTO calculatedSaldo, String kodeDRI) {
        //language=Oracle
        String query = "" +
                "UPDATE ACC_SALDO_CURRENT\n" +
                "SET SALDO_DEBET    = :SALDO_DEBET,\n" +
                "    SALDO_KREDIT   = :SALDO_KREDIT,\n" +
                "    SALDO_ANGGARAN = :SALDO_ANGGARAN,\n" +
                "    SALDO_AKHIR    = :SALDO_AKHIR,\n" +
                "    SALDO_JUAL     = :SALDO_JUAL,\n" +
                "    KODE_DRI       = CASE\n" +
                "                         WHEN :KODE_DRI IS NULL THEN (SELECT KODE_DRI\n" +
                "                                                      FROM ACC_SALDO_CURRENT\n" +
                "                                                      WHERE ID_REKENING = :ID_REKENING)\n" +
                "                         ELSE :KODE_DRI END,\n" +
                "    UPDATED_BY     = :UPDATED_BY,\n" +
                "    UPDATED_DATE   = :UPDATED_DATE\n" +
                "WHERE ID_REKENING = :ID_REKENING";

        return this.namedParameterJdbcTemplate.update(query, paramsUpdateSaldo(calculatedSaldo, kodeDRI));
    }

    public int updateSaldoPA(ValidasiWarkatDTO calculatedSaldo, String kodeDRI) {
        //language=Oracle
        String query = "" +
                "UPDATE ACC_SALDO_PA\n" +
                "SET SALDO_DEBET    = :SALDO_DEBET,\n" +
                "    SALDO_KREDIT   = :SALDO_KREDIT,\n" +
                "    SALDO_ANGGARAN = :SALDO_ANGGARAN,\n" +
                "    SALDO_AKHIR    = :SALDO_AKHIR,\n" +
                "    SALDO_JUAL     = :SALDO_JUAL,\n" +
                "    KODE_DRI       = :KODE_DRI,\n" +
                "    UPDATED_BY     = :UPDATED_BY,\n" +
                "    UPDATED_DATE   = :UPDATED_DATE\n" +
                "WHERE ID_REKENING = :ID_REKENING";

        return this.namedParameterJdbcTemplate.update(query, paramsUpdateSaldo(calculatedSaldo, kodeDRI));
    }

    public int updateSaldoFA(ValidasiWarkatDTO calculatedSaldo, String kodeDRI) {
        //language=Oracle
        String query = "" +
                "UPDATE ACC_SALDO_FA\n" +
                "SET SALDO_DEBET    = :SALDO_DEBET,\n" +
                "    SALDO_KREDIT   = :SALDO_KREDIT,\n" +
                "    SALDO_ANGGARAN = :SALDO_ANGGARAN,\n" +
                "    SALDO_AKHIR    = :SALDO_AKHIR,\n" +
                "    SALDO_JUAL     = :SALDO_JUAL,\n" +
                "    KODE_DRI       = :KODE_DRI,\n" +
                "    UPDATED_BY     = :UPDATED_BY,\n" +
                "    UPDATED_DATE   = :UPDATED_DATE\n" +
                "WHERE ID_REKENING = :ID_REKENING";

        return this.namedParameterJdbcTemplate.update(query, paramsUpdateSaldo(calculatedSaldo, kodeDRI));
    }

    private MapSqlParameterSource paramsUpdateSaldo(ValidasiWarkatDTO calculatedSaldo, String kodeDRI) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("SALDO_DEBET", calculatedSaldo.getSaldoDebet());
        params.addValue("SALDO_KREDIT", calculatedSaldo.getSaldoKredit());
        params.addValue("SALDO_ANGGARAN", calculatedSaldo.getSaldoAnggaran());
        params.addValue("SALDO_AKHIR", calculatedSaldo.getSaldoAkhir());
        params.addValue("SALDO_JUAL", calculatedSaldo.getSaldoJual());
        params.addValue("KODE_DRI", kodeDRI);
        params.addValue("UPDATED_BY", calculatedSaldo.getCreatedBy());
        params.addValue("UPDATED_DATE", Timestamp.valueOf(LocalDateTime.now()));
        params.addValue("ID_REKENING", calculatedSaldo.getIdRekening());
        return params;
    }

    public int updateRealisasiBerjalanAnggaran(ValidasiWarkatDTO validasiWarkatDTO) {
        String query = "UPDATE ACC_ANGGARAN SET REALISASI_BERJALAN = :REALISASI_BERJALAN WHERE ID_REKENING = :ID_REKENING AND STATUS_AKTIF = '1' ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("REALISASI_BERJALAN", validasiWarkatDTO.getSaldoDebet());
        params.addValue("ID_REKENING", validasiWarkatDTO.getIdRekening());
        return namedParameterJdbcTemplate.update(query, params);
    }

    public List<WarkatDTO> datatable(DataTablesRequest<WarkatDTO> params, String search) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_WARKAT,\n" +
                "       NUWP,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       COALESCE(TOTAL_TRANSAKSI, 0) AS TOTAL_TRANSAKSI,\n" +
                "       TERBILANG(TOTAL_TRANSAKSI) AS TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       JENIS_WARKAT,\n" +
                "       ARUSKAS,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT WHERE 1=1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesWarkatQueryComparator queryComparator = new DatatablesWarkatQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns(
                "NO_WARKAT", "NO_WARKAT", "KODE_TRANSAKSI", "KETERANGAN", "TOTAL_TRANSAKSI", "TGL_TRANSAKSI", "STATUS_DATA");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<WarkatDTO>() {
            @Override
            public WarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatDTO value = new WarkatDTO();
                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                return getWarkat(resultSet, value, tahunBuku, periode, transaksi);
            }
        });
    }

    public Long datatable(WarkatDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesWarkatQueryComparator queryComparator = new DatatablesWarkatQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesWarkatQueryComparator implements QueryComparator<WarkatDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesWarkatQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(WarkatDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(WarkatDTO params, String value) {
            if (!params.getStartDate().isEmpty() && !params.getEndDate().isEmpty()) {
                builder.append(" AND TO_CHAR(TGL_TRANSAKSI, 'YYYY-MM-DD') BETWEEN :START_DATE AND :END_DATE ");
                map.addValue("START_DATE", params.getStartDate());
                map.addValue("END_DATE", params.getEndDate());
            }
            if (!params.getKodeTransaksi().getKodeTransaksi().isEmpty()) {
                builder.append(" AND KODE_TRANSAKSI = :KODE_TRANSAKSI ");
                map.addValue("KODE_TRANSAKSI", params.getKodeTransaksi().getKodeTransaksi());
            }
            if (!params.getStatusData().isEmpty()) {
                builder.append(" AND STATUS_DATA = :STATUS_DATA ");
                map.addValue("STATUS_DATA", params.getStatusData());
            }
            if (!params.getJenisWarkat().isEmpty()) {
                if (params.getJenisWarkat().equals("SSE")) {
                    List<String> jenisWarkats = new ArrayList<>();
                    jenisWarkats.add("AUDIT");
                    jenisWarkats.add("SSE");
                    builder.append(" AND JENIS_WARKAT IN (:JENIS_WARKAT) ");
                    map.addValue("JENIS_WARKAT", jenisWarkats);
                } else {
                    builder.append(" AND JENIS_WARKAT = :JENIS_WARKAT ");
                    map.addValue("JENIS_WARKAT", params.getJenisWarkat());
                }
            }
            if (!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like '%").append(value).append("%'\n")
                        .append("or lower(KODE_TRANSAKSI) like '%").append(value).append("%'\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%'\n")
                        .append("or lower(STATUS_DATA) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<WarkatDTO> datatableLog(DataTablesRequest<WarkatDTO> params, String search) {
        //language=Oracle
        String query = "SELECT R" +
                "OW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       ID_WARKAT_LOG,\n" +
                "       NO_WARKAT,\n" +
                "       AKTIVITAS,\n" +
                "       KETERANGAN,\n" +
                "       COALESCE(TOTAL_TRANSAKSI, 0) AS TOTAL_TRANSAKSI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT_LOG\n" +
                "WHERE 1 = 1 ";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesWarkatLogQueryComparator queryComparator = new DatatablesWarkatLogQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT", "KETERANGAN");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<WarkatDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<WarkatDTO>() {
            @Override
            public WarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatDTO value = new WarkatDTO();
                value.setTglTransaksi(resultSet.getTimestamp("CREATED_DATE"));
                value.setNoWarkat(resultSet.getString("NO_WARKAT"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                return value;
            }
        });
        return list;
    }

    public Long datatableLog(WarkatDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT_LOG WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesWarkatLogQueryComparator queryComparator = new DatatablesWarkatLogQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesWarkatLogQueryComparator implements QueryComparator<WarkatDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesWarkatLogQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(WarkatDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(WarkatDTO params, String value) {
            if (!params.getStatusData().isEmpty()) {
                builder.append(" AND STATUS_DATA = :STATUS_DATA ");
                map.addValue("STATUS_DATA", params.getStatusData());
            }
            if (!params.getCreatedBy().isEmpty()) {
                builder.append(" AND CREATED_BY = :CREATED_BY ");
                map.addValue("CREATED_BY", params.getCreatedBy());
            }
            if (!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like '%").append(value).append("%'\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<WarkatDTO> datatableJurnalBiaya(DataTablesRequest<WarkatDTO> params, String search, String statusData) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       NO_WARKAT,\n" +
                "       NUWP,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       COALESCE(TOTAL_TRANSAKSI, 0) AS TOTAL_TRANSAKSI,\n" +
                "       TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE\n" +
                "FROM ACC_WARKAT WHERE 1=1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        DatatablesJurnalBiayaQueryComparator queryComparator = new DatatablesJurnalBiayaQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, statusData);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<WarkatDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<WarkatDTO>() {
            @Override
            public WarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                WarkatDTO value = new WarkatDTO();
                TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
                Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
//                List<WarkatJurnal> warkatJurnals = warkatJurnalDao.findByNoWarkatDRI2(resultSet.getString("NO_WARKAT"));

                return getWarkat(resultSet, value, tahunBuku, periode, transaksi);
            }
        });

        return list;
    }

    public Long datatableJurnalBiaya(String search, String statusData) {
        String baseQuery = "select count(*) as value_row from ACC_WARKAT WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("statusData", statusData);

        DatatablesJurnalBiayaQueryComparator queryComparator = new DatatablesJurnalBiayaQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search, statusData);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesJurnalBiayaQueryComparator implements QueryComparator<WarkatDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesJurnalBiayaQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(WarkatDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value, String statusData) {
            if (!statusData.equalsIgnoreCase("null")) {
                builder.append("and STATUS_DATA = :STATUS_DATA ");
                map.addValue("STATUS_DATA", statusData);
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(NO_WARKAT) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(KODE_TRANSAKSI) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(TOTAL_TRANSAKSI) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(TGL_BUKU) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(STATUS_DATA) like '%").append(value).append("%' escape ' ')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    private WarkatDTO getWarkat(ResultSet resultSet, WarkatDTO value, TahunBuku tahunBuku, Periode periode, Transaksi transaksi) throws SQLException {
        value.setNoWarkat(resultSet.getString("NO_WARKAT"));
        value.setNuwp(resultSet.getString("NUWP"));
        value.setKodeTransaksi(transaksi);
        value.setIdOrg(resultSet.getInt("ID_ORG"));
        value.setKodeOrg(resultSet.getString("KODE_ORG"));
        value.setNamaOrg(resultSet.getString("NAMA_ORG"));
        value.setTahunBuku(tahunBuku);
        value.setKodePeriode(periode);
        value.setTglBuku(resultSet.getTimestamp("TGL_BUKU"));
        value.setTglTransaksi(resultSet.getTimestamp("TGL_TRANSAKSI"));
        value.setKeterangan(resultSet.getString("KETERANGAN"));
        value.setTotalTransaksi(resultSet.getBigDecimal("TOTAL_TRANSAKSI"));
        value.setTerbilang(resultSet.getString("TERBILANG"));
        value.setNoPengantarWarkat(resultSet.getString("NO_PENGANTARWARKAT"));
        value.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
        value.setUserValidasi(resultSet.getString("USER_VALIDASI"));
        value.setCatatanValidasi(resultSet.getString("CATATAN_VALIDASI"));
        value.setJenisWarkat(resultSet.getString("JENIS_WARKAT"));
        value.setArusKas(resultSet.getString("ARUSKAS"));
        value.setStatusData(resultSet.getString("STATUS_DATA"));
        value.setCreatedBy(resultSet.getString("CREATED_BY"));
        value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
        value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
        value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
        return value;
    }

    public List<MSTLookUp> getStatusWarkatList() {
        //language=Oracle
        String query = "SELECT KODE_LOOKUP, JENIS_LOOKUP, NAMA_LOOKUP, KETERANGAN, STATUS_DATA\n" +
                "FROM MST_LOOKUP\n" +
                "WHERE JENIS_LOOKUP = 'STATUS_WARKAT'\n" +
                "ORDER BY KETERANGAN ASC ";
        return namedParameterJdbcTemplate.query(query, new MapSqlParameterSource(), (resultSet, i) -> {
            MSTLookUp mstLookUp = new MSTLookUp();
            mstLookUp.setKodeLookUp(resultSet.getString("KODE_LOOKUP"));
            mstLookUp.setJenisLookUp(resultSet.getString("JENIS_LOOKUP"));
            mstLookUp.setNamaLookUp(resultSet.getString("NAMA_LOOKUP"));
            mstLookUp.setKeterangan(resultSet.getString("KETERANGAN"));
            mstLookUp.setStatusData(resultSet.getString("STATUS_DATA"));
            return mstLookUp;
        });
    }

    public List<PengantarWarkatDTO> pengantarWarkatDatatables(DataTablesRequest<PengantarWarkatDTO> params, String search) {
        String query = "SELECT \n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   aw.*\n" +
                "FROM ACC_WARKAT aw\n" +
                "WHERE 1 = 1\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        OrderingByColumns columns = new OrderingByColumns("aw.NO_WARKAT", "aw.NO_WARKAT");
        PengantarWarkatDatatablesQueryComparator queryComparator = new PengantarWarkatDatatablesQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<PengantarWarkatDTO>() {
            @Override
            public PengantarWarkatDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PengantarWarkatDTO value = new PengantarWarkatDTO();
                value.setNoWarkat(resultSet.getString("NO_WARKAT"));
                value.setKeterangan(resultSet.getString("KETERANGAN"));
                value.setTotalTransaksi(resultSet.getBigDecimal("TOTAL_TRANSAKSI"));
                return value;
            }
        });
    }

    public Long pengantarWarkatDatatables(PengantarWarkatDTO params, String search) {
        String query = "select count(*) as value_row\n" +
                "FROM ACC_WARKAT aw\n" +
                "WHERE 1 = 1\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        PengantarWarkatDatatablesQueryComparator queryComparator = new PengantarWarkatDatatablesQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        return this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, ((resultSet, i) -> resultSet.getLong("value_row")));
    }

    private static class PengantarWarkatDatatablesQueryComparator implements QueryComparator<PengantarWarkatDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public PengantarWarkatDatatablesQueryComparator(String baseQuery, MapSqlParameterSource map) {
            this.builder = new StringBuilder(baseQuery);
            this.map = map;
        }

        public StringBuilder getQuerySearch(PengantarWarkatDTO params, String value) {
//            log.info("Object: {}", params.getTglTransaksi());
            if (!params.getTglTransaksi().isEmpty()) {
                builder.append("and TO_CHAR(aw.TGL_TRANSAKSI, 'yyyy-MM-dd') = :tglTransaksi\n");
                map.addValue("tglTransaksi", params.getTglTransaksi());
            }

            if (!params.getStatusData().isEmpty()) {
                builder.append("and aw.STATUS_DATA = :statusData\n");
                map.addValue("statusData", params.getStatusData());
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(aw.NO_WARKAT) like '").append(value).append("%'\n")
                        .append("or lower(aw.KETERANGAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public StringBuilder getQuery(PengantarWarkatDTO params) {
            return null;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public WarkatDTO findByNoWarkat(String noWarkat) {
        String query = "SELECT NO_WARKAT,\n" +
                "       NUWP,\n" +
                "       KODE_TRANSAKSI,\n" +
                "       ID_ORG,\n" +
                "       KODE_ORG,\n" +
                "       NAMA_ORG,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE,\n" +
                "       TGL_BUKU,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KETERANGAN,\n" +
                "       COALESCE(TOTAL_TRANSAKSI, 0) AS TOTAL_TRANSAKSI,\n" +
                "       TERBILANG(TOTAL_TRANSAKSI) AS TERBILANG,\n" +
                "       NO_PENGANTARWARKAT,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       CATATAN_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       JENIS_WARKAT,\n" +
                "       ARUSKAS\n" +
                "FROM ACC_WARKAT WHERE NO_WARKAT = :NO_WARKAT\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("noWarkat", noWarkat);
        return namedParameterJdbcTemplate.query(query, params, resultSet -> {
            WarkatDTO value = new WarkatDTO();
            TahunBuku tahunBuku = new TahunBuku(resultSet.getString("KODE_THNBUKU"));
            Periode periode = new Periode(resultSet.getString("KODE_PERIODE"));
            Transaksi transaksi = new Transaksi();
            transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
            return getWarkat(resultSet, value, tahunBuku, periode, transaksi);
        });
    }
}

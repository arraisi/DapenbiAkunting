package id.co.dapenbi.accounting.dao.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TransaksiJurnalDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //language=Oracle
    String querySelect = "" +
            "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
            "       TJ.ID_TRANSAKSI_JURNAL,\n" +
            "       TJ.KODE_TRANSAKSI,\n" +
            "       TJ.ID_REKENING,\n" +
            "       TJ.SALDO_NORMAL,\n" +
            "       TJ.NO_URUT,\n" +
            "       TJ.CREATED_BY,\n" +
            "       TJ.CREATED_DATE,\n" +
            "       TJ.UPDATED_BY,\n" +
            "       TJ.UPDATED_DATE,\n" +
            "       AR.KODE_REKENING,\n" +
            "       AR.NAMA_REKENING,\n" +
            "       AR.SALDO_NORMAL AS AR_SALDO_NORMAL\n" +
            "FROM ACC_JNS_TRANSAKSI_JURNAL TJ\n" +
            "LEFT JOIN ACC_REKENING AR on TJ.ID_REKENING = AR.ID_REKENING\n" +
            "WHERE 1 = 1 ";

    public int save(TransaksiJurnalDTO transaksiJurnal) {
        log.info("{}", transaksiJurnal);
        String query = "INSERT INTO ACC_JNS_TRANSAKSI_JURNAL (\n" +
                "    KODE_TRANSAKSI, \n" +
                "    ID_REKENING, \n" +
                "    SALDO_NORMAL, \n" +
                "    NO_URUT, \n" +
                "    CREATED_BY, \n" +
                "    CREATED_DATE, \n" +
                "    UPDATED_BY, \n" +
                "    UPDATED_DATE) \n" +
                "VALUES (\n" +
                "    :kodeTransaksi,\n" +
                "    :idRekening,\n" +
                "    :saldoNormal,\n" +
                "    :noUrut,\n" +
                "    :createdBy,\n" +
                "    :createdDate,\n" +
                "    :updatedBy,\n" +
                "    :updatedDate)\n";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("kodeTransaksi", transaksiJurnal.getKodeTransaksi().getKodeTransaksi());
            params.addValue("idRekening", transaksiJurnal.getIdRekening().getIdRekening());
            params.addValue("saldoNormal", transaksiJurnal.getSaldoNormal());
            params.addValue("noUrut", transaksiJurnal.getNoUrut());
            params.addValue("createdBy", transaksiJurnal.getCreatedBy());
            params.addValue("createdDate", transaksiJurnal.getCreatedDate());
            params.addValue("updatedBy", transaksiJurnal.getUpdatedBy());
            params.addValue("updatedDate", transaksiJurnal.getUpdatedDate());
            return this.namedParameterJdbcTemplate.update(query, params);
        } catch (DataAccessException e) {
            log.error("error disini", e);
            return 0;
        }
    }

    public int deleteByKodeTransaksi(String kodeTransaksi) {
        String query = "DELETE FROM ACC_JNS_TRANSAKSI_JURNAL WHERE KODE_TRANSAKSI = :KODE_TRANSAKSI";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_TRANSAKSI", kodeTransaksi);
            return this.namedParameterJdbcTemplate.update(query, params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return 0;
        }
    }

    private List<TransaksiJurnal> getTransaksiJurnals(String query, MapSqlParameterSource params) {
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<TransaksiJurnal>() {
            @Override
            public TransaksiJurnal mapRow(ResultSet resultSet, int i) throws SQLException {
                TransaksiJurnal value = new TransaksiJurnal();
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                Rekening rekening = new Rekening();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setSaldoNormal(resultSet.getString("AR_SALDO_NORMAL"));

                value.setIdTransaksiJurnal(resultSet.getInt("ID_TRANSAKSI_JURNAL"));
                value.setKodeTransaksi(transaksi);
                value.setIdRekening(rekening);
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setNoUrut(resultSet.getString("NO_URUT"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));

                return value;
            }
        });
    }

    private Optional<TransaksiJurnal> getTransaksiJurnal(String query, MapSqlParameterSource params) {
        return namedParameterJdbcTemplate.queryForObject(query, params, new RowMapper<Optional<TransaksiJurnal>>() {
            @Override
            public Optional<TransaksiJurnal> mapRow(ResultSet resultSet, int i) throws SQLException {
                TransaksiJurnal value = new TransaksiJurnal();
                Transaksi transaksi = new Transaksi();
                transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                Rekening rekening = new Rekening();
                rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                rekening.setSaldoNormal(resultSet.getString("AR_SALDO_NORMAL"));

                value.setIdTransaksiJurnal(resultSet.getInt("ID_TRANSAKSI_JURNAL"));
                value.setKodeTransaksi(transaksi);
                value.setIdRekening(rekening);
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setNoUrut(resultSet.getString("NO_URUT"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));

                return Optional.of(value);
            }
        });
    }

    public List<TransaksiJurnalDTO> findByKodeTransaksi(String kodeTransaksi) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       TJ.ID_TRANSAKSI_JURNAL,\n" +
                "       TJ.KODE_TRANSAKSI,\n" +
                "       TJ.ID_REKENING,\n" +
                "       TJ.SALDO_NORMAL,\n" +
                "       TJ.NO_URUT,\n" +
                "       TJ.CREATED_BY,\n" +
                "       TJ.CREATED_DATE,\n" +
                "       TJ.UPDATED_BY,\n" +
                "       TJ.UPDATED_DATE,\n" +
                "       AR.TIPE_REKENING,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.SALDO_NORMAL                     AS AR_SALDO_NORMAL,\n" +
                "       COALESCE(S.SALDO_AKHIR, 0)          AS SALDO_AKHIR,\n" +
                "       COALESCE(S.SALDO_DEBET, 0)          AS SALDO_DEBET,\n" +
                "       COALESCE(S.SALDO_KREDIT, 0)         AS SALDO_KREDIT\n" +
                "FROM ACC_JNS_TRANSAKSI_JURNAL TJ\n" +
                "         LEFT JOIN ACC_REKENING AR on TJ.ID_REKENING = AR.ID_REKENING\n" +
                "         LEFT JOIN (SELECT ID_REKENING, KODE_DRI, SALDO_DEBET, SALDO_KREDIT, SALDO_AKHIR FROM ACC_SALDO_FA) S\n" +
                "                   on AR.ID_REKENING = S.ID_REKENING\n" +
                "WHERE TJ.KODE_TRANSAKSI = :KODE_TRANSAKSI ";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_TRANSAKSI", kodeTransaksi);
            return namedParameterJdbcTemplate.query(query, params, new RowMapper<TransaksiJurnalDTO>() {
                @Override
                public TransaksiJurnalDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                    TransaksiJurnalDTO value = new TransaksiJurnalDTO();
                    TransaksiDTO transaksi = new TransaksiDTO();
                    transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                    RekeningDTO rekening = new RekeningDTO();
                    rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                    rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    rekening.setSaldoNormal(resultSet.getString("AR_SALDO_NORMAL"));

                    value.setIdTransaksiJurnal(resultSet.getInt("ID_TRANSAKSI_JURNAL"));
                    value.setKodeTransaksi(transaksi);
                    value.setIdRekening(rekening);
                    value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                    value.setNoUrut(resultSet.getString("NO_URUT"));
                    value.setCreatedBy(resultSet.getString("CREATED_BY"));
                    value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));

                    final String saldoNormal = resultSet.getString("SALDO_NORMAL");
                    if (saldoNormal.equalsIgnoreCase("K")) {
                        value.setJumlahKredit(resultSet.getBigDecimal("SALDO_AKHIR"));
                        value.setJumlahDebit(BigDecimal.ZERO);
                    } else {
                        value.setJumlahKredit(BigDecimal.ZERO);
                        value.setJumlahDebit(resultSet.getBigDecimal("SALDO_AKHIR"));
                    }

                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<TransaksiJurnalDTO> findByKodeTransaksiForJurnalTransaksi(String kodeTransaksi) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       TJ.ID_TRANSAKSI_JURNAL,\n" +
                "       TJ.KODE_TRANSAKSI,\n" +
                "       TJ.ID_REKENING,\n" +
                "       TJ.SALDO_NORMAL,\n" +
                "       TJ.NO_URUT,\n" +
                "       TJ.CREATED_BY,\n" +
                "       TJ.CREATED_DATE,\n" +
                "       TJ.UPDATED_BY,\n" +
                "       TJ.UPDATED_DATE,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.SALDO_NORMAL                     AS AR_SALDO_NORMAL,\n" +
                "       COALESCE(S.SALDO_AKHIR, 0)          AS SALDO_AKHIR\n" +
                "FROM ACC_JNS_TRANSAKSI_JURNAL TJ\n" +
                "         LEFT JOIN ACC_REKENING AR on TJ.ID_REKENING = AR.ID_REKENING\n" +
                "         LEFT JOIN (SELECT ID_REKENING, KODE_DRI, SALDO_DEBET, SALDO_KREDIT, SALDO_AKHIR FROM ACC_SALDO_FA) S\n" +
                "                   on AR.ID_REKENING = S.ID_REKENING\n" +
                "WHERE TJ.KODE_TRANSAKSI = :KODE_TRANSAKSI ";

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_TRANSAKSI", kodeTransaksi);
            return namedParameterJdbcTemplate.query(query, params, new RowMapper<TransaksiJurnalDTO>() {
                @Override
                public TransaksiJurnalDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                    TransaksiJurnalDTO value = new TransaksiJurnalDTO();
                    TransaksiDTO transaksi = new TransaksiDTO();
                    transaksi.setKodeTransaksi(resultSet.getString("KODE_TRANSAKSI"));
                    RekeningDTO rekening = new RekeningDTO();
                    rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
                    rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    rekening.setSaldoNormal(resultSet.getString("AR_SALDO_NORMAL"));

                    value.setIdTransaksiJurnal(resultSet.getInt("ID_TRANSAKSI_JURNAL"));
                    value.setKodeTransaksi(transaksi);
                    value.setIdRekening(rekening);
                    value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                    value.setNoUrut(resultSet.getString("NO_URUT"));
                    value.setCreatedBy(resultSet.getString("CREATED_BY"));
                    value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                    value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                    value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));

                    if (resultSet.getString("SALDO_NORMAL").equals("D")) {
                        value.setJumlahDebit(resultSet.getBigDecimal("SALDO_AKHIR"));
                        value.setJumlahKredit(BigDecimal.ZERO);
                    } else {
                        value.setJumlahDebit(BigDecimal.ZERO);
                        value.setJumlahKredit(resultSet.getBigDecimal("SALDO_AKHIR"));
                    }

                    return value;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<TransaksiJurnal> findByKodeTransaksiDRI2(String kodeTransaksi) {
        //language=Oracle
        String query = "" +
                "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       TJ.ID_TRANSAKSI_JURNAL,\n" +
                "       TJ.KODE_TRANSAKSI,\n" +
                "       TJ.ID_REKENING,\n" +
                "       TJ.SALDO_NORMAL,\n" +
                "       TJ.NO_URUT,\n" +
                "       TJ.CREATED_BY,\n" +
                "       TJ.CREATED_DATE,\n" +
                "       TJ.UPDATED_BY,\n" +
                "       TJ.UPDATED_DATE,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       AR.SALDO_NORMAL AS AR_SALDO_NORMAL\n" +
                "FROM ACC_JNS_TRANSAKSI_JURNAL TJ\n" +
                "LEFT JOIN ACC_REKENING AR on TJ.ID_REKENING = AR.ID_REKENING\n" +
                "LEFT JOIN (SELECT ID_REKENING, KODE_DRI FROM ACC_SALDO GROUP BY ID_REKENING, KODE_DRI) S on AR.ID_REKENING = S.ID_REKENING\n" +
                "WHERE 1 = 1 AND TJ.KODE_TRANSAKSI = :KODE_TRANSAKSI AND S.KODE_DRI = 2";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_TRANSAKSI", kodeTransaksi);
            return getTransaksiJurnals(query, params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Optional<TransaksiJurnal> findById(Integer id) {
        StringBuilder query = new StringBuilder();
        query.append(querySelect).append("AND KODE_TRANSAKSI = :ID_TRANSAKSI_JURNAL ");

        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("ID_TRANSAKSI_JURNAL", id);
            return getTransaksiJurnal(query.toString(), params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public Iterable<TransaksiJurnal> findAll() {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            return getTransaksiJurnals(querySelect, params);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<TransaksiJurnal> datatables(DataTablesRequest<TransaksiJurnal> params, String search) {
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiJurnalDao.DatatablesTransaksiJurnalQueryComparator queryComparator =
                new TransaksiJurnalDao.DatatablesTransaksiJurnalQueryComparator(querySelect, map);

        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);

        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("ID_TRANSAKSI_JURNAL", "KODE_TRANSAKSI", "ID_REKENING");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return getTransaksiJurnals(finalQuery, map);
    }

    public Long datatables(TransaksiJurnal params, String search) {
        //language=Oracle
        String baseQuery = "SELECT COUNT(*) AS VALUE_ROW\n" +
                "FROM ACC_JNS_TRANSAKSI_JURNAL\n" +
                "WHERE 1 = 1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiJurnalDao.DatatablesTransaksiJurnalQueryComparator queryComparator = new TransaksiJurnalDao.DatatablesTransaksiJurnalQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesTransaksiJurnalQueryComparator implements QueryComparator<Transaksi> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesTransaksiJurnalQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(Transaksi params) {
            return null;
        }

        public StringBuilder getQuerySearch(TransaksiJurnal params, String value) {
            if (params != null) {
                if (params.getKodeTransaksi() != null) {
                    builder.append("and KODE_TRANSAKSI = :KODE_TRANSAKSI ");
                    map.addValue("KODE_TRANSAKSI", params.getKodeTransaksi().getKodeTransaksi());
                }
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(KODE_TRANSAKSI) = '").append(value).append("' \n")
                        .append("or lower(ID_REKENING) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(SALDO_NORMAL) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(NO_URUT) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(CREATED_BY) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(CREATED_DATE) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(UPDATED_BY) like '%").append(value).append("%' escape ' '\n")
                        .append("or lower(UPDATED_DATE) like '%").append(value).append("%' escape ' ')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<TransaksiJurnalDTO> findDataJurnal(String tipeRekening) {
        String query = "SELECT R.ID_REKENING,\n" +
                "       R.SALDO_NORMAL,\n" +
                "       R.KODE_REKENING,\n" +
                "       R.NAMA_REKENING,\n" +
                "       COALESCE(S.SALDO_AKHIR, 0) AS SALDO_AKHIR\n" +
                "FROM ACC_SALDO_FA S\n" +
                "         LEFT JOIN ACC_REKENING R ON S.ID_REKENING = R.ID_REKENING\n" +
                "WHERE S.SALDO_AKHIR > 0 AND R.TIPE_REKENING IN (:TIPE_REKENING) ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (tipeRekening.equalsIgnoreCase("BIAYA")) {
            List<String> tipeRekenings = new ArrayList<>();
//            tipeRekenings.add("ASET_OPR");
            tipeRekenings.add("BIAYA");
            params.addValue("TIPE_REKENING", tipeRekenings);
        } else if (tipeRekening.equalsIgnoreCase("PENDAPATAN")) {
            params.addValue("TIPE_REKENING", tipeRekening);
        }
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            TransaksiJurnalDTO transaksiJurnal = new TransaksiJurnalDTO();
            RekeningDTO rekening = new RekeningDTO();
            rekening.setIdRekening(resultSet.getInt("ID_REKENING"));
            rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
            rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            if (tipeRekening.equalsIgnoreCase("BIAYA")) {
                rekening.setSaldoNormal("K");
                transaksiJurnal.setSaldoNormal("K");
                transaksiJurnal.setJumlahKredit(resultSet.getBigDecimal("SALDO_AKHIR"));
                transaksiJurnal.setJumlahDebit(BigDecimal.ZERO);
            } else {
                rekening.setSaldoNormal("D");
                transaksiJurnal.setSaldoNormal("D");
                transaksiJurnal.setJumlahKredit(BigDecimal.ZERO);
                transaksiJurnal.setJumlahDebit(resultSet.getBigDecimal("SALDO_AKHIR"));
            }
            transaksiJurnal.setIdRekening(rekening);
            return transaksiJurnal;
        });
    }

    public List<BigDecimal> findPajakPph(String kodePeriode, String tahunBuku) {
        //language=Oracle
        String query = "SELECT coalesce(SALDO_BERJALAN, 0) as SALDO_BERJALAN\n" +
                "FROM LAP_KEU\n" +
                "WHERE KODE_RUMUS = 'F10'\n" +
                "  AND KODE_DRI = '1'\n" +
                "  AND KODE_PERIODE = :KODE_PERIODE\n" +
                "  AND KODE_THNBUKU = (SELECT KODE_THNBUKU FROM ACC_THNBUKU WHERE TAHUN = :TAHUN)";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_PERIODE", kodePeriode);
            params.addValue("TAHUN", tahunBuku);
            return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> resultSet.getBigDecimal("SALDO_BERJALAN"));
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<BigDecimal> findHutangPph(String kodePeriode, String tahunBuku) {
        //language=Oracle
        String query = "SELECT ABS(coalesce(SALDO_BERJALAN, 0)) as SALDO_BERJALAN\n" +
                "FROM LAP_KEU\n" +
                "WHERE KODE_RUMUS = 'F15'\n" +
                "  AND KODE_DRI = '1'\n" +
                "  AND KODE_PERIODE = :KODE_PERIODE\n" +
                "  AND KODE_THNBUKU = (SELECT KODE_THNBUKU FROM ACC_THNBUKU WHERE TAHUN = :TAHUN)";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("KODE_PERIODE", kodePeriode);
            params.addValue("TAHUN", tahunBuku);
            return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> resultSet.getBigDecimal("SALDO_BERJALAN"));
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
            return null;
        }
    }
}

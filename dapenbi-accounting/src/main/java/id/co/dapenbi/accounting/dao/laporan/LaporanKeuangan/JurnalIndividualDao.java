package id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository
public class JurnalIndividualDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<JurnalIndividualDTO> datatable(DataTablesRequest<JurnalIndividualDTO> params, String search) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM)     as no,\n" +
                "       AW.NO_WARKAT,\n" +
                "       TO_CHAR(AW.TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0)           AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0)          AS JUMLAH_KREDIT,\n" +
                "       AW.NUWP,\n" +
                "       COALESCE(AW.KETERANGAN, '-')            AS KETERANGAN\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_WARKAT AW on AWJ.NO_WARKAT = AW.NO_WARKAT\n" +
                "         LEFT JOIN ACC_REKENING AR on AWJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE AW.STATUS_DATA = 'FA' \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalIndividualDao.DatatablesJurnalIndividualQueryComparator queryComparator = new JurnalIndividualDao.DatatablesJurnalIndividualQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("NO_WARKAT", "KODE_REKENING");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));
        if (!params.getColDir().isEmpty()) {
            stringBuilder.append(", KODE_REKENING DESC");
        }

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return getJurnalIndividual(finalQuery, map);
    }

    public Long datatable(JurnalIndividualDTO params, String search) {
        String baseQuery = "SELECT COUNT(*) as value_row\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_WARKAT AW on AWJ.NO_WARKAT = AW.NO_WARKAT\n" +
                "         LEFT JOIN ACC_REKENING AR on AWJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE AW.STATUS_DATA = 'FA' \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        JurnalIndividualDao.DatatablesJurnalIndividualQueryComparator queryComparator = new JurnalIndividualDao.DatatablesJurnalIndividualQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesJurnalIndividualQueryComparator implements QueryComparator<JurnalIndividualDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesJurnalIndividualQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(JurnalIndividualDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(JurnalIndividualDTO params, String value) {
            if (params != null) {
                if (!params.getStartDate().isEmpty() && !params.getEndDate().isEmpty()) {
                    builder.append(" AND TO_CHAR(AW.TGL_TRANSAKSI, 'YYYY-MM-DD') between :TGL_PERIODE1 AND :TGL_PERIODE2 ");
                    map.addValue("TGL_PERIODE1", params.getStartDate());
                    map.addValue("TGL_PERIODE2", params.getEndDate());
                }
            }

            if (!value.isEmpty()) {
                builder.append("and (lower(KODE_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(NAMA_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(JUMLAH_DEBIT) like '%").append(value).append("%'\n")
                        .append("or lower(JUMLAH_KREDIT) like '%").append(value).append("%'\n")
                        .append("or lower(NUWP) like '%").append(value).append("%'\n")
                        .append("or lower(KETERANGAN) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<JurnalIndividualDTO> findAll() {
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM)     AS no,\n" +
                "       AW.NO_WARKAT,\n" +
                "       TO_CHAR(AW.TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0)           AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0)          AS JUMLAH_KREDIT,\n" +
                "       AW.NUWP,\n" +
                "       COALESCE(AW.KETERANGAN, '-')            AS KETERANGAN\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_WARKAT AW on AWJ.NO_WARKAT = AW.NO_WARKAT\n" +
                "         LEFT JOIN ACC_REKENING AR on AWJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE AW.STATUS_DATA = 'FA'\n" +
                "ORDER BY AW.NO_WARKAT ASC, AR.KODE_REKENING DESC ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return getJurnalIndividual(query, params);
    }

    public List<JurnalIndividualDTO> findAllByDate(JurnalIndividualDTO parameter) {
        //language=Oracle
        String query = "SELECT " +
                "TO_CHAR(W.TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI,\n" +
                "       W.NUWP                                 AS NUWP,\n" +
                "       W.NO_WARKAT                            AS NO_WARKAT,\n" +
                "       COALESCE(W.KETERANGAN, '-')            AS KETERANGAN\n" +
                "FROM ACC_WARKAT W,\n" +
                "     ACC_WARKAT_JURNAL WJ\n" +
                "WHERE W.NO_WARKAT = WJ.NO_WARKAT\n" +
                "  AND WJ.NO_WARKAT IN (\n" +
                "    SELECT a.NO_WARKAT\n" +
                "    FROM ACC_WARKAT_JURNAL a\n" +
                "    WHERE (a.ID_REKENING = :ID_REKENING OR :ID_REKENING IS Null))\n" +
                "  AND TO_CHAR(W.TGL_TRANSAKSI, 'YYYY-MM-DD') BETWEEN :STARTDATE AND :ENDDATE\n" +
                "  " +
                "AND W.STATUS_DATA = 'FA'\n" +
                "GROUP BY W.TGL_TRANSAKSI, W.NUWP, W.KETERANGAN, w.NO_WARKAT ORDER BY W.NUWP";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("STARTDATE", parameter.getStartDate());
        params.addValue("ENDDATE", parameter.getEndDate());
        params.addValue("ID_REKENING", parameter.getIdRekening());
        return getJurnalIndividual(query, params);
    }

    public JurnalIndividualDTO getSummaryValue(String startDate, String endDate, Integer idRekening) {
        //language=Oracle
        String query = "SELECT COUNT(*) AS TOTALWP, SUM(X.JUMLAH_DEBIT) AS JUMLAH_DEBIT, SUM(X.JUMLAH_KREDIT) AS JUMLAH_KREDIT\n" +
                "FROM (SELECT TO_CHAR(W.TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI,\n" +
                "             W.NUWP                                 AS NUWP,\n" +
                "             W.NO_WARKAT                            AS NO_WARKAT,\n" +
                "             COALESCE(W.KETERANGAN, '-')            AS KETERANGAN,\n" +
                "             SUM(WJ.JUMLAH_DEBIT)                   AS JUMLAH_DEBIT,\n" +
                "             SUM(WJ.JUMLAH_KREDIT)                  AS JUMLAH_KREDIT\n" +
                "      FROM ACC_WARKAT W,\n" +
                "           ACC_WARKAT_JURNAL WJ\n" +
                "      WHERE W.NO_WARKAT = WJ.NO_WARKAT\n" +
                "        AND WJ.NO_WARKAT IN (\n" +
                "          SELECT a.NO_WARKAT\n" +
                "          FROM ACC_WARKAT_JURNAL a\n" +
                "          WHERE (a.ID_REKENING = :ID_REKENING OR :ID_REKENING IS Null))\n" +
                "        AND TO_CHAR(W.TGL_TRANSAKSI, 'YYYY-MM-DD') BETWEEN :STARTDATE AND :ENDDATE\n" +
                "        " +
                "AND W.STATUS_DATA = 'FA'\n" +
                "      GROUP BY W.TGL_TRANSAKSI, W.NUWP, W.KETERANGAN, w.NO_WARKAT) X";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("STARTDATE", startDate);
        params.addValue("ENDDATE", endDate);
        params.addValue("ID_REKENING", idRekening);
        return namedParameterJdbcTemplate.queryForObject(query, params, (resultSet, i) -> {
           JurnalIndividualDTO summary = new JurnalIndividualDTO();
           summary.setTotalWarkatPembukuan(resultSet.getBigDecimal("TOTALWP"));
           summary.setTotalMutasiDebet(resultSet.getBigDecimal("JUMLAH_DEBIT"));
           summary.setTotalMutasiKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
           return summary;
        });
    }

    private List<JurnalIndividualDTO> getJurnalIndividual(String query, MapSqlParameterSource params) {
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<JurnalIndividualDTO>() {
            @Override
            public JurnalIndividualDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                JurnalIndividualDTO jurnalIndividualDTO = new JurnalIndividualDTO();
                jurnalIndividualDTO.setNoWarkat(resultSet.getString("NO_WARKAT"));
                jurnalIndividualDTO.setTglTransaksi(resultSet.getString("TGL_TRANSAKSI"));
                jurnalIndividualDTO.setNuwp(resultSet.getString("NUWP"));
                jurnalIndividualDTO.setKeterangan(resultSet.getString("KETERANGAN"));
                jurnalIndividualDTO.setJurnalIndividualDetails(findDetails(resultSet.getString("NO_WARKAT")));
                return jurnalIndividualDTO;
            }
        });
    }

    public List<JurnalIndividualDTO.JurnalIndividualDetails> findDetails(String noWarkat) {
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM)     AS no,\n" +
                "       AW.NO_WARKAT,\n" +
                "       TO_CHAR(AW.TGL_TRANSAKSI, 'YYYY-MM-DD') AS TGL_TRANSAKSI,\n" +
                "       AR.KODE_REKENING,\n" +
                "       AR.NAMA_REKENING,\n" +
                "       COALESCE(AWJ.JUMLAH_DEBIT, 0)           AS JUMLAH_DEBIT,\n" +
                "       COALESCE(AWJ.JUMLAH_KREDIT, 0)          AS JUMLAH_KREDIT,\n" +
                "       AW.NUWP,\n" +
                "       COALESCE(AW.KETERANGAN, '-')            AS KETERANGAN\n" +
                "FROM ACC_WARKAT_JURNAL AWJ\n" +
                "         LEFT JOIN ACC_WARKAT AW on AWJ.NO_WARKAT = AW.NO_WARKAT\n" +
                "         LEFT JOIN ACC_REKENING AR on AWJ.ID_REKENING = AR.ID_REKENING\n" +
                "WHERE AW.NO_WARKAT = :NO_WARKAT\n" +
                "ORDER BY AW.NO_WARKAT ASC, AR.KODE_REKENING DESC ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("NO_WARKAT", noWarkat);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            JurnalIndividualDTO.JurnalIndividualDetails value = new JurnalIndividualDTO.JurnalIndividualDetails();
            value.setTglTransaksi(resultSet.getString("TGL_TRANSAKSI"));
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
            value.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
            return value;
        });
    }

    public List<JurnalIndividualDTO.Rekening> findListRekening() {
        String query = "SELECT DISTINCT R.ID_REKENING,\n" +
                "       R.KODE_REKENING,\n" +
                "       R.NAMA_REKENING,\n" +
                "       R.LEVEL_REKENING,\n" +
                "       R.TIPE_REKENING,\n" +
                "       R.URUTAN,\n" +
                "       R.SALDO_NORMAL,\n" +
                "       R.STATUS_AKTIF,\n" +
                "       AWJ.NO_WARKAT\n" +
                "FROM ACC_REKENING R\n" +
                "         LEFT JOIN ACC_WARKAT_JURNAL AWJ on R.ID_REKENING = AWJ.ID_REKENING\n" +
                "ORDER BY R.KODE_REKENING ASC\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(query, params, new RowMapper<JurnalIndividualDTO.Rekening>() {
            @Override
            public JurnalIndividualDTO.Rekening mapRow(ResultSet resultSet, int i) throws SQLException {
                JurnalIndividualDTO.Rekening value = new JurnalIndividualDTO.Rekening();
                value.setIdRekening(resultSet.getInt("ID_REKENING"));
                value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                value.setLevelRekening(resultSet.getInt("LEVEL_REKENING"));
                value.setTipeRekening(resultSet.getString("TIPE_REKENING"));
                value.setUrutan(resultSet.getInt("URUTAN"));
                value.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setNoWarkat(resultSet.getString("NO_WARKAT"));
                value.setNuwp(resultSet.getString("NUWP"));
                return value;
            }
        });
    }
}

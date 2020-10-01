package id.co.dapenbi.accounting.dao.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dao.parameter.RekeningDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.parameter.SaldoCurrentDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository
public class RekeningIndividualDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private RekeningDao rekeningDao;

    public List<RekeningIndividualDTO> datatable(DataTablesRequest<RekeningIndividualDTO> params, String search) {
        //language=Oracle
        String query =
                "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                        "       NO_WARKAT,\n" +
                        "       SALDO_NORMAL,\n" +
                        "       TGL_TRANSAKSI,\n" +
                        "       KODE_REKENING,\n" +
                        "       NAMA_REKENING,\n" +
                        "       SALDO_AWAL,\n" +
                        "       JUMLAH_DEBIT,\n" +
                        "       JUMLAH_KREDIT,\n" +
                        "       SALDO,\n" +
                        "       NUWP,\n" +
                        "       KETERANGAN\n" +
                        "FROM V_BUKU_BESAR WHERE 1=1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        RekeningIndividualDao.DatatablesJurnalIndividualQueryComparator queryComparator = new RekeningIndividualDao.DatatablesJurnalIndividualQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns(
                "TGL_TRANSAKSI",
                "KODE_REKENING",
                "NAMA_REKENING",
                "JUMLAH_DEBIT",
                "JUMLAH_KREDIT",
                "SALDO",
                "NUWP",
                "KETERANGAN");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));
        if (!params.getColDir().isEmpty()) {
            stringBuilder.append(", KODE_REKENING DESC");
        }

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        List<RekeningIndividualDTO> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<RekeningIndividualDTO>() {
            @Override
            public RekeningIndividualDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                RekeningIndividualDTO RekeningIndividualDTO = new RekeningIndividualDTO();
                RekeningIndividualDTO.setNoWarkat(resultSet.getString("NO_WARKAT"));
                RekeningIndividualDTO.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
                RekeningIndividualDTO.setTglTransaksi(resultSet.getString("TGL_TRANSAKSI"));
                RekeningIndividualDTO.setKodeRekening(resultSet.getString("KODE_REKENING"));
                RekeningIndividualDTO.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                RekeningIndividualDTO.setJumlahDebit(resultSet.getBigDecimal("JUMLAH_DEBIT"));
                RekeningIndividualDTO.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
                RekeningIndividualDTO.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
                RekeningIndividualDTO.setSaldo(resultSet.getBigDecimal("SALDO"));
                RekeningIndividualDTO.setNuwp(resultSet.getString("NUWP"));
                RekeningIndividualDTO.setKeterangan(resultSet.getString("KETERANGAN"));
                return RekeningIndividualDTO;
            }
        });

        return list;
    }

    public Long datatable(RekeningIndividualDTO params, String search) {
        String baseQuery = "SELECT COUNT(*) as value_row FROM V_BUKU_BESAR WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        RekeningIndividualDao.DatatablesJurnalIndividualQueryComparator queryComparator = new RekeningIndividualDao.DatatablesJurnalIndividualQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesJurnalIndividualQueryComparator implements QueryComparator<RekeningIndividualDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesJurnalIndividualQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(RekeningIndividualDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(RekeningIndividualDTO params, String value) {
            if (params != null) {
                if (!params.getTglPeriode1().isEmpty() && !params.getTglPeriode2().isEmpty()) {
                    builder.append(" AND TGL_TRANSAKSI between :TGL_PERIODE1 AND :TGL_PERIODE2 ");
                    map.addValue("TGL_PERIODE1", params.getTglPeriode1());
                    map.addValue("TGL_PERIODE2", params.getTglPeriode2());
                }
                if (!params.getKodeRekening().isEmpty()) {
                    builder.append(" AND KODE_REKENING = :KODE_REKENING ");
                    map.addValue("KODE_REKENING", params.getKodeRekening());
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

    public List<WarkatDTO> getData(List<String> idList) {
        //language=Oracle
        String query = "SELECT * FROM ACC_WARKAT";
        return null;
    }

    public List<RekeningIndividualDTO.Header> findRekeningByRange(String startDate, String endDate, String kodeRekening) throws ParseException {
        StringBuilder queryBuilder = new StringBuilder();
        //language=Oracle
        String query =
                "SELECT DISTINCT BB.KODE_REKENING,\n" +
                        "                BB.NAMA_REKENING,\n" +
                        "                BB.SALDO_NORMAL,\n" +
                        "                BB.TGL_TRANSAKSI,\n" +
                        "\n" +
                        "                TB.KODE_THNBUKU,\n" +
                        "                TB.NAMA_THNBUKU,\n" +
                        "                P.KODE_PERIODE,\n" +
                        "                P.NAMA_PERIODE,\n" +
                        "\n" +
                        "                COALESCE((SELECT F_SALDO_AWAL(:START_DATE, KODE_REKENING)\n" +
                        "                          FROM V_BUKU_BESAR\n" +
                        "                          WHERE TGL_TRANSAKSI BETWEEN :START_DATE AND :END_DATE\n" +
                        "                            AND KODE_REKENING = BB.KODE_REKENING\n" +
                        "                            AND ROWNUM = 1), 0) AS SALDO_AWAL,\n" +
                        "\n" +
                        "                COALESCE((SELECT F_SALDO_AKHIR(:END_DATE, KODE_REKENING)\n" +
                        "                          FROM V_BUKU_BESAR\n" +
                        "                          WHERE TGL_TRANSAKSI BETWEEN :START_DATE AND :END_DATE\n" +
                        "                            AND KODE_REKENING = BB.KODE_REKENING\n" +
                        "                            AND ROWNUM = 1), 0) AS SALDO_AKHIR\n" +
                        "\n" +
                        "FROM V_BUKU_BESAR BB\n" +
                        "         LEFT JOIN ACC_THNBUKU TB ON TB.TAHUN = TO_CHAR(TO_DATE(BB.TGL_TRANSAKSI, 'YYYY-MM-DD'), 'YYYY')\n" +
                        "         LEFT JOIN ACC_PERIODE P ON P.KODE_PERIODE = TO_CHAR(TO_DATE(BB.TGL_TRANSAKSI, 'YYYY-MM-DD'), 'MM')\n" +
                        "WHERE BB.TGL_TRANSAKSI BETWEEN :START_DATE AND :END_DATE ";
        queryBuilder.append(query);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("START_DATE", startDate);
        params.addValue("END_DATE", endDate);
        if (!kodeRekening.isEmpty()) {
            queryBuilder.append(" AND BB.KODE_REKENING = :KODE_REKENING ");
            params.addValue("KODE_REKENING", kodeRekening);
        }
        return namedParameterJdbcTemplate.query(queryBuilder.toString(), params, (resultSet, i) -> {
            RekeningIndividualDTO.Header rekening = new RekeningIndividualDTO.Header();

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat startDateFormat = new SimpleDateFormat("dd MMMM", new Locale("in", "ID"));
                DateFormat endDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("in", "ID"));

                rekening.setTglPeriode1(startDateFormat.format(formatter.parse(startDate)));
                rekening.setTglPeriode2(endDateFormat.format(formatter.parse(endDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final List<RekeningIndividualDTO> detailsRekeningByRange = findDetailsRekeningByRange(startDate, endDate, resultSet.getString("KODE_REKENING"));
            final int lastIndexDetails = detailsRekeningByRange.size() - 1;

            rekening.setKodeRekening(resultSet.getString("KODE_REKENING"));
            rekening.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            rekening.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
            rekening.setNamaTahunBuku(resultSet.getString("NAMA_THNBUKU"));
            rekening.setKodePeriode(resultSet.getString("KODE_PERIODE"));
            rekening.setNamaPeriode(resultSet.getString("NAMA_PERIODE"));
            rekening.setSaldoNormal(resultSet.getString("SALDO_NORMAL"));
            rekening.setSaldoAwal(resultSet.getBigDecimal("SALDO_AWAL"));
            rekening.setSaldoAkhir(detailsRekeningByRange.get(lastIndexDetails).getSaldo());
            rekening.setDetails(detailsRekeningByRange);
            return rekening;
        });
    }

    public List<RekeningIndividualDTO> findDetailsRekeningByRange(String startDate, String endDate, String kodeRekening) {
        String query = "" +
                "SELECT NO,\n" +
                "       NO_WARKAT,\n" +
                "       SALDO_NORMAL,\n" +
                "       TGL_TRANSAKSI,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       COALESCE(JUMLAH_DEBIT, 0)       AS JUMLAH_DEBIT,\n" +
                "       COALESCE(JUMLAH_KREDIT, 0)      AS JUMLAH_KREDIT,\n" +
                "       SALDO,\n" +
                "       NUWP,\n" +
                "       KETERANGAN,\n" +
                "\n" +
                "       COALESCE((SELECT F_SALDO_AWAL(:START_DATE, KODE_REKENING)\n" +
                "                 FROM V_BUKU_BESAR\n" +
                "                 WHERE TGL_TRANSAKSI BETWEEN :START_DATE AND :END_DATE\n" +
                "                   AND KODE_REKENING = BB.KODE_REKENING\n" +
                "                   AND ROWNUM = 1), 0) AS SALDO_AWAL,\n" +
                "\n" +
                "       COALESCE((\n" +
                "                    SELECT SUM(COALESCE(JUMLAH_DEBIT, 0))\n" +
                "                    FROM V_BUKU_BESAR\n" +
                "                    WHERE NO < BB.NO\n" +
                "                      AND TGL_TRANSAKSI between :START_DATE AND :END_DATE\n" +
                "                      AND KODE_REKENING = :KODE_REKENING\n" +
                "                ), 0) + JUMLAH_DEBIT  AS TOTAL_DEBIT_PREV,\n" +
                "\n" +
                "       COALESCE((\n" +
                "                    SELECT SUM(COALESCE(JUMLAH_KREDIT, 0))\n" +
                "                    FROM V_BUKU_BESAR\n" +
                "                    WHERE NO < BB.NO\n" +
                "                      AND TGL_TRANSAKSI between :START_DATE AND :END_DATE\n" +
                "                      AND KODE_REKENING = :KODE_REKENING\n" +
                "                ), 0) + JUMLAH_KREDIT  AS TOTAL_KREDIT_PREV\n" +
                "FROM V_BUKU_BESAR BB\n" +
                "WHERE 1 = 1\n" +
                "  AND BB.TGL_TRANSAKSI between :START_DATE AND :END_DATE\n" +
                "  AND BB.KODE_REKENING = :KODE_REKENING";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("START_DATE", startDate);
        params.addValue("END_DATE", endDate);
        params.addValue("KODE_REKENING", kodeRekening);
        return namedParameterJdbcTemplate.query(query, params, (resultSet, i) -> {
            RekeningIndividualDTO RekeningIndividualDTO = new RekeningIndividualDTO();
            final String saldoNormal = resultSet.getString("SALDO_NORMAL");
            final BigDecimal saldoAwal = resultSet.getBigDecimal("SALDO_AWAL");
            final BigDecimal jumlahDebit = resultSet.getBigDecimal("JUMLAH_DEBIT");
            final BigDecimal totalDebitPrev = resultSet.getBigDecimal("TOTAL_DEBIT_PREV");
            final BigDecimal totalKreditPrev = resultSet.getBigDecimal("TOTAL_KREDIT_PREV");

            RekeningIndividualDTO.setNoWarkat(resultSet.getString("NO_WARKAT"));
            RekeningIndividualDTO.setSaldoNormal(saldoNormal);
            RekeningIndividualDTO.setTglTransaksi(resultSet.getString("TGL_TRANSAKSI"));
            RekeningIndividualDTO.setKodeRekening(resultSet.getString("KODE_REKENING"));
            RekeningIndividualDTO.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            RekeningIndividualDTO.setJumlahDebit(jumlahDebit);
            RekeningIndividualDTO.setJumlahKredit(resultSet.getBigDecimal("JUMLAH_KREDIT"));
            RekeningIndividualDTO.setSaldoAwal(saldoAwal);
            RekeningIndividualDTO.setSaldo(saldoNormal.equals("D") ? saldoAwal.add(totalDebitPrev).subtract(totalKreditPrev) : saldoAwal.subtract(totalDebitPrev).add(totalKreditPrev));
            RekeningIndividualDTO.setNuwp(resultSet.getString("NUWP"));
            RekeningIndividualDTO.setKeterangan(resultSet.getString("KETERANGAN"));
            return RekeningIndividualDTO;
        });
    }
}

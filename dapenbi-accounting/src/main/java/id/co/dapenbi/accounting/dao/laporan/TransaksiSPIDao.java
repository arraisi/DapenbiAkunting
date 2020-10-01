package id.co.dapenbi.accounting.dao.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.laporan.SPIDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIHeaderDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
@Slf4j
public class TransaksiSPIDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static SPIDetailDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        TahunBuku tahunBuku = new TahunBuku();
        tahunBuku.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
        tahunBuku.setNamaTahunBuku(resultSet.getString("NAMA_THNBUKU"));
        Periode periode = new Periode();
        periode.setKodePeriode(resultSet.getString("KODE_PERIODE"));
        periode.setNamaPeriode(resultSet.getString("NAMA_PERIODE"));

        SPIDetailDTO value = new SPIDetailDTO();
        value.setIdSPIDtl(resultSet.getInt("ID_SPI_DTL"));
        value.setIdSPIHdr(resultSet.getInt("ID_SPI_HDR"));
        value.setKeteranganSPI(resultSet.getString("KETERANGAN_SPI"));
        value.setKodeTahunBuku(tahunBuku);
        value.setKodePeriode(periode);
        value.setTglSPI(resultSet.getTimestamp("TGL_SPI"));
        value.setIdInvestasi(resultSet.getString("ID_INVESTASI"));
        value.setIdSPI(resultSet.getString("ID_SPI"));
        value.setStatusData(resultSet.getString("STATUS_DATA"));
        value.setNilaiPerolehan(resultSet.getBigDecimal("NILAI_PEROLEHAN"));
        value.setNilaiWajar(resultSet.getBigDecimal("NILAI_WAJAR"));
        value.setNilaiSPI(resultSet.getBigDecimal("NILAI_SPI"));
        value.setCreatedBy(resultSet.getString("CREATED_BY"));
        value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
        value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
        value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
        return value;
    }

    public int deleteHdr(Integer idSPIHdr) {
        //language=Oracle
        String query = "DELETE FROM ACC_SPI_HDR WHERE ID_SPI_HDR = :ID_SPI_HDR ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_SPI_HDR", idSPIHdr);
        return namedParameterJdbcTemplate.update(query, params);
    }

    public int deleteDetailByIdHdr(Integer idSPIHdr) {
        //language=Oracle
        String query = "DELETE FROM ACC_SPI_DTL WHERE ID_SPI_HDR = :ID_SPI_HDR ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID_SPI_HDR", idSPIHdr);
        return namedParameterJdbcTemplate.update(query, params);
    }

    public List<SPIHeaderDTO> datatable(DataTablesRequest<SPIHeaderDTO> params, String search) {
        //language=Oracle
        String query = "SELECT" +
                " ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       ID_SPI_HDR,\n" +
                "       S.KODE_THNBUKU,\n" +
                "       T.NAMA_THNBUKU,\n" +
                "       S.KODE_PERIODE,\n" +
                "       P.NAMA_PERIODE,\n" +
                "       TGL_SPI,\n" +
                "       TGL_VALIDASI,\n" +
                "       USER_VALIDASI,\n" +
                "       STATUS_DATA,\n" +
                "       S.CREATED_BY,\n" +
                "       S.CREATED_DATE,\n" +
                "       S.UPDATED_BY,\n" +
                "       S.UPDATED_DATE\n" +
                "FROM ACC_SPI_HDR S\n" +
                "         LEFT JOIN ACC_THNBUKU T on S.KODE_THNBUKU = T.KODE_THNBUKU\n" +
                "         LEFT JOIN ACC_PERIODE P on S.KODE_PERIODE = P.KODE_PERIODE\n" +
                "WHERE 1 = 1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiSPIDao.DatatablesTransaksiSPIQueryComparator queryComparator = new DatatablesTransaksiSPIQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns(
                "KODE_THNBUKU", "KODE_THNBUKU", "KODE_PERIODE", "TGL_SPI", "CREATED_DATE", "CREATED_BY", "STATUS_DATA");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            TahunBuku tahunBuku = new TahunBuku();
            tahunBuku.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
            tahunBuku.setNamaTahunBuku(resultSet.getString("NAMA_THNBUKU"));
            Periode periode = new Periode();
            periode.setKodePeriode(resultSet.getString("KODE_PERIODE"));
            periode.setNamaPeriode(resultSet.getString("NAMA_PERIODE"));

            SPIHeaderDTO value = new SPIHeaderDTO();
            value.setIdSPIHdr(resultSet.getInt("ID_SPI_HDR"));
            value.setKodeTahunBuku(tahunBuku);
            value.setKodePeriode(periode);
            value.setTglSPI(resultSet.getTimestamp("TGL_SPI"));
            value.setTglValidasi(resultSet.getTimestamp("TGL_VALIDASI"));
            value.setUserValidasi(resultSet.getString("USER_VALIDASI"));
            value.setStatusData(resultSet.getString("STATUS_DATA"));
            value.setCreatedBy(resultSet.getString("CREATED_BY"));
            value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
            value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));
            return value;
        });
    }

    public Long datatable(SPIHeaderDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_SPI_HDR WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiSPIDao.DatatablesTransaksiSPIQueryComparator queryComparator = new DatatablesTransaksiSPIQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    public List<SPIDetailDTO> findDetailByIdSPIHdr(Integer id) {
        //language=Oracle
        String query = "SELECT " +
                "SPIDTL.ID_SPI_DTL,\n" +
                "       SPIDTL.ID_SPI_HDR,\n" +
                "       SPIDTL.KODE_THNBUKU,\n" +
                "       T.NAMA_THNBUKU,\n" +
                "       SPIDTL.KODE_PERIODE,\n" +
                "       P.NAMA_PERIODE,\n" +
                "       (SELECT KETERANGAN_SPI\n" +
                "        FROM ACC_INVESTASI_DTL\n" +
                "        WHERE ID_INVESTASI = SPIDTL.ID_INVESTASI\n" +
                "          and ID_SPI = SPIDTL.ID_SPI) AS KETERANGAN_SPI,\n" +
                "       SPIDTL.TGL_SPI,\n" +
                "       SPIDTL.ID_INVESTASI,\n" +
                "       SPIDTL.ID_SPI,\n" +
                "       SPIDTL.STATUS_DATA,\n" +
                "       SPIDTL.NILAI_PEROLEHAN,\n" +
                "       SPIDTL.NILAI_WAJAR,\n" +
                "       SPIDTL.NILAI_SPI,\n" +
                "       SPIDTL.CREATED_BY,\n" +
                "       SPIDTL.CREATED_DATE,\n" +
                "       SPIDTL.UPDATED_BY,\n" +
                "       SPIDTL.UPDATED_DATE\n" +
                "FROM ACC_SPI_DTL SPIDTL\n" +
                "         LEFT JOIN ACC_THNBUKU T on SPIDTL.KODE_THNBUKU = T.KODE_THNBUKU\n" +
                "         LEFT JOIN ACC_PERIODE P on SPIDTL.KODE_PERIODE = P.KODE_PERIODE\n" +
                "WHERE ID_SPI_HDR = :ID \n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ID", id);
        return namedParameterJdbcTemplate.query(query, params, TransaksiSPIDao::mapRow);
    }

    private static class DatatablesTransaksiSPIQueryComparator implements QueryComparator<SPIHeaderDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesTransaksiSPIQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(SPIHeaderDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(SPIHeaderDTO params, String value) {
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public List<SPIDetailDTO> SPIDetaildatatables(DataTablesRequest<SPIDetailDTO> params, String search) {
        //language=Oracle
        String query = "SELECT ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       SPIDTL.ID_SPI_DTL,\n" +
                "       SPIDTL.ID_SPI_HDR,\n" +
                "       SPIDTL.KODE_THNBUKU,\n" +
                "       T.NAMA_THNBUKU,\n" +
                "       SPIDTL.KODE_PERIODE,\n" +
                "       P.NAMA_PERIODE,\n" +
                "       (SELECT KETERANGAN_SPI\n" +
                "        FROM ACC_INVESTASI_DTL\n" +
                "        WHERE ID_INVESTASI = SPIDTL.ID_INVESTASI\n" +
                "          and ID_SPI = SPIDTL.ID_SPI)      AS KETERANGAN_SPI,\n" +
                "       SPIDTL.TGL_SPI,\n" +
                "       SPIDTL.ID_INVESTASI,\n" +
                "       SPIDTL.ID_SPI,\n" +
                "       SPIDTL.STATUS_DATA,\n" +
                "       SPIDTL.NILAI_PEROLEHAN,\n" +
                "       SPIDTL.NILAI_WAJAR,\n" +
                "       SPIDTL.NILAI_SPI,\n" +
                "       SPIDTL.CREATED_BY,\n" +
                "       SPIDTL.CREATED_DATE,\n" +
                "       SPIDTL.UPDATED_BY,\n" +
                "       SPIDTL.UPDATED_DATE\n" +
                "FROM ACC_SPI_DTL SPIDTL\n" +
                "         LEFT JOIN ACC_THNBUKU T on SPIDTL.KODE_THNBUKU = T.KODE_THNBUKU\n" +
                "         LEFT JOIN ACC_PERIODE P on SPIDTL.KODE_PERIODE = P.KODE_PERIODE\n" +
                "WHERE 1 = 1";

        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiSPIDao.DatatablesTransaksiSPIDTLQueryComparator queryComparator = new DatatablesTransaksiSPIDTLQueryComparator(query, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params.getValue(), search);
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns(
                "ID_SPI_DTL", "KETERANGAN_SPI", "NILAI_PEROLEHAN", "NILAI_WAJAR", "NILAI_SPI");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());

        return this.namedParameterJdbcTemplate.query(finalQuery, map, TransaksiSPIDao::mapRow);
    }

    public Long SPIDetaildatatables(SPIDetailDTO params, String search) {
        String baseQuery = "select count(*) as value_row from ACC_SPI_DTL SPIDTL WHERE 1=1 \n";
        MapSqlParameterSource map = new MapSqlParameterSource();

        TransaksiSPIDao.DatatablesTransaksiSPIDTLQueryComparator queryComparator = new DatatablesTransaksiSPIDTLQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(params, search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private static class DatatablesTransaksiSPIDTLQueryComparator implements QueryComparator<SPIDetailDTO> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesTransaksiSPIDTLQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(SPIDetailDTO params) {
            return null;
        }

        public StringBuilder getQuerySearch(SPIDetailDTO params, String value) {
//            if (!params.get_tglSPI().isEmpty()) {
//                builder.append(" AND TO_CHAR(TGL_SPI, 'YYYY-MM-DD') = :TGL_SPI ");
//                map.addValue("TGL_SPI", params.get_tglSPI());
//            }
//            if (!params.getKodeThnBuku().isEmpty()) {
//                builder.append(" AND KODE_THNBUKU = :KODE_THNBUKU ");
//                map.addValue("KODE_THNBUKU", params.getKodeThnBuku());
//            }
//            if (!params.getKodePeriode().isEmpty()) {
//                builder.append(" AND KODE_PERIODE = :KODE_PERIODE ");
//                map.addValue("KODE_PERIODE", params.getKodePeriode());
//            }
            if (params.getIdSPIHdr() != null) {
                builder.append(" AND ID_SPI_HDR = :ID_SPI_HDR ");
                map.addValue("ID_SPI_HDR", params.getIdSPIHdr());
            }
            if (!value.isEmpty()) {
                builder.append("and (lower(ID_SPI_DTL) like '%").append(value).append("%'\n")
                        .append("or lower((SELECT KETERANGAN_SPI\n" +
                                "        FROM ACC_INVESTASI_DTL\n" +
                                "        WHERE ID_INVESTASI = SPIDTL.ID_INVESTASI\n" +
                                "          and ID_SPI = SPIDTL.ID_SPI)) like '%").append(value).append("%'\n")
                        .append("or lower(NILAI_PEROLEHAN) like '%").append(value).append("%'\n")
                        .append("or lower(NILAI_WAJAR) like '%").append(value).append("%'\n")
                        .append("or lower(NILAI_SPI) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public int mergeSPIDetail(SPIDetailDTO spiDetailDTO, Principal principal) {
        String query = "MERGE INTO ACC_SPI_DTL A\n" +
                "USING DUAL\n" +
                "ON (ID_INVESTASI = :idInvestasi AND ID_SPI =:idSpi AND ID_SPI_HDR = :idSpiHdr)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET KODE_THNBUKU    = :kodeThnbuku,\n" +
                "        KODE_PERIODE    = :kodePeriode,\n" +
                "        TGL_SPI         = :tglSpi,\n" +
                "        STATUS_DATA     = :statusData,\n" +
                "        NILAI_PEROLEHAN = :nilaiPerolehan,\n" +
                "        NILAI_WAJAR     = :nilaiWajar,\n" +
                "        NILAI_SPI       = :nilaiSpi,\n" +
                "        CREATED_BY      = :createdBy,\n" +
                "        CREATED_DATE    = :createdDate,\n" +
                "        UPDATED_BY      = :updatedBy,\n" +
                "        UPDATED_DATE    = :updatedDate\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_SPI_DTL, ID_SPI_HDR, KODE_THNBUKU, KODE_PERIODE, TGL_SPI, ID_INVESTASI, ID_SPI, STATUS_DATA,\n" +
                "            NILAI_PEROLEHAN, NILAI_WAJAR, NILAI_SPI, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)\n" +
                "    VALUES (:idSpiDtl, :idSpiHdr, :kodeThnbuku, :kodePeriode, :tglSpi, :idInvestasi, :idSpi, :statusData,\n" +
                "            :nilaiPerolehan, :nilaiWajar, :nilaiSpi, :createdBy, :createdDate, :updatedBy, :updatedDate)\n" +
                "\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idSpiDtl", spiDetailDTO.getIdSPIDtl());
        params.addValue("idSpiHdr", spiDetailDTO.getIdSPIHdr());
        params.addValue("kodeThnbuku", spiDetailDTO.getKodeTahunBuku().getKodeTahunBuku());
        params.addValue("kodePeriode", spiDetailDTO.getKodePeriode().getKodePeriode());
        params.addValue("tglSpi", spiDetailDTO.getTglSPI());
        params.addValue("idInvestasi", spiDetailDTO.getIdInvestasi());
        params.addValue("idSpi", spiDetailDTO.getIdSPI());
        params.addValue("statusData", spiDetailDTO.getStatusData());
        params.addValue("nilaiPerolehan", spiDetailDTO.getNilaiPerolehan());
        params.addValue("nilaiWajar", spiDetailDTO.getNilaiWajar());
        params.addValue("nilaiSpi", spiDetailDTO.getNilaiSPI());
        params.addValue("createdBy", spiDetailDTO.getCreatedBy());
        params.addValue("createdDate", spiDetailDTO.getCreatedDate());
        params.addValue("updatedBy", principal.getName());
        params.addValue("updatedDate", Timestamp.valueOf(LocalDateTime.now()));
        return namedParameterJdbcTemplate.update(query, params);
    }
}

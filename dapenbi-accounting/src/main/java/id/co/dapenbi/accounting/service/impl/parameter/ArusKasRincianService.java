package id.co.dapenbi.accounting.service.impl.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import commons.ui.datatables.OrderingByColumns;
import commons.utils.PageableLimitOffset;
import commons.utils.QueryComparator;
import commons.utils.oracle.Oracle11LimitOffset;
import id.co.dapenbi.accounting.dto.parameter.ArusKasRincianDTO;
import id.co.dapenbi.accounting.entity.parameter.ArusKasRincian;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.repository.parameter.ArusKasRincianRepository;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArusKasRincianService {

    @Autowired
    private ArusKasRincianRepository arusKasRincianRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private RekeningRepository rekeningRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ArusKasRincianDTO.ArusKasRincianEntity save(ArusKasRincianDTO.ArusKasRincianEntity arusKasRincian) {
        arusKasRincian.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
//        arusKasRincian = arusKasRincianRepository.save(arusKasRincian);

        String query = "INSERT INTO \n" +
                "   ACC_ARUSKAS_RINCIAN\n" +
                "   (ID_ARUSKAS, KODE_ARUSKAS, ID_REKENING, KETERANGAN, STATUS_AKTIF, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, SALDO_AWALTAHUN, FLAG_RUMUS, FLAG_GROUP, FLAG_REKENING)\n" +
                "VALUES\n" +
                "   (:kodeRincian, :kodeArusKas, :idRekening, :keterangan, :statusAktif, :createdBy, :createdDate, :updatedBy, :updatedDate, :saldoAwalTahun, :flagRumus, :flagGroup, :flagRekening)\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeRincian", arusKasRincian.getKodeRincian());
        map.addValue("kodeArusKas", arusKasRincian.getKodeArusKas());
        map.addValue("idRekening", arusKasRincian.getIdRekening());
        map.addValue("keterangan", arusKasRincian.getKeterangan());
        map.addValue("statusAktif", arusKasRincian.getStatusAktif());
        map.addValue("createdBy", arusKasRincian.getCreatedBy());
        map.addValue("createdDate", arusKasRincian.getCreatedDate());
        map.addValue("updatedBy", arusKasRincian.getUpdatedBy());
        map.addValue("updatedDate", arusKasRincian.getUpdatedDate());
        map.addValue("saldoAwalTahun", arusKasRincian.getSaldoAwalTahun());
        map.addValue("flagRumus", arusKasRincian.getFlagRumus());
        map.addValue("flagGroup", arusKasRincian.getFlagGroup());
        map.addValue("flagRekening", arusKasRincian.getFlagRekening());

        this.namedParameterJdbcTemplate.update(query, map);

        return arusKasRincian;
    }

    public Optional<ArusKasRincianDTO.ArusKasRincianEntity> findByKodeArusKasAndKodeArusKasRincian(String kodeArusKas, String kodeRincian) {
        String query = "SELECT\n" +
                "   akr.ID_ARUSKAS        AS kodeRincian,\n" +
                "   akr.CREATED_BY          AS createdBy,\n" +
                "   akr.CREATED_DATE        AS createdDate,\n" +
                "   akr.FLAG_GROUP          AS flagGroup,\n" +
                "   akr.FLAG_RUMUS          AS flagRumus,\n" +
                "   akr.ID_REKENING         AS idRekening,\n" +
                "   akr.KETERANGAN          AS keterangan,\n" +
                "   akr.KODE_ARUSKAS        AS kodeArusKas,\n" +
                "   akr.SALDO_AWALTAHUN     AS saldoAwalTahun,\n" +
                "   akr.STATUS_AKTIF        AS statusAktif,\n" +
                "   akr.UPDATED_BY          AS updatedBy,\n" +
                "   akr.FLAG_REKENING       AS flagRekening,\n" +
                "   akr.UPDATED_DATE        AS updatedDate  \n" +
                "FROM ACC_ARUSKAS_RINCIAN akr\n" +
                "WHERE akr.KODE_ARUSKAS = :kodeArusKas\n" +
                "AND akr.ID_ARUSKAS = :kodeRincian\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeArusKas", kodeArusKas);
        map.addValue("kodeRincian", kodeRincian);

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<ArusKasRincianDTO.ArusKasRincianEntity>>() {
                @Override
                public Optional<ArusKasRincianDTO.ArusKasRincianEntity> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Optional<Rekening> optionalRekening = rekeningRepository.findById(resultSet.getInt("idRekening"));
                    Rekening rekening = optionalRekening.isPresent() ? optionalRekening.get() : new Rekening();
                    String detailRekening = optionalRekening.isPresent() ? rekening.getKodeRekening() + " - " + rekening.getNamaRekening() : "";
                    ArusKasRincianDTO.ArusKasRincianEntity value = new ArusKasRincianDTO.ArusKasRincianEntity();
                    value.setKodeRincian(resultSet.getString("kodeRincian"));
                    value.setKodeArusKas(resultSet.getString("kodeArusKas"));
                    value.setIdRekening(resultSet.getString("idRekening"));
                    value.setKeterangan(resultSet.getString("keterangan"));
                    value.setSaldoAwalTahun(resultSet.getBigDecimal("saldoAwalTahun"));
                    value.setFlagRumus(resultSet.getString("flagRumus"));
                    value.setFlagGroup(resultSet.getString("flagGroup"));
                    value.setFlagRekening(resultSet.getString("flagRekening"));
                    value.setStatusAktif(resultSet.getString("statusAktif"));
                    value.setCreatedBy(resultSet.getString("createdBy"));
                    value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                    value.setUpdatedBy(resultSet.getString("updatedBy"));
                    value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                    value.setDetailRekening(detailRekening);
                    return Optional.of(value);
                }
            });
        } catch(EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ArusKasRincianDTO.ArusKasRincianEntity update(ArusKasRincianDTO.ArusKasRincianEntity arusKasRincian) {
        arusKasRincian.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
//        arusKasRincian = arusKasRincianRepository.save(arusKasRincian);

        String query = "UPDATE \n" +
                "   ACC_ARUSKAS_RINCIAN akr\n" +
                "SET\n" +
                "   akr.ID_ARUSKAS    = :kodeRincian,\n" +
                "   akr.KODE_ARUSKAS    = :kodeArusKas,\n" +
                "   akr.ID_REKENING     = :idRekening,\n" +
                "   akr.KETERANGAN      = :keterangan,\n" +
                "   akr.STATUS_AKTIF    = :statusAktif,\n" +
//                "   akr.CREATED_BY      = :createdBy,\n" +
//                "   akr.CREATED_DATE    = :createdDate,\n" +
                "   akr.UPDATED_BY      = :updatedBy,\n" +
                "   akr.UPDATED_DATE    = :updatedDate,\n" +
                "   akr.SALDO_AWALTAHUN = :saldoAwalTahun,\n" +
                "   akr.FLAG_RUMUS      = :flagRumus,\n" +
                "   akr.FLAG_GROUP      = :flagGroup,\n" +
                "   akr.FLAG_REKENING   = :flagRekening\n" +
                "WHERE akr.ID_ARUSKAS = :kodeRincian AND akr.KODE_ARUSKAS = :kodeArusKas\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeRincian", arusKasRincian.getKodeRincian());
        map.addValue("kodeArusKas", arusKasRincian.getKodeArusKas());
        map.addValue("idRekening", arusKasRincian.getIdRekening());
        map.addValue("keterangan", arusKasRincian.getKeterangan());
        map.addValue("statusAktif", arusKasRincian.getStatusAktif());
//        map.addValue("createdBy", arusKasRincian.getCreatedBy());
//        map.addValue("createdDate", arusKasRincian.getCreatedDate());
        map.addValue("updatedBy", arusKasRincian.getUpdatedBy());
        map.addValue("updatedDate", arusKasRincian.getUpdatedDate());
        map.addValue("saldoAwalTahun", arusKasRincian.getSaldoAwalTahun());
        map.addValue("flagRumus", arusKasRincian.getFlagRumus());
        map.addValue("flagGroup", arusKasRincian.getFlagGroup());
        map.addValue("flagRekening", arusKasRincian.getFlagRekening());

        this.namedParameterJdbcTemplate.update(query, map);

        return arusKasRincian;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String kodeRincian, String kodeArusKas) {
        String query = "DELETE FROM ACC_ARUSKAS_RINCIAN WHERE ID_ARUSKAS = :kodeRincian AND KODE_ARUSKAS = :kodeArusKas\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeRincian", kodeRincian);
        map.addValue("kodeArusKas", kodeArusKas);

        this.namedParameterJdbcTemplate.update(query, map);
    }

    public DataTablesOutput<ArusKasRincian> findForDataTable(DataTablesInput input) {
        return arusKasRincianRepository.findAll(input);
    }

    public DataTablesResponse<ArusKasRincian> datatableByArusKasId(DataTablesRequest<ArusKasRincian> params, String id, String search) {
        List<ArusKasRincian> data = findForDataTableByArusKasId(params, id, search);
        Long rowCount = findForDataTableByArusKasId(search, id);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<ArusKasRincian> findForDataTableByArusKasId(DataTablesRequest<ArusKasRincian> params, String id, String search) {
        String query = "SELECT\n" +
                "   ROW_NUMBER() over (%s) as no,\n" +
                "   akr.ID_ARUSKAS    AS kodeRincian,\n" +
                "   akr.CREATED_BY      AS createdBy,\n" +
                "   akr.CREATED_DATE    AS createdDate,\n" +
                "   akr.FLAG_GROUP      AS flagGroup,\n" +
                "   akr.FLAG_REKENING   AS flagRekening,\n" +
                "   akr.FLAG_RUMUS      AS flagRumus,\n" +
                "   akr.ID_REKENING     AS idRekening,\n" +
                "   akr.KETERANGAN      AS keterangan,\n" +
                "   akr.KODE_ARUSKAS    AS kodeArusKas,\n" +
                "   akr.SALDO_AWALTAHUN AS saldoAwalTahun,\n" +
                "   akr.STATUS_AKTIF    AS statusAktif,\n" +
                "   akr.UPDATED_BY      AS updatedBy,\n" +
                "   akr.UPDATED_DATE    AS updatedDate \n" +
                "FROM ACC_ARUSKAS_RINCIAN akr\n" +
                "WHERE akr.KODE_ARUSKAS = :kodeArusKas\n";

        OrderingByColumns columns = new OrderingByColumns("akr.ID_ARUSKAS", "akr.ID_REKENING");
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeArusKas", id);
        DatatablesByArusKasIdQueryComparator queryComparator = new DatatablesByArusKasIdQueryComparator(String.format(query, columns.orderBy(params.getColDir(), params.getColOrder())), map);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no", params.getLength());
//        log.info("{}", finalQuery);

        List<ArusKasRincian> list = this.namedParameterJdbcTemplate.query(finalQuery, map, new RowMapper<ArusKasRincian>() {
            @Override
            public ArusKasRincian mapRow(ResultSet resultSet, int i) throws SQLException {
                ArusKasRincian value = new ArusKasRincian();
                value.setKodeRincian(resultSet.getString("kodeRincian"));
                value.setKodeArusKas(resultSet.getString("kodeArusKas"));
                value.setIdRekening(resultSet.getString("idRekening"));
                value.setKeterangan(resultSet.getString("keterangan"));
                value.setSaldoAwalTahun(resultSet.getBigDecimal("saldoAwalTahun"));
                value.setFlagRumus(resultSet.getString("flagRumus"));
                value.setFlagGroup(resultSet.getString("flagGroup"));
                value.setFlagRekening(resultSet.getString("flagRekening"));
                value.setStatusAktif(resultSet.getString("statusAktif"));
                value.setCreatedBy(resultSet.getString("createdBy"));
                value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                value.setUpdatedBy(resultSet.getString("updatedBy"));
                value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));
                return value;
            }
        });
        log.info("{}", list);

        return list;
    }

    public Long findForDataTableByArusKasId(String search, String id) {
        String baseQuery = "select count(*) as value_row from ACC_ARUSKAS_RINCIAN akr where akr.KODE_ARUSKAS = :id\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        DatatablesByArusKasIdQueryComparator queryComparator = new DatatablesByArusKasIdQueryComparator(baseQuery, map);
//        StringBuilder stringBuilder = queryComparator.getQuery(value);
        StringBuilder stringBuilder = queryComparator.getQuerySearch(search);
        map = queryComparator.getParameters();

        Long row = this.namedParameterJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
    }

    private class DatatablesByArusKasIdQueryComparator implements QueryComparator<ArusKasRincian> {

        private StringBuilder builder;
        private MapSqlParameterSource map;

        public DatatablesByArusKasIdQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.map = parameterSource;
        }

        @Override
        public StringBuilder getQuery(ArusKasRincian params) {
            return null;
        }

        public StringBuilder getQuerySearch(String value) {
            if(!value.isEmpty()) {
                builder.append("and (lower(akr.ID_ARUSKAS) like '%").append(value).append("%'\n")
                        .append("or lower(akr.ID_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(akr.SALDO_AWALTAHUN) like '%").append(value).append("%'\n")
                        .append("or lower(akr.KETERANGAN) like '%").append(value).append("%'\n")
                        .append("or lower(akr.FLAG_REKENING) like '%").append(value).append("%'\n")
                        .append("or lower(akr.FLAG_RUMUS) like '%").append(value).append("%'\n")
                        .append("or lower(akr.FLAG_GROUP) like '%").append(value).append("%')\n");
            }
            return builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return map;
        }
    }

    public Integer getIdArusKasRincian(String kodeArusKas) {
        String query = "SELECT\n" +
                "   min_a - 1 + LEVEL\n" +
                "FROM(\n" +
                "       SELECT\n" +
                "           min(TO_NUMBER(aar.ID_ARUSKAS))  min_a,\n" +
                "           max(TO_NUMBER(aar.ID_ARUSKAS))  max_a\n" +
                "       FROM ACC_ARUSKAS_RINCIAN aar \n" +
                "       WHERE aar.KODE_ARUSKAS = :kodeArusKas\n" +
                "   )\n" +
                "CONNECT BY LEVEL <= max_a - min_a + 1\n" +
                "MINUS \n" +
                "SELECT \n" +
                "   TO_NUMBER(aar2.ID_ARUSKAS) \n" +
                "FROM ACC_ARUSKAS_RINCIAN aar2 \n" +
                "WHERE aar2.KODE_ARUSKAS = :kodeArusKas";

        String query2 = "SELECT \n" +
                "   aar.ID_ARUSKAS  \n" +
                "FROM ACC_ARUSKAS_RINCIAN aar \n" +
                "WHERE aar.KODE_ARUSKAS = :kodeArusKas\n" +
                "ORDER BY aar.ID_ARUSKAS desc";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeArusKas", kodeArusKas);

        List<Integer> data1 = this.namedParameterJdbcTemplate.queryForList(query, map, Integer.class);
        if(data1.isEmpty()) {
            List<Integer> data = this.namedParameterJdbcTemplate.queryForList(query2, map, Integer.class);
            int id = 1;
            if(CollectionUtils.isNotEmpty(data)) {
                id = data.get(0) + 1;
            }
            return id;
        } else {
            return data1.get(0);
        }
    }

    public Optional<ArusKasRincian> idChecker(String kodeRincian, String kodeArusKas) {
        String query = "SELECT \n" +
                "    akr.ID_ARUSKAS     AS kodeRincian\n" +
                "FROM ACC_ARUSKAS_RINCIAN akr \n" +
                "WHERE akr.ID_ARUSKAS = :kodeRincian\n" +
                "AND akr.KODE_ARUSKAS = :kodeArusKas\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeRincian", kodeRincian);
        map.addValue("kodeArusKas", kodeArusKas);

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<ArusKasRincian>>() {
                @Override
                public Optional<ArusKasRincian> mapRow(ResultSet resultSet, int i) throws SQLException {
                    ArusKasRincian value = new ArusKasRincian();
                    value.setKodeRincian(resultSet.getString("kodeRincian"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public DataTablesOutput<Rekening> rekeningDataTablesOutput(DataTablesInput input) {
        return rekeningRepository.findAll(input);
    }

    public BigDecimal sumArusKasRincian(String kodeTahunBuku, String kodePeriode, String kodeDRI, String tglSaldo, String flagGroup, String kodeArusKas) {
        String query = "SELECT \n" +
                "   SUM(\n" +
                "       (\n" +
                "           SELECT \n" +
                "               (\n" +
                "                   CASE aar.FLAG_RUMUS \n" +
                "                       WHEN 'A' THEN SUM(COALESCE(as2.SALDO_AWAL, 0))\n" +
                "                       WHEN 'D' THEN SUM(COALESCE(as2.SALDO_DEBET, 0))\n" +
                "                       WHEN 'K' THEN SUM(COALESCE(as2.SALDO_KREDIT, 0))\n" +
                "                       WHEN 'H' THEN SUM(COALESCE(as2.SALDO_AKHIR, 0))\n" +
                "                       WHEN 'S' THEN aar.SALDO_AWALTAHUN\n" +
                "                   END\n" +
                "               )\n" +
                "           FROM ACC_SALDO as2 \n" +
                "           WHERE as2.ID_REKENING = aar.ID_REKENING \n" +
                "           AND as2.KODE_THNBUKU = :kodeTahunBuku\n" +
                "           AND as2.KODE_PERIODE = :kodePeriode\n" +
                "           AND as2.KODE_DRI = :kodeDRI\n" +
                "           AND TO_CHAR(as2.TGL_SALDO, 'yyyy-MM-dd') = :tglSaldo\n" +
                "       )\n" +
                "   )  AS penambahan\n" +
                "FROM ACC_ARUSKAS_RINCIAN aar \n" +
                "WHERE aar.FLAG_GROUP = :flagGroup\n" +
                "AND aar.KODE_ARUSKAS = :kodeArusKas\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeDRI", kodeDRI);
        map.addValue("tglSaldo", tglSaldo);
        map.addValue("flagGroup", flagGroup);
        map.addValue("kodeArusKas", kodeArusKas);

        BigDecimal res = this.namedParameterJdbcTemplate.queryForObject(query, map, BigDecimal.class);
        if (res == null) {
            return BigDecimal.ZERO;
        } else {
            return res;
        }
    }

    public List<ArusKasRincian> listAllSortByKodeArusKasAndIdArusKas() {
        return arusKasRincianRepository.listAllSortByKodeArusKasAndIdArusKas();
    }

    public void copyArusKasRincianToArusKasBulanan(String kodeTahunBuku, String kodePeriode, String kodeDRI, Timestamp tglSaldo, String createdBy) {
        String query = "INSERT INTO \n" +
                "   ACC_ARUSKAS_BULANAN (TANGGAL, KODE_ARUSKAS, ID_ARUSKAS, KODE_THNBUKU, KODE_PERIODE,\n" +
                "                                 ID_REKENING, KODE_REKENING, NAMA_REKENING, KODE_DRI, FLAG_GROUP, SALDO, CREATED_BY,\n" +
                "                                 CREATED_DATE, SALDO_AWALTAHUN, KETERANGAN) \n" +
                "SELECT \n" +
                "   :tglSaldo,\n" +
                "   aar.KODE_ARUSKAS,\n" +
                "   aar.ID_ARUSKAS,\n" +
                "   :kodeTahunBuku,\n" +
                "   :kodePeriode,\n" +
                "   aar.ID_REKENING,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           ar.KODE_REKENING \n" +
                "       FROM ACC_REKENING ar \n" +
                "       WHERE ar.ID_REKENING = aar.ID_REKENING \n" +
                "   ),\n" +
                "   (\n" +
                "       SELECT \n" +
                "           ar.NAMA_REKENING \n" +
                "       FROM ACC_REKENING ar \n" +
                "       WHERE ar.ID_REKENING = aar.ID_REKENING \n" +
                "   ),\n" +
                "   :kodeDRI,\n" +
                "   aar.FLAG_GROUP,\n" +
                "   (\n" +
                "       SELECT \n" +
                "           (\n" +
                "               CASE aar.FLAG_RUMUS \n" +
                "                   WHEN 'A' THEN SUM(COALESCE(as2.SALDO_AWAL, 0))\n" +
                "                   WHEN 'D' THEN SUM(COALESCE(as2.SALDO_DEBET, 0))\n" +
                "                   WHEN 'K' THEN SUM(COALESCE(as2.SALDO_KREDIT, 0))\n" +
                "                   WHEN 'H' THEN SUM(COALESCE(as2.SALDO_AKHIR, 0))\n" +
                "                   WHEN 'S' THEN aar.SALDO_AWALTAHUN\n" +
                "               END\n" +
                "           )\n" +
                "       FROM ACC_SALDO as2 \n" +
                "       WHERE as2.KODE_REKENING like ar.KODE_REKENING||'%s'\n" +
                "       AND as2.KODE_THNBUKU = :kodeTahunBuku\n" +
                "       AND as2.KODE_PERIODE = :kodePeriode\n" +
                "       AND as2.KODE_DRI = :kodeDRI\n" +
                "       AND TO_CHAR(as2.TGL_SALDO, 'yyyy-MM-dd') = TO_CHAR(:tglSaldo, 'yyyy-MM-dd')\n" +
                "   ) - (\n" +
                "                   CASE aar.FLAG_RUMUS \n" +
                "                       WHEN 'D' THEN COALESCE((SELECT sum(awj.JUMLAH_DEBIT) FROM ACC_WARKAT aw JOIN ACC_WARKAT_JURNAL awj ON awj.NO_WARKAT = aw.NO_WARKAT WHERE aw.ARUSKAS = 0 AND awj.ID_REKENING = ar.ID_REKENING), 0)\n" +
                "                       WHEN 'K' THEN COALESCE((SELECT sum(awj.JUMLAH_KREDIT) FROM ACC_WARKAT aw JOIN ACC_WARKAT_JURNAL awj ON awj.NO_WARKAT = aw.NO_WARKAT WHERE aw.ARUSKAS = 0 AND awj.ID_REKENING = ar.ID_REKENING), 0)\n" +
                "                       ELSE 0\n" +
                "                   END\n" +
                "        ),\n" +
                "   :createdBy,\n" +
                "   CURRENT_DATE,\n" +
                "   aar.SALDO_AWALTAHUN,\n" +
                "   aar.KETERANGAN \n" +
                "FROM ACC_ARUSKAS_RINCIAN aar\n" +
                "JOIN ACC_REKENING ar on ar.ID_REKENING = aar.ID_REKENING\n" +
                "WHERE 1 = 1\n" +
                "ORDER BY cast(aar.KODE_ARUSKAS AS integer) ASC, cast(aar.ID_ARUSKAS AS integer) ASC\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeDRI", kodeDRI);
        map.addValue("tglSaldo", tglSaldo);
        map.addValue("createdBy", createdBy);

        this.namedParameterJdbcTemplate.update(String.format(query, '%'), map);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteArusKasBulananByTahunBukuAndPeriodeAndDRI(String kodeTahunBuku, String kodePeriode, String kodeDRI) {
        String query = "delete from ACC_ARUSKAS_BULANAN where KODE_THNBUKU = :kodeTahunBuku and KODE_PERIODE = :kodePeriode and KODE_DRI = :kodeDRI\n";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeDRI", kodeDRI);

        this.namedParameterJdbcTemplate.update(query, map);
    }
}

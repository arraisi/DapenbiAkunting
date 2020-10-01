package id.co.dapenbi.accounting.service.impl.parameter;

import id.co.dapenbi.accounting.dto.transaksi.BukaSistemDto;
import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.BukaSistem;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PengaturanSistemService {

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PengaturanSistem save(PengaturanSistem pengaturanSistem) {
//        pengaturanSistem.setIdParameter(UUID.randomUUID().toString());
        Optional<PengaturanSistem> value = idChecker();
        Long lastId = value.isPresent() ? value.get().getIdParameter() : 0;
        pengaturanSistem.setIdParameter(lastId + 1);
        pengaturanSistem.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        pengaturanSistem.setStatusAktif("1");
        pengaturanSistemRepository.resetStatusAktif();
        pengaturanSistem = pengaturanSistemRepository.save(pengaturanSistem);

        return pengaturanSistem;
    }

    public Optional<PengaturanSistem> findByUsername(String username) {
        String query = "SELECT\n" +
                "   mp.ID_PARAMETER          AS id,\n" +
                "   mp.KODE_THNBUKU          AS thnBuku,\n" +
                "   mp.KODE_PERIODE          AS periode,\n" +
                "   mp.DIRUT                 AS dirut,\n" +
                "   mp.DIV                   AS div,\n" +
                "   mp.KDIV                  AS kdiv,\n" +
                "   mp.KS                    AS ks,\n" +
                "   mp.JAM_TUTUP             AS jamTutup,\n" +
                "   mp.NO_PENGANTAR_WARKAT   AS noWarkat,\n" +
                "   mp.TGL_TRANSAKSI         AS tglTransaksi,\n" +
                "   mp.STATUS_AKTIF          AS statusAktif,\n" +
                "   mp.CREATED_BY            AS createdBy,\n" +
                "   mp.CREATED_DATE          AS createdDate,\n" +
                "   mp.UPDATED_BY            AS updatedBy,\n" +
                "   mp.UPDATED_DATE          AS updatedDate\n" +
                "FROM ACC_PARAMETER mp \n" +
                "WHERE mp.CREATED_BY = :username\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("username", username);

        return jdbcTemplate.queryForObject(query, map, new RowMapper<Optional<PengaturanSistem>>() {
            @Override
            public Optional<PengaturanSistem> mapRow(ResultSet resultSet, int i) throws SQLException {
                PengaturanSistem value = new PengaturanSistem();
                value.setIdParameter(resultSet.getLong("id"));
                value.setKodeTahunBuku(resultSet.getString("thnBuku"));
                value.setKodePeriode(resultSet.getString("periode"));
                value.setDirut(resultSet.getString("dirut"));
                value.setDiv(resultSet.getString("div"));
                value.setKdiv(resultSet.getString("kdiv"));
                value.setKs(resultSet.getString("ks"));
                value.setJamTutup(resultSet.getString("jamTutup"));
                value.setNoPengantarWarkat(resultSet.getString("noWarkat"));
                value.setTglTransaksi(resultSet.getTimestamp("tglTransaksi"));
                value.setStatusAktif(resultSet.getString("statusAktif"));
                value.setCreatedBy(resultSet.getString("createdBy"));
                value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                value.setUpdatedBy(resultSet.getString("updatedBy"));
                value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));

                return Optional.of(value);
            }
        });
    }

    public Optional<PengaturanSistemDTO> findByCreatedDate() {
        String query = "SELECT \n" +
                "   *\n" +
                "FROM (\n" +
                "   SELECT \n" +
                "       ROW_NUMBER() over (order by ap.CREATED_DATE DESC) as NO,\n" +
                "       ap.ID_PARAMETER          AS id,\n" +
                "       ap.KODE_THNBUKU          AS thnBuku,\n" +
                "       ap.KODE_PERIODE          AS periode,\n" +
                "       ap.DIRUT                 AS dirut,\n" +
                "       ap.DIV                   AS div,\n" +
                "       ap.KDIV                  AS kdiv,\n" +
                "       ap.KS                    AS ks,\n" +
                "       ap.JAM_TUTUP             AS jamTutup,\n" +
                "       ap.NO_PENGANTAR_WARKAT   AS noWarkat,\n" +
                "       ap.TGL_TRANSAKSI         AS tglTransaksi,\n" +
                "       ap.STATUS_AKTIF          AS statusAktif,\n" +
                "       ap.STATUS_OPEN           AS statusOpen,\n" +
                "       ap.CREATED_BY            AS createdBy,\n" +
                "       ap.CREATED_DATE          AS createdDate,\n" +
                "       ap.UPDATED_BY            AS updatedBy,\n" +
                "       ap.UPDATED_DATE          AS updatedDate\n" +
                "   FROM ACC_PARAMETER ap \n" +
                ")WHERE no BETWEEN 1 AND 1";

        try {
            return jdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Optional<PengaturanSistemDTO>>() {
                @Override
                public Optional<PengaturanSistemDTO> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(resultSet.getTimestamp("tglTransaksi").getTime());

                    Optional<TahunBuku> tahunBuku = tahunBukuService.findByTahun(calendar.get(Calendar.YEAR));
                    Optional<Periode> periode = periodeService.findById(resultSet.getString("periode"));
                    PengaturanSistemDTO value = new PengaturanSistemDTO();
                    value.setIdParameter(resultSet.getLong("id"));
                    value.setKodeTahunBuku(tahunBuku.get());
                    value.setKodePeriode(periode.get());
                    value.setDirut(resultSet.getString("dirut"));
                    value.setDiv(resultSet.getString("div"));
                    value.setKdiv(resultSet.getString("kdiv"));
                    value.setKs(resultSet.getString("ks"));
                    value.setJamTutup(resultSet.getString("jamTutup"));
                    value.setNoPengantarWarkat(resultSet.getString("noWarkat"));
                    value.setTglTransaksi(resultSet.getTimestamp("tglTransaksi"));
                    value.setStatusAktif(resultSet.getString("statusAktif"));
                    value.setStatusOpen(resultSet.getString("statusOpen"));
                    value.setCreatedBy(resultSet.getString("createdBy"));
                    value.setCreatedDate(resultSet.getTimestamp("createdDate"));
                    value.setUpdatedBy(resultSet.getString("updatedBy"));
                    value.setUpdatedDate(resultSet.getTimestamp("updatedDate"));

                    log.info("{}", value);
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PengaturanSistem update(PengaturanSistem pengaturanSistem) {
        pengaturanSistem.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        pengaturanSistem = pengaturanSistemRepository.save(pengaturanSistem);

        return pengaturanSistem;
    }

    public Optional<PengaturanSistem> idChecker() {
        String query = "SELECT \n" +
                "   *\n" +
                "FROM (\n" +
                "   SELECT \n" +
                "       ROW_NUMBER() over (order by ap.CREATED_DATE DESC) as NO,\n" +
                "       ap.ID_PARAMETER     AS idParameter\n" +
                "   FROM ACC_PARAMETER ap \n" +
                ")WHERE no BETWEEN 1 AND 1\n";

        try {
            return this.jdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Optional<PengaturanSistem>>() {
                @Override
                public Optional<PengaturanSistem> mapRow(ResultSet resultSet, int i) throws SQLException {
                    PengaturanSistem value = new PengaturanSistem();
                    value.setIdParameter(resultSet.getLong("idParameter"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public Optional<PengaturanSistem> findByStatusAktif() {
        return pengaturanSistemRepository.findByStatusAktif();
    }

    public PengaturanSistemDTO findDTOByStatusAktif() throws Exception {
        String query = "SELECT P.ID_PARAMETER,\n" +
                "       P.KODE_THNBUKU,\n" +
                "       T.NAMA_THNBUKU,\n" +
                "       T.TAHUN,\n" +
                "       P.KODE_PERIODE,\n" +
                "       PR.NAMA_PERIODE,\n" +
                "       P.DIRUT,\n" +
                "       P.DIV,\n" +
                "       P.KDIV,\n" +
                "       P.KS,\n" +
                "       P.JAM_TUTUP,\n" +
                "       P.NO_PENGANTAR_WARKAT,\n" +
                "       P.TGL_TRANSAKSI,\n" +
                "       P.STATUS_AKTIF,\n" +
                "       P.STATUS_OPEN,\n" +
                "       P.CREATED_BY,\n" +
                "       P.CREATED_DATE,\n" +
                "       P.UPDATED_BY,\n" +
                "       P.UPDATED_DATE\n" +
                "FROM ACC_PARAMETER P\n" +
                "         LEFT JOIN ACC_THNBUKU T on P.KODE_THNBUKU = T.KODE_THNBUKU\n" +
                "         LEFT JOIN ACC_PERIODE PR on PR.KODE_PERIODE = P.KODE_PERIODE\n" +
                "WHERE P.STATUS_AKTIF = 1 \n";

        MapSqlParameterSource map = new MapSqlParameterSource();

        return jdbcTemplate.queryForObject(query, map, new RowMapper<PengaturanSistemDTO>() {
            @Override
            public PengaturanSistemDTO mapRow(ResultSet resultSet, int i) throws SQLException {
                PengaturanSistemDTO value = new PengaturanSistemDTO();
                TahunBuku tahunBuku = new TahunBuku();
                tahunBuku.setKodeTahunBuku(resultSet.getString("KODE_THNBUKU"));
                tahunBuku.setNamaTahunBuku(resultSet.getString("NAMA_THNBUKU"));
                tahunBuku.setTahun(resultSet.getString("TAHUN"));
                value.setKodeTahunBuku(tahunBuku);
                Periode periode = new Periode();
                periode.setKodePeriode(resultSet.getString("KODE_PERIODE"));
                periode.setNamaPeriode(resultSet.getString("NAMA_PERIODE"));
                value.setKodePeriode(periode);

                value.setIdParameter(resultSet.getLong("ID_PARAMETER"));
                value.setDirut(resultSet.getString("DIRUT"));
                value.setDiv(resultSet.getString("DIV"));
                value.setKdiv(resultSet.getString("KDIV"));
                value.setKs(resultSet.getString("KS"));
                value.setJamTutup(resultSet.getString("JAM_TUTUP"));
                value.setNoPengantarWarkat(resultSet.getString("NO_PENGANTAR_WARKAT"));
                value.setTglTransaksi(resultSet.getTimestamp("TGL_TRANSAKSI"));
                value.setStatusOpen(resultSet.getString("STATUS_OPEN"));
                value.setStatusAktif(resultSet.getString("STATUS_AKTIF"));
                value.setCreatedBy(resultSet.getString("CREATED_BY"));
                value.setCreatedDate(resultSet.getTimestamp("CREATED_DATE"));
                value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
                value.setUpdatedDate(resultSet.getTimestamp("UPDATED_DATE"));

                return value;
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void setStatusOpen() {
        pengaturanSistemRepository.setStatusOpen();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void setTahunBukuAndPeriode(String kodeTahunBuku, String kodePeriode) {
        pengaturanSistemRepository.setTahunBukuAndPeriode(kodeTahunBuku, kodePeriode);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void setTanggalTransaksi(String tglTransaksi) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp ts = new Timestamp((df.parse(tglTransaksi)).getTime());
        pengaturanSistemRepository.setTanggalTransaksi(ts);
    }

    public Optional<String> findLatestKodeDRI() {
        String query = "SELECT\n" +
                "   COALESCE(MAX(KODE_DRI), '1') AS KODE_DRI\n" +
                "FROM (\n" +
                "       SELECT \n" +
                "            KODE_DRI \n" +
                "       FROM ACC_SALDO \n" +
                "       WHERE TGL_SALDO = (SELECT TGL_TRANSAKSI FROM ACC_PARAMETER)\n" +
                "       GROUP BY KODE_DRI\n" +
                "   ) AT";

        try {
            return jdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Optional<String>>() {
                @Override
                public Optional<String> mapRow(ResultSet resultSet, int i) throws SQLException {
                    return Optional.of(resultSet.getString("KODE_DRI"));
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }
}

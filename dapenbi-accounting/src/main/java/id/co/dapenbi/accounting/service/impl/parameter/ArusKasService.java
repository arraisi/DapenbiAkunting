package id.co.dapenbi.accounting.service.impl.parameter;

import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.laporan.LaporanDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.parameter.ArusKas;
import id.co.dapenbi.accounting.repository.parameter.ArusKasRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ArusKasService {

    @Autowired
    private ArusKasRepository arusKasRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ArusKas save(ArusKas arusKas) {
        arusKas.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        arusKas = arusKasRepository.save(arusKas);

        return arusKas;
    }

    public Optional<ArusKas> findById(String id) {
        return arusKasRepository.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ArusKas update(ArusKas arusKas) {
        arusKas.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        arusKasRepository.update(
                arusKas.getKodeArusKas(),
                arusKas.getKeterangan(),
                arusKas.getStatusAktif(),
                arusKas.getUpdatedBy(),
                arusKas.getUpdatedDate(),
                arusKas.getArusKasAktivitas()
        );

        return arusKas;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteById(String id) {
        arusKasRepository.deleteById(id);
    }

    public DataTablesOutput<ArusKas> findForDataTable(DataTablesInput input) {
        return arusKasRepository.findAll(input);
    }

    public Optional<ArusKas> idChecker(String kodeArusKas) {
        String query = "SELECT \n" +
                "    ak.KODE_ARUSKAS     AS kodeArusKas\n" +
                "FROM ACC_ARUSKAS ak \n" +
                "WHERE ak.KODE_ARUSKAS = :kodeArusKas\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodeArusKas", kodeArusKas);

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<ArusKas>>() {
                @Override
                public Optional<ArusKas> mapRow(ResultSet resultSet, int i) throws SQLException {
                    ArusKas value = new ArusKas();
                    value.setKodeArusKas(resultSet.getString("kodeArusKas"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    public List<MSTLookUp> findJenisAktivitas() throws SQLException {
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
                "WHERE " +
                "JENIS_LOOKUP = 'ARUSKAS_AKTIVITAS' AND STATUS_DATA = '1' ORDER BY KETERANGAN ASC\n";
        return namedParameterJdbcTemplate.query(query, new MapSqlParameterSource(), new RowMapper<MSTLookUp>() {
            @Override
            public MSTLookUp mapRow(ResultSet resultSet, int i) throws SQLException {
                MSTLookUp mstLookUp = new MSTLookUp();
                mstLookUp.setKodeLookUp(resultSet.getString("KODE_LOOKUP"));
                mstLookUp.setJenisLookUp(resultSet.getString("JENIS_LOOKUP"));
                mstLookUp.setNamaLookUp(resultSet.getString("NAMA_LOOKUP"));
                mstLookUp.setKeterangan(resultSet.getString("KETERANGAN"));
                mstLookUp.setStatusData(resultSet.getString("STATUS_DATA"));
                return mstLookUp;
            }
        });
    }

    public int saveArusKasRincianToBulanan(String kodeTahunBuku, String kodePeriode, String kodeDRI) throws SQLException {
        String query = "INSERT INTO ACC_ARUSKAS_BULANAN (TANGGAL, KODE_ARUSKAS, ID_ARUSKAS, KODE_THNBUKU, KODE_PERIODE,\n" +
                "                                 ID_REKENING, KODE_REKENING, NAMA_REKENING, KODE_DRI, FLAG_GROUP, SALDO, CREATED_BY,\n" +
                "                                 CREATED_DATE, SALDO_AWALTAHUN, KETERANGAN)\n" +
                "SELECT CURRENT_DATE                    AS TANGGAL,\n" +
                "       AK.KODE_ARUSKAS,\n" +
                "       AKR.ID_ARUSKAS,\n" +
                "       :KODE_THNBUKU                   AS KODE_THNBUKU,\n" +
                "       :KODE_PERIODE                   AS KODE_PERIODE,\n" +
                "       AKR.ID_REKENING,\n" +
                "       COALESCE(AR.KODE_REKENING, '-') AS KODE_REKENING,\n" +
                "       COALESCE(AR.NAMA_REKENING, '-') AS NAMA_REKENING,\n" +
                "       :KODE_DRI                       AS KODE_PERIODE,\n" +
                "       AKR.FLAG_GROUP                  AS FLAG_GROUP,\n" +
                "       (CASE AKR.FLAG_RUMUS\n" +
                "            WHEN 'D' THEN (SELECT DISTINCT COALESCE(SALDO_DEBET, 0)\n" +
                "                           FROM ACC_SALDO\n" +
                "                           WHERE ID_REKENING = AKR.ID_REKENING\n" +
                "                             AND KODE_THNBUKU = :KODE_THNBUKU\n" +
                "                             AND KODE_PERIODE = :KODE_PERIODE)\n" +
                "            WHEN 'K' THEN (SELECT DISTINCT COALESCE(SALDO_KREDIT, 0)\n" +
                "                           FROM ACC_SALDO\n" +
                "                           WHERE ID_REKENING = AKR.ID_REKENING\n" +
                "                             AND KODE_THNBUKU = :KODE_THNBUKU\n" +
                "                             AND KODE_PERIODE = :KODE_PERIODE)\n" +
                "            WHEN 'A' THEN (SELECT DISTINCT COALESCE(SALDO_AWAL, 0)\n" +
                "                           FROM ACC_SALDO\n" +
                "                           WHERE ID_REKENING = AKR.ID_REKENING\n" +
                "                             AND KODE_THNBUKU = :KODE_THNBUKU\n" +
                "                             AND KODE_PERIODE = :KODE_PERIODE)\n" +
                "            WHEN 'H' THEN (SELECT DISTINCT COALESCE(SALDO_AKHIR, 0)\n" +
                "                           FROM ACC_SALDO\n" +
                "                           WHERE ID_REKENING = AKR.ID_REKENING\n" +
                "                             AND KODE_THNBUKU = :KODE_THNBUKU\n" +
                "                             AND KODE_PERIODE = :KODE_PERIODE)\n" +
                "            WHEN 'S' THEN AKR.SALDO_AWALTAHUN\n" +
                "            ELSE 0\n" +
                "           END)                        AS SALDO,\n" +
                "       AKR.CREATED_BY                  AS CREATED_BY,\n" +
                "       AKR.CREATED_DATE                AS CREATED_DATE,\n" +
                "       AKR.SALDO_AWALTAHUN             AS SALDO_AWALTAHUN,\n" +
                "       AKR.KETERANGAN                  AS KETERANGAN\n" +
                "FROM ACC_ARUSKAS AK\n" +
                "         LEFT JOIN ACC_ARUSKAS_RINCIAN AKR ON AKR.KODE_ARUSKAS = AK.KODE_ARUSKAS\n" +
                "         LEFT JOIN ACC_REKENING AR ON AR.ID_REKENING = AKR.ID_REKENING";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("KODE_THNBUKU", kodeTahunBuku);
        params.addValue("KODE_PERIODE", kodePeriode);
        params.addValue("KODE_DRI", kodeDRI);
        return namedParameterJdbcTemplate.update(query, params);
    }
}

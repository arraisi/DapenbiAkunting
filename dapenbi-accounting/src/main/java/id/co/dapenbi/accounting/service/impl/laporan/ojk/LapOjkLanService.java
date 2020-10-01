package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkLan;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRekinv;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkLanRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkLanService {

    @Autowired
    private OjkLanRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkLan> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkLan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkLan> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkLan> ojkLanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkLanIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Persentase Investasi", "persentaseInvestasi"));
        columns.add(new TableColumn("Program Pensiun", "programPensiun"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Gabungan", "gabungan"));
        return columns;
    }

    @Transactional
    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws Exception {
        //language=Oracle
        String finalQuery = "" +
                "MERGE INTO OJK_LAN OJK\n" +
                "USING (SELECT ID_LAPORAN     AS ID_LAPORAN,\n" +
                "              TGL_LAPORAN    AS TGL_LAPORAN,\n" +
                "              URAIAN         AS URAIAN,\n" +
                "              ID_REKENING    AS ID_REKENING,\n" +
                "              KODE_REKENING  AS KODE_REKENING,\n" +
                "              NAMA_REKENING  AS NAMA_REKENING,\n" +
                "              LEVEL_AKUN     AS LEVEL_AKUN,\n" +
                "              SALDO_BERJALAN AS PROGRAM_PENSIUN,\n" +
                "              SALDO_BERJALAN AS GABUNGAN,\n" +
                "              :CREATED_BY    AS CREATED_BY,\n" +
                "              CREATED_DATE   AS CREATED_DATE,\n" +
                "              UPDATED_BY     AS UPDATED_BY,\n" +
                "              UPDATED_DATE   AS UPDATED_DATE,\n" +
                "              KODE_PERIODE   AS KODE_PERIODE,\n" +
                "              KODE_THNBUKU   AS KODE_THNBUKU\n" +
                "       FROM LAP_KEU\n" +
                "       WHERE KODE_THNBUKU = (SELECT KODE_THNBUKU FROM ACC_THNBUKU WHERE TAHUN = :KODE_THNBUKU)\n" +
                "         AND KODE_PERIODE = :KODE_PERIODE\n" +
                "         AND KODE_DRI = '1'\n" +
                "         AND ID_LAPORAN_HDR = 1\n" +
                "       ORDER BY TGL_LAPORAN) LK\n" +
                "ON (OJK.KODE_PERIODE = LK.KODE_PERIODE AND OJK.KODE_THNBUKU = LK.KODE_THNBUKU AND OJK.URAIAN = LK.URAIAN\n" +
                "        AND OJK.ID_LAPORAN = LK.ID_LAPORAN)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET OJK.ID_REKENING     = LK.ID_REKENING,\n" +
                "        OJK.KODE_REKENING   = LK.KODE_REKENING,\n" +
                "        OJK.NAMA_REKENING   = LK.NAMA_REKENING,\n" +
                "        OJK.LEVEL_AKUN      = LK.LEVEL_AKUN,\n" +
                "        OJK.PROGRAM_PENSIUN = LK.PROGRAM_PENSIUN,\n" +
                "        OJK.GABUNGAN        = LK.GABUNGAN,\n" +
                "        OJK.CREATED_BY      = LK.CREATED_BY,\n" +
                "        OJK.CREATED_DATE    = LK.CREATED_DATE,\n" +
                "        OJK.UPDATED_BY      = LK.UPDATED_BY,\n" +
                "        OJK.UPDATED_DATE    = LK.UPDATED_DATE\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, TGL_LAPORAN, URAIAN, ID_REKENING, KODE_REKENING, NAMA_REKENING, LEVEL_AKUN, PROGRAM_PENSIUN, GABUNGAN,\n" +
                "            CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, KODE_PERIODE, KODE_THNBUKU)\n" +
                "    VALUES (LK.ID_LAPORAN, LK.TGL_LAPORAN, LK.URAIAN, LK.ID_REKENING, LK.KODE_REKENING, LK.NAMA_REKENING, LK.LEVEL_AKUN, LK.PROGRAM_PENSIUN, LK.GABUNGAN,\n" +
                "            LK.CREATED_BY, LK.CREATED_DATE, LK.UPDATED_BY, LK.UPDATED_DATE, LK.KODE_PERIODE, LK.KODE_THNBUKU) ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkLan> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkLan> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkLan> findAll() {
        //language=Oracle
        String finalQuery = "" +
                "SELECT ID_LAPORAN,\n" +
                "       TGL_LAPORAN,\n" +
                "       URAIAN,\n" +
                "       ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       LEVEL_AKUN,\n" +
                "       PRESENTASE_INVESTASI,\n" +
                "       PROGRAM_PENSIUN,\n" +
                "       MANFAAT_LAIN,\n" +
                "       GABUNGAN,\n" +
                "       CREATED_BY,\n" +
                "       CREATED_DATE,\n" +
                "       UPDATED_BY,\n" +
                "       UPDATED_DATE,\n" +
                "       KODE_THNBUKU,\n" +
                "       KODE_PERIODE\n" +
                "FROM OJK_LAN\n" +
                "WHERE 1 = 1  \n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            OjkLan value = new OjkLan();
            value.setIdLaporan(resultSet.getInt("ID_LAPORAN"));
            value.setTglLaporan(resultSet.getTimestamp("TGL_LAPORAN"));
            value.setUraian(resultSet.getString("URAIAN"));
            value.setIdRekening(resultSet.getLong("ID_REKENING"));
            value.setKodeRekening(resultSet.getString("KODE_REKENING"));
            value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
            value.setLevelAkun(resultSet.getInt("LEVEL_AKUN"));
            value.setPersentaseInvestasi(resultSet.getBigDecimal("PRESENTASE_INVESTASI"));
            value.setProgramPensiun(resultSet.getBigDecimal("PROGRAM_PENSIUN"));
            value.setManfaatLain(resultSet.getBigDecimal("MANFAAT_LAIN"));
            value.setGabungan(resultSet.getBigDecimal("GABUNGAN"));
            value.setCreatedBy(resultSet.getString("CREATED_BY"));
            value.setCreatedDate(resultSet.getDate("CREATED_DATE"));
            value.setUpdatedBy(resultSet.getString("UPDATED_BY"));
            value.setUpdatedDate(resultSet.getDate("UPDATED_DATE"));
            return value;
        });
    }

    public List<OjkLan> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkLan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

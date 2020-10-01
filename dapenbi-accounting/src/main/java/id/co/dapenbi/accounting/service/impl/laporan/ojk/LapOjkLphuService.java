package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkLphu;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkLphuRepository;
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
public class LapOjkLphuService {

    @Autowired
    private OjkLphuRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkLphu> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkLphu> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLphu> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkLphu> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkLphu> ojkLanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkLanIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Program Pensiun", "programPensiun"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Gabungan", "gabungan"));
        return columns;
    }

    public List<OjkLphu> findAll() {
        return repository.findAll();
    }

    @Transactional
    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws Exception {
        //language=Oracle
        String finalQuery = "" +
                "MERGE INTO OJK_LPHU OJK\n" +
                "USING (SELECT ID_LAPORAN     AS ID_LAPORAN," +
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
                "         AND ID_LAPORAN_HDR = 4\n" +
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
                "            LK.CREATED_BY, LK.CREATED_DATE, LK.UPDATED_BY, LK.UPDATED_DATE, LK.KODE_PERIODE, LK.KODE_THNBUKU)";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkLphu> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkLphu> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkLphu> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkLphu> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLphu> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

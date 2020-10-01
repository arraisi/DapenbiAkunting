package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKup;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKupRepository;
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
public class LapOjkKupService {

    @Autowired
    private OjkKupRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkKup> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKup> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKup> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKup> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKup> ojkKupIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKupIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Nilai", "nilai"));
        return columns;
    }

    public List<OjkKup> findAll() {
        return repository.findAll();
    }

    public List<OjkKup> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkKup> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKup> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }


    @Transactional
    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws Exception {
        //language=Oracle
        String finalQuery = "" +
                "MERGE INTO OJK_KUP OJK\n" +
                "USING (SELECT ID_LAPORAN     AS ID_LAPORAN,\n" +
                "              TGL_LAPORAN    AS TGL_LAPORAN,\n" +
                "              URAIAN         AS URAIAN,\n" +
                "              ID_REKENING    AS ID_REKENING,\n" +
                "              KODE_REKENING  AS KODE_REKENING,\n" +
                "              NAMA_REKENING  AS NAMA_REKENING,\n" +
                "              LEVEL_AKUN     AS LEVEL_AKUN,\n" +
                "              SALDO_BERJALAN AS NILAI,\n" +
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
                "    AND OJK.ID_LAPORAN = LK.ID_LAPORAN)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET OJK.ID_REKENING   = LK.ID_REKENING,\n" +
                "        OJK.KODE_REKENING = LK.KODE_REKENING,\n" +
                "        OJK.NAMA_REKENING = LK.NAMA_REKENING,\n" +
                "        OJK.LEVEL_AKUN    = LK.LEVEL_AKUN,\n" +
                "        OJK.NILAI         = LK.NILAI,\n" +
                "        OJK.CREATED_BY    = LK.CREATED_BY,\n" +
                "        OJK.CREATED_DATE  = LK.CREATED_DATE,\n" +
                "        OJK.UPDATED_BY    = LK.UPDATED_BY,\n" +
                "        OJK.UPDATED_DATE  = LK.UPDATED_DATE\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, TGL_LAPORAN, URAIAN, ID_REKENING, KODE_REKENING, NAMA_REKENING, LEVEL_AKUN, NILAI,\n" +
                "            CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, KODE_PERIODE, KODE_THNBUKU)\n" +
                "    VALUES (LK.ID_LAPORAN, LK.TGL_LAPORAN, LK.URAIAN, LK.ID_REKENING, LK.KODE_REKENING, LK.NAMA_REKENING, LK.LEVEL_AKUN,\n" +
                "            LK.NILAI, LK.CREATED_BY, LK.CREATED_DATE, LK.UPDATED_BY, LK.UPDATED_DATE, LK.KODE_PERIODE, LK.KODE_THNBUKU)";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkKup> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKup> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

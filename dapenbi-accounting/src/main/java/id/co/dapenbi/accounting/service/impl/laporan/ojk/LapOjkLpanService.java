package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkLpan;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkLpanRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
public class LapOjkLpanService {

    @Autowired
    private OjkLpanRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkLpan> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkLpan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLpan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkLpan> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkLpan> ojkLpanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkLpanIterable),
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

    @Transactional
    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws IncorrectResultSizeDataAccessException {
        String finalQuery = "" +
                "MERGE INTO OJK_LPAN OJK\n" +
                "USING (\n" +
                "   SELECT\n" +
                "       ID_LAPORAN,\n" +
                "       TGL_LAPORAN,\n" +
                "       URAIAN,\n" +
                "       ID_REKENING,\n" +
                "       KODE_REKENING,\n" +
                "       NAMA_REKENING,\n" +
                "       LEVEL_AKUN,\n" +
                "       SALDO_BERJALAN,\n" +
                "       KODE_PERIODE,\n" +
                "       KODE_THNBUKU,\n" +
                "       :CREATED_BY     AS CREATED_BY,\n" +
                "       CURRENT_DATE    AS CREATED_DATE\n" +
                "   FROM LAP_KEU \n" +
                "   WHERE   KODE_THNBUKU = (SELECT KODE_THNBUKU FROM ACC_THNBUKU WHERE TAHUN = :kodeTahunBuku) AND\n" +
                "           KODE_PERIODE = :kodePeriode AND\n" +
                "           ID_LAPORAN_HDR = 2 AND\n" +
                "           KODE_DRI = '1'\n" +
                ") SUMBER\n" +
                "ON (\n" +
                "       SUMBER.URAIAN = OJK.URAIAN AND\n" +
                "       SUMBER.KODE_PERIODE = OJK.KODE_PERIODE AND\n" +
                "       SUMBER.KODE_THNBUKU = OJK.KODE_THNBUKU\n" +
                "   )\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET ID_LAPORAN  = SUMBER.ID_LAPORAN,\n" +
                "        TGL_LAPORAN       = SUMBER.TGL_LAPORAN,\n" +
                "        PROGRAM_PENSIUN = SUMBER.SALDO_BERJALAN,\n" +
                "        GABUNGAN   = SUMBER.SALDO_BERJALAN,\n" +
                "        UPDATED_BY   = SUMBER.CREATED_BY,\n" +
                "        UPDATED_DATE   = SUMBER.CREATED_DATE\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, TGL_LAPORAN, URAIAN, ID_REKENING, KODE_REKENING, NAMA_REKENING,\n" +
                "            LEVEL_AKUN, PROGRAM_PENSIUN, GABUNGAN, KODE_PERIODE, KODE_THNBUKU,\n" +
                "            CREATED_BY, CREATED_DATE)\n" +
                "    values (SUMBER.ID_LAPORAN, SUMBER.TGL_LAPORAN, SUMBER.URAIAN, SUMBER.ID_REKENING,\n" +
                "            SUMBER.KODE_REKENING, SUMBER.NAMA_REKENING, SUMBER.LEVEL_AKUN, SUMBER.SALDO_BERJALAN,\n" +
                "            SUMBER.SALDO_BERJALAN, SUMBER.KODE_PERIODE, SUMBER.KODE_THNBUKU,\n" +
                "            SUMBER.CREATED_BY, SUMBER.CREATED_DATE)";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("kodePeriode", kodePeriode);
        map.addValue("kodeTahunBuku", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkLpan> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkLpan> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkLpan> findAll() {
        return IterableUtils.toList(repository.findAll());
    }

    public List<OjkLpan> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkLpan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkLpan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode.and(byKodeTahunBuku)));
    }
}

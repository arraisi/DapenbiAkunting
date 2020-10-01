package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPddm;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPill;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRekinv;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPddmRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPddmService {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private OjkPddmRepository ojkPddmRepository;

    public TableLapOjk<OjkPddm> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPddm> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPddm> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPddm> output = ojkPddmRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPddm> ojkLanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkLanIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Rincian Pendapatan Diterima Di Muka", "keterangan"));
        columns.add(new TableColumn("Pihak", "pihak"));
        columns.add(new TableColumn("Nilai", "nilai"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPddm> findAll() {
        return ojkPddmRepository.findAll();
    }

    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws IncorrectResultSizeDataAccessException {
        //language=Oracle
        String finalQuery = "" +
                "MERGE INTO OJK_PDDM ojk\n" +
                "USING (\n" +
                "    SELECT ID_SALDO AS ID_LAPORAN, NAMA_REKENING            AS JENIS_PIUTANG,\n" +
                "           COALESCE(SALDO_AKHIR, 0) AS JUMLAH,\n" +
                "           '-'                      AS MANFAAT_LAIN,\n" +
                "           '-'                      AS KETERANGAN,\n" +
                "           KODE_PERIODE             AS KODE_PERIODE,\n" +
                "           KODE_THNBUKU             AS KODE_THNBUKU,\n" +
                "           :CREATED_BY              AS CREATED_BY,\n" +
                "           CURRENT_DATE             AS CREATED_DATE\n" +
                "    FROM (SELECT *\n" +
                "          FROM ACC_SALDO\n" +
                "          ORDER BY TGL_SALDO DESC) S\n" +
                "    WHERE KODE_THNBUKU = (SELECT KODE_THNBUKU FROM ACC_THNBUKU WHERE TAHUN = :KODE_THNBUKU)\n" +
                "      AND KODE_PERIODE = :KODE_PERIODE\n" +
                "      AND S.KODE_REKENING LIKE '904%'\n" +
                "      AND KODE_DRI = 1\n" +
                "      AND ROWNUM = 1\n" +
                ") PPH\n" +
                "ON (PPH.KODE_PERIODE = OJK.KODE_PERIODE AND PPH.KODE_THNBUKU = OJK.KODE_THNBUKU AND PPH.ID_LAPORAN = OJK.ID_LAPORAN)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET PIHAK        = pph.JENIS_PIUTANG,\n" +
                "        NILAI        = pph.JUMLAH,\n" +
                "        MANFAAT_LAIN = pph.MANFAAT_LAIN,\n" +
                "        KETERANGAN   = pph.KETERANGAN,\n" +
                "        CREATED_BY   = pph.CREATED_BY\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, KODE_PERIODE, KODE_THNBUKU, PIHAK, NILAI, MANFAAT_LAIN, KETERANGAN, CREATED_BY, CREATED_DATE)\n" +
                "    values (PPH.ID_LAPORAN, pph.KODE_PERIODE, pph.KODE_THNBUKU, pph.JENIS_PIUTANG, pph.JUMLAH, pph.MANFAAT_LAIN, pph.KETERANGAN, pph.CREATED_BY, pph.CREATED_DATE) ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkPddm> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPddm> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkPddm> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPddm> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPddm> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPddmRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

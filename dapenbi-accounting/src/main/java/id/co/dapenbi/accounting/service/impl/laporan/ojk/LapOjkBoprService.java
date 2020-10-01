package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkBopr;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkBoprRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LapOjkBoprService {

    @Autowired
    private OjkBoprRepository ojkBoprRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkBopr> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkBopr> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkBopr> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkBopr> output = ojkBoprRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkBopr> ojkBoprIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkBoprIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Jenis Beban", "jenisBeban"));
        columns.add(new TableColumn("Jumlah", "jumlah"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkBopr> findAll() {
        return ojkBoprRepository.findAll();
    }

    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) {
        String finalQuery = "" +
                "MERGE INTO OJK_BOPR ojk\n" +
                "USING (\n" +
                "    SELECT ID_SALDO                 AS ID_LAPORAN,\n" +
                "           TGL_SALDO                AS TGL_LAPORAN,\n" +
                "           NAMA_REKENING            AS JENIS_BEBAN,\n" +
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
                "      AND (\n" +
                "            S.KODE_REKENING LIKE '50201%' OR S.KODE_REKENING LIKE '50202%'\n" +
                "            OR S.KODE_REKENING LIKE '50203%'\n" +
                "            OR S.KODE_REKENING LIKE '50204%'\n" +
                "            OR S.KODE_REKENING LIKE '50205%'\n" +
                "            OR S.KODE_REKENING LIKE '50206%'\n" +
                "        )\n" +
                "      AND KODE_DRI = 1\n" +
                "      AND ROWNUM = 1\n" +
                ") PPH\n" +
                "ON (PPH.KODE_PERIODE = OJK.KODE_PERIODE AND PPH.KODE_THNBUKU = OJK.KODE_THNBUKU AND PPH.ID_LAPORAN = OJK.ID_LAPORAN)\n" +
                "WHEN MATCHED THEN\n" +
                "    UPDATE\n" +
                "    SET JENIS_BEBAN  = pph.JENIS_BEBAN,\n" +
                "        JUMLAH       = pph.JUMLAH,\n" +
                "        MANFAAT_LAIN = pph.MANFAAT_LAIN,\n" +
                "        KETERANGAN   = pph.KETERANGAN,\n" +
                "        CREATED_BY   = pph.CREATED_BY,\n" +
                "        CREATED_DATE = pph.CREATED_DATE\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, TGL_LAPORAN, JENIS_BEBAN, JUMLAH, MANFAAT_LAIN, KETERANGAN, CREATED_BY,\n" +
                "            CREATED_DATE, KODE_PERIODE, KODE_THNBUKU)\n" +
                "    values (PPH.ID_LAPORAN, pph.TGL_LAPORAN, pph.JENIS_BEBAN, pph.JUMLAH, pph.MANFAAT_LAIN,\n" +
                "            pph.KETERANGAN, pph.CREATED_BY, pph.CREATED_DATE, PPH.KODE_PERIODE, PPH.KODE_THNBUKU) ";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    public List<OjkBopr> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkBopr> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkBopr> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkBoprRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkBopr> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkBopr> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRekinv;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkRekinvRepository;
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
public class LapOjkRekinvService {

    @Autowired
    private OjkRekinvRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TableLapOjk<OjkRekinv> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkRekinv> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRekinv> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkRekinv> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkRekinv> ojkLpanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkLpanIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Jan", "totJan"));
        columns.add(new TableColumn("Feb", "totFeb"));
        columns.add(new TableColumn("Mar", "totMar"));
        columns.add(new TableColumn("Apr", "totApr"));
        columns.add(new TableColumn("Mei", "totMay"));
        columns.add(new TableColumn("Jun", "totJun"));
        columns.add(new TableColumn("Jul", "totJul"));
        columns.add(new TableColumn("Agu", "totAug"));
        columns.add(new TableColumn("Sep", "totSep"));
        columns.add(new TableColumn("Okt", "totOct"));
        columns.add(new TableColumn("Nov", "totNov"));
        columns.add(new TableColumn("Des", "totDes"));
        return columns;
    }

    public List<OjkRekinv> findAll() {
        return repository.findAll();
    }

    public List<OjkRekinv> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkRekinv> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRekinv> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    @Transactional
    public int proses(String kodePeriode, String kodeTahunBuku, Principal principal) throws Exception {
        //language=Oracle
        String finalQuery = "" +
                "MERGE INTO OJK_REKINV OJK\n" +
                "USING (SELECT ID_LAPORAN                                               AS ID_LAPORAN,\n" +
                "              TGL_LAPORAN                                              AS TGL_LAPORAN,\n" +
                "              URAIAN                                                   AS URAIAN,\n" +
                "              ID_REKENING                                              AS ID_REKENING,\n" +
                "              KODE_REKENING                                            AS KODE_REKENING,\n" +
                "              NAMA_REKENING                                            AS NAMA_REKENING,\n" +
                "              LEVEL_AKUN                                               AS LEVEL_AKUN,\n" +
                "              (CASE :KODE_PERIODE WHEN '01' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_JAN,\n" +
                "              (CASE :KODE_PERIODE WHEN '02' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_FEB,\n" +
                "              (CASE :KODE_PERIODE WHEN '03' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_MAR,\n" +
                "              (CASE :KODE_PERIODE WHEN '04' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_APR,\n" +
                "              (CASE :KODE_PERIODE WHEN '05' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_MAY,\n" +
                "              (CASE :KODE_PERIODE WHEN '06' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_JUN,\n" +
                "              (CASE :KODE_PERIODE WHEN '07' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_JUL,\n" +
                "              (CASE :KODE_PERIODE WHEN '08' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_AUG,\n" +
                "              (CASE :KODE_PERIODE WHEN '09' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_SEP,\n" +
                "              (CASE :KODE_PERIODE WHEN '10' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_OCT,\n" +
                "              (CASE :KODE_PERIODE WHEN '11' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_NOV,\n" +
                "              (CASE :KODE_PERIODE WHEN '12' THEN SALDO_BERJALAN ELSE 0 END) AS TOTAL_DES,\n" +
                "              :CREATED_BY                                              AS CREATED_BY,\n" +
                "              CREATED_DATE                                             AS CREATED_DATE,\n" +
                "              UPDATED_BY                                               AS UPDATED_BY,\n" +
                "              UPDATED_DATE                                             AS UPDATED_DATE,\n" +
                "              KODE_PERIODE                                             AS KODE_PERIODE,\n" +
                "              KODE_THNBUKU                                             AS KODE_THNBUKU\n" +
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
                "        OJK.TOTAL_JAN     = LK.TOTAL_JAN,\n" +
                "        OJK.TOTAL_FEB     = LK.TOTAL_FEB,\n" +
                "        OJK.TOTAL_MAR     = LK.TOTAL_MAR,\n" +
                "        OJK.TOTAL_APR     = LK.TOTAL_APR,\n" +
                "        OJK.TOTAL_MAY     = LK.TOTAL_MAY,\n" +
                "        OJK.TOTAL_JUN     = LK.TOTAL_JUN,\n" +
                "        OJK.TOTAL_JUL     = LK.TOTAL_JUL,\n" +
                "        OJK.TOTAL_AUG     = LK.TOTAL_AUG,\n" +
                "        OJK.TOTAL_SEP     = LK.TOTAL_SEP,\n" +
                "        OJK.TOTAL_OCT     = LK.TOTAL_OCT,\n" +
                "        OJK.TOTAL_NOV     = LK.TOTAL_NOV,\n" +
                "        OJK.TOTAL_DES     = LK.TOTAL_DES,\n" +
                "        OJK.CREATED_BY    = LK.CREATED_BY,\n" +
                "        OJK.CREATED_DATE  = LK.CREATED_DATE,\n" +
                "        OJK.UPDATED_BY    = LK.UPDATED_BY,\n" +
                "        OJK.UPDATED_DATE  = LK.UPDATED_DATE\n" +
                "WHEN NOT MATCHED THEN\n" +
                "    INSERT (ID_LAPORAN, TGL_LAPORAN, URAIAN, ID_REKENING, KODE_REKENING, NAMA_REKENING, LEVEL_AKUN, TOTAL_JAN,\n" +
                "            TOTAL_FEB, TOTAL_MAR, TOTAL_APR, TOTAL_MAY, TOTAL_JUN, TOTAL_JUL, TOTAL_AUG, TOTAL_SEP, TOTAL_OCT,\n" +
                "            TOTAL_NOV, TOTAL_DES, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, KODE_PERIODE, KODE_THNBUKU)\n" +
                "    VALUES (LK.ID_LAPORAN, LK.TGL_LAPORAN, LK.URAIAN, LK.ID_REKENING, LK.KODE_REKENING, LK.NAMA_REKENING, LK.LEVEL_AKUN, \n" +
                "            LK.TOTAL_JAN, LK.TOTAL_FEB, LK.TOTAL_MAR, LK.TOTAL_APR, LK.TOTAL_MAY, LK.TOTAL_JUN, LK.TOTAL_JUL, LK.TOTAL_AUG, \n" +
                "            LK.TOTAL_SEP, LK.TOTAL_OCT, LK.TOTAL_NOV, LK.TOTAL_DES, LK.CREATED_BY, LK.CREATED_DATE, LK.UPDATED_BY, LK.UPDATED_DATE, \n" +
                "            LK.KODE_PERIODE, LK.KODE_THNBUKU)\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("KODE_PERIODE", kodePeriode);
        map.addValue("KODE_THNBUKU", kodeTahunBuku);
        map.addValue("CREATED_BY", principal.getName());
        return namedParameterJdbcTemplate.update(finalQuery, map);
    }

    private Specification<OjkRekinv> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkRekinv> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

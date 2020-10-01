package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSukuk;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkSukukRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkSukukService {

    @Autowired
    private OjkSukukRepository repository;

    public TableLapOjk<OjkSukuk> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkSukuk> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSukuk> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkSukuk> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkSukuk> ojkSukukIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkSukukIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Penerbit", "namaPenerbit"));
        columns.add(new TableColumn("Kode Sukuk", "kodeSukuk"));
        columns.add(new TableColumn("Nama Sukuk", "namaSukuk"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
//        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Kupon (%)", "persenKupon"));
        columns.add(new TableColumn("Tanggal Jatuh Tempo", "tglJatpo"));
        columns.add(new TableColumn("Peringkat Awal", "peringkatAwal"));
        columns.add(new TableColumn("Peringkat Akhir", "peringkatAkhir"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehan"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajar"));
        columns.add(new TableColumn("Sektor Ekonomi", "sektorEkonomi"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Metode Pencatatan", "metodePencatatan"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    private Specification<OjkSukuk> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkSukuk> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

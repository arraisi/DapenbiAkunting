package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkMtn;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkMtnRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkMtnService {

    @Autowired
    private OjkMtnRepository repository;

    public TableLapOjk<OjkMtn> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkMtn> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkMtn> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkMtn> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkMtn> ojkMtnIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkMtnIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kode", "kode"));
        columns.add(new TableColumn("Nama Produk", "namaProduk"));
        columns.add(new TableColumn("Nama Penerbit", "namaPenerbit"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Kupon (%)", "persenKupon"));
        columns.add(new TableColumn("Tanggal Jatuh Tempo", "tglJatpo"));
        columns.add(new TableColumn("Peringkat Awal", "peringkatAwal"));
        columns.add(new TableColumn("Peringkat Akhir", "peringkatAkhir"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehan"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajar"));
        columns.add(new TableColumn("Sektor Ekonomi", "sektorEkonomi"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkMtn> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkMtn> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkMtn> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkMtn> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkMtn> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

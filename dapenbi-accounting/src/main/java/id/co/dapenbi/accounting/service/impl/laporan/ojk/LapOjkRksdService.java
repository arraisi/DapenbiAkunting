package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRksd;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkRksdRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkRksdService {

    @Autowired
    private OjkRksdRepository repository;

    public TableLapOjk<OjkRksd> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkRksd> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRksd> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkRksd> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkRksd> ojkRksdIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkRksdIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kode", "kode"));
        columns.add(new TableColumn("Nama Produk", "namaProduk"));
        columns.add(new TableColumn("Jenis Reksadana", "jenisReksadana"));
        columns.add(new TableColumn("Manajer Investasi", "manajerInvestasi"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
        columns.add(new TableColumn("Jumlah Unit", "jumlahUnit"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehan"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajar"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkRksd> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkRksd> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRksd> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkRksd> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkRksd> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

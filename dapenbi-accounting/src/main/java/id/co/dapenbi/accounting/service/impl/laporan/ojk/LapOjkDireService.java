package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkDire;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkDireRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkDireService {

    @Autowired
    private OjkDireRepository ojkDireRepository;

    public TableLapOjk<OjkDire> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkDire> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDire> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkDire> output = ojkDireRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkDire> ojkDireIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkDireIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kode", "kode"));
        columns.add(new TableColumn("Nama Produk", "namaProduk"));
        columns.add(new TableColumn("Manajer Investasi", "manajerInvestasi"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Jumlah Unit", "jumlahUnit"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajarFormatted"));
        columns.add(new TableColumn("Nilai", "nilaiSelinvFormatted"));
        columns.add(new TableColumn("%", "persenSelinvFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkDire> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkDire> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDire> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkDireRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkDire> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkDire> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

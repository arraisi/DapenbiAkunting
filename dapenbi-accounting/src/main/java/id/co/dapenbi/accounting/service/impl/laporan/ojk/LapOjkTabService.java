package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkTab;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkTabRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkTabService {

    @Autowired
    private OjkTabRepository ojkTabRepository;

    public TableLapOjk<OjkTab> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkTab> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkTab> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkTab> output = ojkTabRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkTab> ojkTabIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkTabIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Bank", "namaBank"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Nilai Nominal Formatted", "nilaiNominalFormatted", false));
        columns.add(new TableColumn("Tingkat Bunga/Nisbah", "tingkatBunga"));
        columns.add(new TableColumn("Tingkat Bunga/NisbahFormatted", "tingkatBungaFormatted", false));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    private Specification<OjkTab> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkTab> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkTab> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkTab> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkTab> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkTabRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

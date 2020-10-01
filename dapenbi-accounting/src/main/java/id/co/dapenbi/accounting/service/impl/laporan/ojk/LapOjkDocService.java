package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkDoc;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkDocRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkDocService {

    @Autowired
    private OjkDocRepository ojkDocRepository;

    public TableLapOjk<OjkDoc> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkDoc> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDoc> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkDoc> output = ojkDocRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkDoc> ojkDocIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkDocIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Bank", "namaBank"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Nilai Nominal Formatted", "nilaiNominalFormatted"));
        columns.add(new TableColumn("Jangka Waktu", "jangkaWaktu"));
        columns.add(new TableColumn("Tingkat Bunga/Nisbah", "tingkatBungaFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    private Specification<OjkDoc> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkDoc> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkDoc> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkDoc> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDoc> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkDocRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

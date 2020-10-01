package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkDpjka;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkDpjkaRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkDpjkaService {

    @Autowired
    private OjkDpjkaRepository ojkDpjkaRepository;

    public TableLapOjk<OjkDpjka> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkDpjka> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDpjka> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkDpjka> output = ojkDpjkaRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkDpjka> ojkDpjkaIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkDpjkaIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Bank", "namaBank"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominalFormatted"));
        columns.add(new TableColumn("Jangka Waktu", "jangkaWaktu"));
        columns.add(new TableColumn("Tingkat Bunga/Nisbah", "tingkatBungaFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkDpjka> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkDpjka> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkDpjka> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkDpjkaRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkDpjka> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkDpjka> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

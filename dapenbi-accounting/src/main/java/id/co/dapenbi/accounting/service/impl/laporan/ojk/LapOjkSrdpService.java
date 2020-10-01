package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAlm;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSrdp;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPph;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSrdp;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkAlmRepository;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkSrdpRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkSrdpService {

    @Autowired
    private OjkSrdpRepository repository;

    public TableLapOjk<OjkSrdp> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkSrdp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSrdp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkSrdp> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkSrdp> ojkSrdpIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkSrdpIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Bank", "namaBank"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Jangka Waktu", "jangkaWaktu"));
        columns.add(new TableColumn("Tingkat Bunga/Nisbah", "tingkatBunga"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkSrdp> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkSrdp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSrdp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkSrdp> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkSrdp> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

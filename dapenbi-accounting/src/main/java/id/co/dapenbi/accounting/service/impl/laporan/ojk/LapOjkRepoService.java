package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRepo;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkRepoRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkRepoService {

    @Autowired
    private OjkRepoRepository ojkRepoRepository;

    public TableLapOjk<OjkRepo> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkRepo> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRepo> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkRepo> output = ojkRepoRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkRepo> ojkRepoIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkRepoIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Counterparty", "counterparty"));
        columns.add(new TableColumn("Jenis Jaminan (SBN/SBI/OBL)", "jenisJaminan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Jaminan", "nilaiJaminanFormatted"));
        columns.add(new TableColumn("Awal", "peringkatAwal"));
        columns.add(new TableColumn("AKhir", "peringkatAkhir"));
        columns.add(new TableColumn("Jangka Waktu", "jangkaWaktu"));
        columns.add(new TableColumn("Kategori(KSEI/BIS4)", "kategori"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Nominal", "marginNominalFormatted"));
        columns.add(new TableColumn("%", "persenMarginFormatted"));
        columns.add(new TableColumn("Amortized Cost", "amortizedCostFormatted"));
        columns.add(new TableColumn("Nilai Jual", "nilaiJualFormatted"));
        columns.add(new TableColumn("Nilai", "nilaiSelinvFormatted"));
        columns.add(new TableColumn("%", "persenSelinvFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkRepo> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkRepo> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRepo> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkRepoRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkRepo> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkRepo> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

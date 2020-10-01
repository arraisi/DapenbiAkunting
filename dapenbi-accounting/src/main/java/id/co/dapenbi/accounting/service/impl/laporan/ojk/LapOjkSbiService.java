package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSbi;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkSbiRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkSbiService {

    @Autowired
    private OjkSbiRepository repository;

    public TableLapOjk<OjkSbi> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkSbi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSbi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkSbi> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkSbi> ojkSbiIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkSbiIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kode Surat Berharga", "kodeSurga"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Kupon (%)", "persenKupon"));
        columns.add(new TableColumn("Tanggal Jatuh Tempo", "tglJatpo"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehan"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajar"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkSbi> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkSbi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSbi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkSbi> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkSbi> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

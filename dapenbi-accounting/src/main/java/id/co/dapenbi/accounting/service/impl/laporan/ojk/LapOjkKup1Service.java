package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKup1;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKup1Repository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkKup1Service {

    @Autowired
    private OjkKup1Repository repository;

    public TableLapOjk<OjkKup1> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKup1> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKup1> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKup1> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKup1> ojkKup1Iterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKup1Iterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nomor Baris", "idLaporan"));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Nilai", "nilai"));
        return columns;
    }

    public List<OjkKup1> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkKup1> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKup1> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkKup1> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKup1> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKup;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKup2;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKup2Repository;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKupRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkKup2Service {

    @Autowired
    private OjkKupRepository repository;

    @Autowired
    private OjkKup2Repository ojkKup2Repository;

    public TableLapOjk<OjkKup> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKup> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKup> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKup> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKup> ojkKupIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKupIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Nilai", "nilai"));
        return columns;
    }

    public List<OjkKup2> findAll(String kodePeriode, String kodeTahunBuku) {
        /*Specification<OjkKup2> byKodePeriode = this.byKodePeriode2(kodePeriode);
        Specification<OjkKup2> byKodeTahunBuku = this.byKodeTahunBuku2(kodeTahunBuku);
        return ojkKup2Repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));*/
        return IterableUtils.toList(ojkKup2Repository.findAll());
    }

    private Specification<OjkKup> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKup> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    private Specification<OjkKup2> byKodePeriode2(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKup2> byKodeTahunBuku2(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

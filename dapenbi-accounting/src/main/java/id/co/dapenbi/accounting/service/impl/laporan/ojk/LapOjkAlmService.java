package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAlm;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAlm;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkAlmRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkAlmService {

    @Autowired
    private OjkAlmRepository repository;

    public TableLapOjk<OjkAlm> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkAlm> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkAlm> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkAlm> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkAlm> ojkAlmIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkAlmIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Tahun 1 RP", "tahun1Rp"));
//        columns.add(new TableColumn("Tahun 1 Non RP", "tahun1NonRp"));
        columns.add(new TableColumn("Tahun 5 RP", "tahun5Rp"));
        columns.add(new TableColumn("Tahun 5 Non RP", "tahun5NonRp"));
        columns.add(new TableColumn("Tahun 10 RP", "tahun10Rp"));
        columns.add(new TableColumn("Tahun 10 Non RP", "tahun10NonRp"));
        columns.add(new TableColumn("Tahun 11 RP", "tahun11Rp"));
        columns.add(new TableColumn("Tahun 11 Non RP", "tahun11NonRp"));
        return columns;
    }

    public List<OjkAlm> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkAlm> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkAlm> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkAlm> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkAlm> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

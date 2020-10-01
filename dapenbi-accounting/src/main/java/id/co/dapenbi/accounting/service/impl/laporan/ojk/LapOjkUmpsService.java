package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkUmps;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkUmpsRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkUmpsService {

    @Autowired
    private OjkUmpsRepository umpsRepository;

    public TableLapOjk<OjkUmps> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkUmps> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUmps> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkUmps> output = umpsRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkUmps> ojkUmpsIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkUmpsIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("<= 1 tahun", "jumlah1Formatted"));
        columns.add(new TableColumn("> 1 tahun", "jumlah2Formatted"));
        columns.add(new TableColumn("Total", ""));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkUmps> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkUmps> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUmps> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return umpsRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkUmps> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkUmps> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

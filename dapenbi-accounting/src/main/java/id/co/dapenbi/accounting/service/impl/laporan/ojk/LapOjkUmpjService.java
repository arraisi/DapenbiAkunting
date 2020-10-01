package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkUmpj;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkUmpjRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkUmpjService {

    @Autowired
    private OjkUmpjRepository ojkUmpjRepository;

    public TableLapOjk<OjkUmpj> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkUmpj> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUmpj> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkUmpj> output = ojkUmpjRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkUmpj> ojkUmpjIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkUmpjIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("<= 1 tahun", "jumlah1Formatted"));
        columns.add(new TableColumn("> 1 tahun", "jumlah2Formatted"));
        columns.add(new TableColumn("Total", ""));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkUmpj> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkUmpj> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUmpj> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkUmpjRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkUmpj> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkUmpj> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

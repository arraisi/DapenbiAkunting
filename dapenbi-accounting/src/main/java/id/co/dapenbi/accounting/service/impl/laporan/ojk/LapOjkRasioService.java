package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRasio;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkRasioRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkRasioService {

    @Autowired
    private OjkRasioRepository ojkRasioRepository;

    public TableLapOjk<OjkRasio> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkRasio> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRasio> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkRasio> output = ojkRasioRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkRasio> OjkRasioIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(OjkRasioIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Program Pensiun", "programPensiun"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Total", "total"));
        columns.add(new TableColumn("Total Formatted", "totalFormatted", false));
        return columns;
    }

    private Specification<OjkRasio> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkRasio> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkRasio> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkRasio> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRasio> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkRasioRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

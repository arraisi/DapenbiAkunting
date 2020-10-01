package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPdpl;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPdplRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPdplService {

    @Autowired
    private OjkPdplRepository ojkPdplRepository;

    public TableLapOjk<OjkPdpl> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPdpl> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPdpl> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPdpl> output = ojkPdplRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPdpl> ojkPdplIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPdplIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Dana Pensiun yang Mengalihkan", "namaDapen"));
        columns.add(new TableColumn("Jumlah", "jumlahFormatted"));
        columns.add(new TableColumn("Rincian Manfaat Lain", ""));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPdpl> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPdpl> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPdpl> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPdplRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPdpl> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPdpl> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

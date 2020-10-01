package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkBinv;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkBinvRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkBinvService {

    @Autowired
    private OjkBinvRepository ojkBinvRepository;

    public TableLapOjk<OjkBinv> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkBinv> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkBinv> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkBinv> output = ojkBinvRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkBinv> ojkBinvIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkBinvIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Jenis Beban", "jenisBeban"));
        columns.add(new TableColumn("Jumlah", "jumlahFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkBinv> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkBinv> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkBinv> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkBinvRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkBinv> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkBinv> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

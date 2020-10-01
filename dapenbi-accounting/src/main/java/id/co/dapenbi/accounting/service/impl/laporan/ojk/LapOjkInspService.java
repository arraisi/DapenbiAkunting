package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkInsp;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkInspRespository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkInspService {

    @Autowired
    private OjkInspRespository respository;

    public TableLapOjk<OjkInsp> getDatatable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkInsp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkInsp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkInsp> output = respository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkInsp> ojkInspIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkInspIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Pihak", "namaPihak"));
        columns.add(new TableColumn("Jenis", "jenis"));
        columns.add(new TableColumn("Jumlah", "jumlah"));
        columns.add(new TableColumn("Persentase Terhadap Total", "persentase"));
        columns.add(new TableColumn("Batasan Dalam Arahan Investasi", "batasanArahan"));
        columns.add(new TableColumn("Batasan Investasi Sesuai Ketentuan", "batasanSesuai"));
        columns.add(new TableColumn("Jumlah Formatted", "jumlahFormatted", false));
        columns.add(new TableColumn("Persentase Formatted", "persentaseFormatted", false));
        return columns;
    }

    public List<OjkInsp> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkInsp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkInsp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return respository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkInsp> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkInsp> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

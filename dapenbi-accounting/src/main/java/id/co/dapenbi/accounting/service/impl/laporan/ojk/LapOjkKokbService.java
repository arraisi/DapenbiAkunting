package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKokb;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKokbRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkKokbService {

    @Autowired
    private OjkKokbRepository ojkKokbRepository;

    public TableLapOjk<OjkKokb> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKokb> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKokb> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKokb> output = ojkKokbRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKokb> ojkKokbIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKokbIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Emiten", "namaEmiten"));
        columns.add(new TableColumn("Nama Pembeli", "namaPembeli"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Jangka Waktu", "jangkaWaktu"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajarFormatted"));
        columns.add(new TableColumn("Nilai", "nilaiSelinvFormatted"));
        columns.add(new TableColumn("%", "persenSelinvFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkKokb> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkKokb> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKokb> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkKokbRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkKokb> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKokb> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

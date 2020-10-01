package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRoi;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkRoiRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkRoiService {

    @Autowired
    private OjkRoiRepository repository;

    public TableLapOjk<OjkRoi> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkRoi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRoi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkRoi> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkRoi> OjkRoiIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(OjkRoiIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Bunga / Bagi Hasil", "totalBunga"));
        columns.add(new TableColumn("Deviden", "totalDeviden"));
        columns.add(new TableColumn("Sewa", "totalSewa"));
        columns.add(new TableColumn("Laba/Rugi Pelepasan", "totalLaba"));
        columns.add(new TableColumn("Lainnya", "totalLainnya"));
        columns.add(new TableColumn("Hasil Investasi yang Belum Terealisasi", "totalInvBelum"));
        columns.add(new TableColumn("Beban Investasi", "totalBebanInv"));
        columns.add(new TableColumn("Hasil Investasi Bersih", "totalHasilInv"));
        columns.add(new TableColumn("Rata-rata Investasi", "totalRata2Inv"));
        columns.add(new TableColumn("ROI", "totalRoi"));
        return columns;
    }

    public List<OjkRoi> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkRoi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkRoi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkRoi> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkRoi> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

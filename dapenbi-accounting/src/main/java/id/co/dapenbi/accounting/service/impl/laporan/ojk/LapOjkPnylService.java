package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPnyl;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPnylRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPnylService {

    @Autowired
    private OjkPnylRepository ojkPnylRepository;

    public TableLapOjk<OjkPnyl> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPnyl> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPnyl> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPnyl> output = ojkPnylRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPnyl> ojkPnylIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPnylIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Perusahaan", "namaPerusahaan"));
        columns.add(new TableColumn("Nama", "namaPerwakilan"));
        columns.add(new TableColumn("Jabatan", "jabatanPerwakilan"));
        columns.add(new TableColumn("Kategori Penyertaan", "kategoriPenyertaan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Tanggal", "tglPenempatanFormatted"));
        columns.add(new TableColumn("% Kepemilikan", "persenPenempatanFormatted"));
        columns.add(new TableColumn("Total", "totalPenempatanFormatted"));
        columns.add(new TableColumn("% Kepemilikan", "persenPerolehanFormatted"));
        columns.add(new TableColumn("Total", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Tanggal", "tglWajarFormatted"));
        columns.add(new TableColumn("% Kepemilikan", "persenWajarFormatted"));
        columns.add(new TableColumn("Total", "nilaiWajarFormatted"));
        columns.add(new TableColumn("Nilai", "nilaiSelinvFormatted"));
        columns.add(new TableColumn("%", "persenSelinvFormatted"));
        columns.add(new TableColumn("Sektor Ekonomi", "sektorEkonomi"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPnyl> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPnyl> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPnyl> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPnylRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPnyl> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPnyl> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

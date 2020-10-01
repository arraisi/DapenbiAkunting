package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPiub;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPiubRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPiubService {

    @Autowired
    private OjkPiubRepository ojkPiubRepository;

    public TableLapOjk<OjkPiub> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPiub> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPiub> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPiub> output = ojkPiubRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPiub> ojkPiubIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPiubIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Pemberi Kerja (Pendiri/Mitra Pendiri)", "namaPemker"));
        columns.add(new TableColumn("Piutang Bunga Iuran Peserta", "bungaPesertaFormatted"));
        columns.add(new TableColumn("Piutang Bunga Iuran Pemberi Kerja", "bungaPemkerFormatted"));
        columns.add(new TableColumn("Piutang Bunga Iuran Tambahan", "bungaTambahanFormatted"));
        columns.add(new TableColumn("Piutang Bunga Keterlambatan Iuran", ""));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPiub> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPiub> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPiub> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPiubRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPiub> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPiub> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

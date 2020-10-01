package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPiut;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPiutRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPiutService {

    @Autowired
    private OjkPiutRepository ojkPiutRepository;

    public TableLapOjk<OjkPiut> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPiut> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPiut> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPiut> output = ojkPiutRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPiut> ojkPiutIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPiutIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Pemberi Kerja (Pendiri/Mitra)", "namaPemker"));
        columns.add(new TableColumn("Usia Piutang <= 3 Bulan", "uspiu1Pemker"));
        columns.add(new TableColumn("Usia Piutang > 3 Bulan", "uspiu3Pemker"));
        columns.add(new TableColumn("Total", ""));
        columns.add(new TableColumn("Usia Piutang <= 3 Bulan", "uspiu1Peserta"));
        columns.add(new TableColumn("Usia Piutang > 3 Bulan", "uspiu3Peserta"));
        columns.add(new TableColumn("Total", ""));
        columns.add(new TableColumn("Usia Piutang <= 3 Bulan", "uspiu1Tambahan"));
        columns.add(new TableColumn("Usia Piutang > 3 Bulan", "uspiu3Tambahan"));
        columns.add(new TableColumn("Total", ""));
        columns.add(new TableColumn("Piutang Iuran Sukarela Peserta", "iuranSukarela"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPiut> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPiut> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPiut> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPiutRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPiut> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPiut> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

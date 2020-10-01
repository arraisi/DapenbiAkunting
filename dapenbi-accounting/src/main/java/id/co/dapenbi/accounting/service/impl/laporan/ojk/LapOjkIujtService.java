package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkIujt;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkIujtRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkIujtService {

    @Autowired
    private OjkIujtRepository ojkIujtRepository;

    public TableLapOjk<OjkIujt> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkIujt> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkIujt> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkIujt> output = ojkIujtRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkIujt> ojkIujtIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkIujtIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Pendiri/Mitra Pendiri", "namaPendiri"));
        columns.add(new TableColumn("PhDP", "phdp"));
        columns.add(new TableColumn("Iuran Normal Peserta (%)", "persenIuranPesertaFormatted"));
        columns.add(new TableColumn("Iuran Normal Peserta (Jumlah)", "nilaiIuranPesertaFormatted"));
        columns.add(new TableColumn("Iuran Normal Pemberi Kerja (%)", "persenIuranPemkerFormatted"));
        columns.add(new TableColumn("Iuran Normal Pemberi Kerja (Jumlah)", "nilaiIuranPemkerFormatted"));
        columns.add(new TableColumn("Iuran Sukarela Peserta", "iuranSukarelaFormatted"));
        columns.add(new TableColumn("Iuran Tambahan", "iuranTambahanFormatted"));
        columns.add(new TableColumn("Iuran Normal Peserta", "iuranPesertaTerimaFormatted"));
        columns.add(new TableColumn("Iuran Normal Pemberi Kerja", "iuranPemkerTerimaFormatted"));
        columns.add(new TableColumn("Iuran Tambahan", "iuranTambahanTerimaFormatted"));
        columns.add(new TableColumn("Iuran Sukarela Peserta", "iuranSukarelaTerimaFormatted"));
        columns.add(new TableColumn("Iuran Normal Peserta", "iuranPesertaKurlebFormatted"));
        columns.add(new TableColumn("Iuran Normal Pemberi Kerja", "iuranPemkerKurlebFormatted"));
        columns.add(new TableColumn("Iuran Normal Tamabahan", "iuranTambahanKurlebFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkIujt> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkIujt> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkIujt> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkIujtRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkIujt> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkIujt> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkProp;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPropRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPropService {

    @Autowired
    private OjkPropRepository ojkPropRepository;

    public TableLapOjk<OjkProp> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkProp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkProp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkProp> output = ojkPropRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkProp> ojkPropIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPropIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Jenis Objek", "jenisObjek"));
        columns.add(new TableColumn("Alamat", "alamat"));
        columns.add(new TableColumn("Lokasi (Kota, Provinsi)", "lokasi"));
        columns.add(new TableColumn("Luas", "luas"));
        columns.add(new TableColumn("Jenis Bukti Kepemilikan", "jenisKepemilikan"));
        columns.add(new TableColumn("Nomor Surat Kepemilikan", "noKepemilikan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "akumulasiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Nilai Appraisal/Nilai Wajar", "nilaiWajarFormatted"));
        columns.add(new TableColumn("Tahun Appraisal", "tahunAppraisal"));
        columns.add(new TableColumn("Nama Penilai Publik/Independen", "namaPenilai"));
        columns.add(new TableColumn("Nama KJPP", "namaKjpp"));
        columns.add(new TableColumn("Nilai", "nilaiSelinvFormatted"));
        columns.add(new TableColumn("%", "persenSelinvFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkProp> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkProp> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkProp> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPropRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkProp> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkProp> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

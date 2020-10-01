package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkTnbg;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkTnbgRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkTnbgService {

    @Autowired
    private OjkTnbgRepository ojkTnbgRepository;

    public TableLapOjk<OjkTnbg> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkTnbg> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkTnbg> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkTnbg> output = ojkTnbgRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkTnbg> ojkTnbgIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkTnbgIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("Nama Tanah dan/atau Bangunan", "namaTanah"));
        columns.add(new TableColumn("Nomor Sertifikat", "noSertifikat"));
        columns.add(new TableColumn("Alamat Lokasi", "alamatLokasi"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "nilaiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Nilai Perolehan Pada Tanggal Laporan", "nilaiPerolehanLapFormatted"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkTnbg> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkTnbg> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkTnbg> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkTnbgRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkTnbg> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkTnbg> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

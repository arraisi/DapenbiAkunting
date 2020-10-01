package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKndr;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKndrRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkKndrService {

    @Autowired
    private OjkKndrRepository ojkKndrRepository;

    public TableLapOjk<OjkKndr> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKndr> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKndr> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKndr> output = ojkKndrRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKndr> ojkKndrIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKndrIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("No. Plat Kendaraan", "noKendaraan"));
        columns.add(new TableColumn("Jenis Kendaraan", "jenisKendaraan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "nilaiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkKndr> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkKndr> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKndr> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkKndrRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkKndr> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKndr> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

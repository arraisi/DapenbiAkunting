package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAsol;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkAsolRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkAsolService {

    @Autowired
    private OjkAsolRepository ojkAsolRepository;

    public TableLapOjk<OjkAsol> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkAsol> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkAsol> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkAsol> output = ojkAsolRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkAsol> ojkAsolIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkAsolIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("Jenis Aset Operasional Lain", "jenisAset"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "nilaiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Katerangan", "keterangan"));
        return columns;
    }

    public List<OjkAsol> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkAsol> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkAsol> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkAsolRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkAsol> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkAsol> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

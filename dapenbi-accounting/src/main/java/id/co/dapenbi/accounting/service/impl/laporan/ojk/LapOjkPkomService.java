package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPkom;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPkomRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPkomService {

    @Autowired
    private OjkPkomRepository ojkPkomRepository;

    public TableLapOjk<OjkPkom> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPkom> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPkom> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPkom> output = ojkPkomRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPkom> ojkPkomIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPkomIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("Jenis Peralatan Komputer", "jenisPeralatan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "nilaiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPkom> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPkom> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPkom> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPkomRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPkom> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPkom> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

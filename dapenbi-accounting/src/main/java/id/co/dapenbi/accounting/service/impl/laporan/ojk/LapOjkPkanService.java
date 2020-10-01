package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPkan;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPkanRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPkanService {

    @Autowired
    private OjkPkanRepository ojkPkanRepository;

    public TableLapOjk<OjkPkan> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPkan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPkan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPkan> output = ojkPkanRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPkan> ojkPkanIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPkanIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList();
        columns.add(new TableColumn("Jenis Peralatan Kantor", "jenisPeralatan"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehanFormatted"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehanFormatted"));
        columns.add(new TableColumn("Akumulasi Penyusutan", "nilaiPenyusutanFormatted"));
        columns.add(new TableColumn("Nilai Buku", "nilaiBukuFormatted"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPkan> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPkan> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPkan> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPkanRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPkan> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPkan> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

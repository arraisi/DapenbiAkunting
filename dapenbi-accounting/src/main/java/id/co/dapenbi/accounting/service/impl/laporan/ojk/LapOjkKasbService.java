package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKasb;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkKasbRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkKasbService {

    @Autowired
    private OjkKasbRepository ojkKasbRepository;

    public TableLapOjk<OjkKasb> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkKasb> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKasb> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkKasb> output = ojkKasbRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkKasb> ojkKasbIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkKasbIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kas/Nama Bank", "namaRekening"));
        columns.add(new TableColumn("No. Rekening", "kodeRekening"));
        columns.add(new TableColumn("Nominal", "nominalFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan (Tujuan Penggunaan)", "keterangan"));
        return columns;
    }

    public List<OjkKasb> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkKasb> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkKasb> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkKasbRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkKasb> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkKasb> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPpin;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPpinRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPpinService {

    @Autowired
    private OjkPpinRepository ojkPpinRepository;

    public TableLapOjk<OjkPpin> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPpin> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPpin> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPpin> output = ojkPpinRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPpin> ojkPpinIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPpinIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Jenis Investasi", "jenisInvestasi"));
        columns.add(new TableColumn("Selisih Nilai Investasi", "selisihInvestasiFormatted"));
        columns.add(new TableColumn("Peningkatan/Penurunan", "peningkatanPenurunanFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkPpin> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPpin> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPpin> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPpinRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPpin> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPpin> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

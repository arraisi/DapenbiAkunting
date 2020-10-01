package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkUtin;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkUtinRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkUtinService {

    @Autowired
    private OjkUtinRepository ojkUtinRepository;

    public TableLapOjk<OjkUtin> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkUtin> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUtin> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkUtin> output = ojkUtinRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkUtin> ojkUtinIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkUtinIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Jenis Investasi", "jenisInvestasi"));
        columns.add(new TableColumn("Nilai", "nilaiFormatted"));
        columns.add(new TableColumn("Program Pensiun/Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }

    public List<OjkUtin> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkUtin> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkUtin> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkUtinRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkUtin> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkUtin> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }
}

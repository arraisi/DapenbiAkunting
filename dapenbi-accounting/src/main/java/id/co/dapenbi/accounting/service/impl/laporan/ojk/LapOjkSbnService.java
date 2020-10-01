package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSbn;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkSbnRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkSbnService {

    @Autowired
    private OjkSbnRepository ojkSbnRepository;

    public TableLapOjk<OjkSbn> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkSbn> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkSbn> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkSbn> output = ojkSbnRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkSbn> ojkSbnIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkSbnIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Jenis Investasi", "sbnNama"));
        columns.add(new TableColumn("Saldo", "sbnSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "sbnSaldoFormatted", false));
        columns.add(new TableColumn("Nama Jenis Investasi", "obligasiJnsInvestasi"));
        columns.add(new TableColumn("Seri Efek", "obligasiSeriEfek"));
        columns.add(new TableColumn("Jenis Kepemilikan", "obligasiJnsKepemilikan"));
        columns.add(new TableColumn("Rating", "obligasiRating"));
        columns.add(new TableColumn("Saldo", "obligasiSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "obligasiSaldoFormatted", false));
        columns.add(new TableColumn("Nama Jenis Investasi", "reksadanaJnsInvestasi"));
        columns.add(new TableColumn("Manajer Investasi", "reksadanaMngInvestasi"));
        columns.add(new TableColumn("Nilai Wajar", "reksadanaNilaiWajar"));
        columns.add(new TableColumn("% SBN dalam Reksadana", "reksadanaPersenSbn"));
        columns.add(new TableColumn("% SBN dalam Reksadana Formatted", "reksadanaPersenSbnFormatted", false));
        columns.add(new TableColumn("Saldo", "reksadanaSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "reksadanaSaldoFormatted", false));
        columns.add(new TableColumn("Nama Jenis Invetasi", "reksadanatJnsInvestasi"));
        columns.add(new TableColumn("Manajer Investasi", "reksadanatMngInvestasi"));
        columns.add(new TableColumn("Jenis Kepemilikan", "reksadanatJnsKepemilikan"));
        columns.add(new TableColumn("Emiten penerima Dana/Project", "reksadanatEmiten"));
        columns.add(new TableColumn("Saldo", "reksadanatSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "reksadanatSaldoFormatted", false));
        columns.add(new TableColumn("Nama Jenis Investasi", "efekJnsInvestasi"));
        columns.add(new TableColumn("Seri Efek", "efekSeriEfek"));
        columns.add(new TableColumn("Jenis Kepemilikan", "efekJnsKepemilikan"));
        columns.add(new TableColumn("Rating", "efekRating"));
        columns.add(new TableColumn("Saldo", "efekSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "efekSaldoFormatted", false));
        columns.add(new TableColumn("Nama Jenis Investasi", "lainJnsInvestasi"));
        columns.add(new TableColumn("Saldo", "lainSaldo"));
        columns.add(new TableColumn("Saldo Formatted", "lainSaldoFormatted", false));
        return columns;
    }

    private Specification<OjkSbn> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkSbn> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkSbn> findAll() {
        return IterableUtils.toList(ojkSbnRepository.findAll());
    }
}

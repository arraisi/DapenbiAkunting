package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPmi;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPmiRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkPmiService {

    @Autowired
    private OjkPmiRepository ojkPmiRepository;

    public TableLapOjk<OjkPmi> getDatatables(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPmi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPmi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPmi> output = ojkPmiRepository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPmi> ojkPmiIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPmiIterable),
                this.getColumns()
        );
    }

    public List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Nama Manajer Investasi", "managerInvestasi"));
        columns.add(new TableColumn("Nomor Kontrak", "noKontrak"));
        columns.add(new TableColumn("Tanggal Kontrak", "tglKontrak"));
        columns.add(new TableColumn("Masa Perjanjian", "masaPerjanjian"));
        columns.add(new TableColumn("Jenis Investasi", "jnsInvestasi"));
        columns.add(new TableColumn("Jumlah Dana Kelolaan (Rp)", "jumlahDana"));
        columns.add(new TableColumn("Jumlah Dana Kelolaan (Rp) Formatted", "jumlahDanaFormatted", false));
        columns.add(new TableColumn("Tingkat Hasil Investasi Bersih (Rp)", "tingkatHasil"));
        columns.add(new TableColumn("Tingkat Hasil Investasi Bersih (Rp) Formatted", "tingkatHasilFormatted", false));
        columns.add(new TableColumn("Jumlah Biaya Pengelolaan yang dibebankan (Rp)", "jumlahBiaya"));
        columns.add(new TableColumn("Jumlah Biaya Pengelolaan yang dibebankan (Rp) Formatted", "jumlahBiayaFormatted"));
        columns.add(new TableColumn("Terafiliasi dengan Dana Pensiun (Ya/Tidak)", "terafiliasi"));
        return columns;
    }

    private Specification<OjkPmi> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPmi> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public List<OjkPmi> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPmi> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPmi> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return ojkPmiRepository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }
}

package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAlm;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkEba;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkAlmRepository;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkEbaRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LapOjkEbaService {

    @Autowired
    private OjkEbaRepository repository;

    public TableLapOjk<OjkEba> getDataTable() {
        return new TableLapOjk<>(
                IterableUtils.toList(repository.findAll()),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Kode", "kode"));
        columns.add(new TableColumn("Nama Produk", "namaProduk"));
        columns.add(new TableColumn("Nama Penerbit", "namaPenerbit"));
        columns.add(new TableColumn("Tanggal Perolehan", "tglPerolehan"));
        columns.add(new TableColumn("Nilai Nominal", "nilaiNominal"));
        columns.add(new TableColumn("Kupon", "persenKupon"));
        columns.add(new TableColumn("Tanggal Jatuh Tempo", "tglJatpo"));
        columns.add(new TableColumn("Peringkat Awal", "peringkatAwal"));
        columns.add(new TableColumn("Peringkat Akhir", "peringkatAkhir"));
        columns.add(new TableColumn("Nilai Perolehan", "nilaiPerolehan"));
        columns.add(new TableColumn("Nilai Wajar", "nilaiWajar"));
        columns.add(new TableColumn("Nilai Selinv", "nilaiSelinv"));
        columns.add(new TableColumn("Persen Selinv", "persenSelinv"));
        columns.add(new TableColumn("Sektor Ekonomi", "sektorEkonomi"));
        columns.add(new TableColumn("Keterangan", "keterangan"));
        return columns;
    }
}

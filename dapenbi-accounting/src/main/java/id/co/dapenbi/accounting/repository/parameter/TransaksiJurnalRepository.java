package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface TransaksiJurnalRepository extends DataTablesRepository<TransaksiJurnal, Integer> {
    int deleteByKodeTransaksi(Transaksi kodeTransaksi);
}

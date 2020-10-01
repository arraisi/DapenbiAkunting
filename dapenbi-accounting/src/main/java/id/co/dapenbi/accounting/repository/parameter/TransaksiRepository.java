package id.co.dapenbi.accounting.repository.parameter;

import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface TransaksiRepository extends DataTablesRepository<Transaksi, String> {
}

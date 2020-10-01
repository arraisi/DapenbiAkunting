package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface ValidasiSerapRepository extends DataTablesRepository<ValidasiSerap, Long> {
}

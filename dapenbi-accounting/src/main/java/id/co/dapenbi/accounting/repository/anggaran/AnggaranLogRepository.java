package id.co.dapenbi.accounting.repository.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.AnggaranLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface AnggaranLogRepository extends DataTablesRepository<AnggaranLog, Integer> {

    void deleteByNoAnggaran(String noAnggaran);

    Iterable<AnggaranLog> findByNoAnggaran(String noAnggaran);
}

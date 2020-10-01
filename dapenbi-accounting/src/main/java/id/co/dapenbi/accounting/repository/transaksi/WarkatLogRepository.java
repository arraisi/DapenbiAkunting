package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WarkatLogRepository extends DataTablesRepository<WarkatLog, Integer> {

    @Modifying
    @Query("delete from WarkatLog where noWarkat = ?1")
    void deleteByNoWarkat(String noWarkat);
}

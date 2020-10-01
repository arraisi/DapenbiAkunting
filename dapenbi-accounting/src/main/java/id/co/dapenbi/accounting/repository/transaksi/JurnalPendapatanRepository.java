package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JurnalPendapatanRepository extends DataTablesRepository<Warkat, String> {

    @Modifying
    @Query("delete from Warkat where noWarkat = ?1")
    void deleteWarkatById(String id);

    @Modifying
    @Query("update Warkat set statusData = ?2 where noWarkat = ?1")
    void updateStatusData(String noWarkat, String statusData);
}

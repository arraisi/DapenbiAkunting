package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WarkatJurnalRepository extends DataTablesRepository<WarkatJurnal, Integer> {
    Iterable<WarkatJurnal> findByNoWarkat(String noWarkat);
    Iterable<WarkatJurnal> findByNoWarkatAndSaldoNormal(Warkat noWarkat, String saldoNormal);

    int deleteByNoWarkat(String warkat);

    @Modifying
    @Query("delete from WarkatJurnal where noWarkat.noWarkat = ?1")
    void deleteByIdWarkat(String id);
}

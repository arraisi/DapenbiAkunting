package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WarkatRepository extends DataTablesRepository<Warkat, String> {
    @Modifying
    @Query("update Warkat set statusData = ?1 where noWarkat = ?2")
    int updateStatusDataWarkat(String statusData, String noWarkat);

    Optional<Warkat> findByNoWarkat(String id);

    int deleteByNoWarkat(String id);
}

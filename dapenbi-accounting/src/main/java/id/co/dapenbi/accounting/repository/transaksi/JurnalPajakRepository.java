package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JurnalPajakRepository extends DataTablesRepository<Warkat, String> {
    @Query("from Warkat where kodeTransaksi = 'JURNAL-PAJAK'")
    List<Warkat> datatables();

    @Modifying
    @Query("delete from Warkat where noWarkat = ?1")
    void deleteWarkatById(String id);

    @Modifying
    @Query("update Warkat set statusData = ?2 where noWarkat = ?1")
    void updateStatusData(String noWarkat, String statusData);
}

package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface SaldoCurrentRepository extends DataTablesRepository<SaldoCurrent, Integer> {
    Optional<SaldoCurrent> findByIdRekening(Integer idRekening);

    @Modifying
    @Query("update SaldoCurrent set saldoDebet = ?1, saldoKredit = ?2, saldoAkhir = ?3 where idRekening = ?4")
    void updateSaldoCurrentWarkatJurnal(BigDecimal saldoDebit, BigDecimal saldoKredit, BigDecimal saldoAkhir, Integer id);

    @Modifying
    @Query("update SaldoCurrent set nilaiAnggaran = ?2, saldoAnggaran = (?2 + serapTambah - serapKurang - saldoDebet + saldoKredit) where idRekening = ?1")
    void updateFromPenyusunanAnggaran(Integer idRekening, BigDecimal totalAnggaran);

    @Modifying
    @Query("update SaldoCurrent set nilaiAnggaran = ?2, saldoAnggaran = (?2 + serapTambah - serapKurang - saldoDebet + saldoKredit) where idRekening = ?1")
    void updateAnggaranBiaya(Integer idRekening, BigDecimal totalAnggaran);

    @Modifying
    @Query("update SaldoCurrent set nilaiAnggaran = ?2, saldoAnggaran = (?2 - saldoAkhir + serapTambah - serapKurang - saldoJual) where idRekening = ?1")
    void updateAnggaranPendapatan(Integer idRekening, BigDecimal totalAnggaran);
}

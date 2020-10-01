package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoFA;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SaldoFARepository extends DataTablesRepository<SaldoFA, Integer> {

    @Query("select sum(saldoDebet) from SaldoFA where 1 = 1")
    BigDecimal totalDebit();

    @Query("select sum(saldoKredit) from SaldoFA where 1 = 1")
    BigDecimal totalKredit();

    @Modifying
    @Query("update SaldoFA set serapTambah = ?1, serapKurang = ?2, saldoAnggaran = ?3 where idRekening = ?4")
    void updateFromApprovalSerap(BigDecimal serapTambah, BigDecimal serapKurang, BigDecimal saldoAnggaran, Integer idRekening);

    @Modifying
    @Query("update SaldoFA set nilaiAnggaran = ?2, saldoAnggaran = (?2 + serapTambah - serapKurang - saldoDebet + saldoKredit) where idRekening = ?1")
    void updateFromPenyusunanAnggaran(Integer idRekening, BigDecimal totalAnggaran);

    @Modifying
    @Query("update SaldoPA set nilaiAnggaran = ?2, saldoAnggaran = (?2 + serapTambah - serapKurang - saldoDebet + saldoKredit) where idRekening = ?1")
    void updateAnggaranBiaya(Integer idRekening, BigDecimal totalAnggaran);

    @Modifying
    @Query("update SaldoPA set nilaiAnggaran = ?2, saldoAnggaran = (?2 - saldoAkhir + serapTambah - serapKurang - saldoJual) where idRekening = ?1")
    void updateAnggaranPendapatan(Integer idRekening, BigDecimal totalAnggaran);
}

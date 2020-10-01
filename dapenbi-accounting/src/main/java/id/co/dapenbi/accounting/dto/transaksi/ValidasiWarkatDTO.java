package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidasiWarkatDTO {
    private String noWarkat;
    private String idRekening;
    private String kodeRekekning;
    private String namaRekening;
    private String tipeRekening;
    private String saldoNormal;
    private String kodeDRI;
    private String createdBy;
    private BigDecimal jumlahDebit;
    private BigDecimal jumlahKredit;
    private BigDecimal saldoAwal;
    private BigDecimal saldoDebet;
    private BigDecimal saldoKredit;
    private BigDecimal saldoAnggaran;
    private BigDecimal saldoAkhir;
    private BigDecimal saldoJual;
    private BigDecimal nilaiAnggaran;
    private BigDecimal serapTambah;
    private BigDecimal serapKurang;
}

package id.co.dapenbi.accounting.entity.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_SALDO")
public class Saldo {

    @Id
    @SequenceGenerator(sequenceName = "ACC_SALDO_SEQ", allocationSize = 1, name = "saldoGenerator")
    @GeneratedValue(generator = "saldoGenerator")
    @Column(name = "ID_SALDO")
    private Integer idSaldo;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "KODE_DRI")
    private String kodeDri;

    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "TGL_SALDO")
    private Timestamp tglSaldo;

    @Column(name = "SALDO_AWAL")
    private BigDecimal saldoAwal;

    @Column(name = "SALDO_DEBET")
    private BigDecimal saldoDebet;

    @Column(name = "SALDO_KREDIT")
    private BigDecimal saldoKredit;

    @Column(name = "SALDO_AKHIR")
    private BigDecimal saldoAkhir;

    @Column(name = "NILAI_ANGGARAN")
    private BigDecimal nilaiAnggaran;

    @Column(name = "SERAP_TAMBAH")
    private BigDecimal serapTambah;

    @Column(name = "SERAP_KURANG")
    private BigDecimal serapKurang;

    @Column(name = "SALDO_ANGGARAN")
    private BigDecimal saldoAnggaran;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

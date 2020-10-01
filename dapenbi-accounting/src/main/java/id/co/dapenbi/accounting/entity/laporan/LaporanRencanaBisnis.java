package id.co.dapenbi.accounting.entity.laporan;

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
@Table(name = "LAP_RB")
public class LaporanRencanaBisnis {

    @Id
    @SequenceGenerator(sequenceName = "LAP_RB_SEQ", allocationSize = 1, name = "laporanRencanaBisnisGenerator")
    @GeneratedValue(generator = "laporanRencanaBisnisGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "ID_LAPORAN_HDR")
    private Integer idLaporanHdr;

    @Column(name = "ID_LAPORAN_DTL")
    private Integer idLaporanDtl;

    @Column(name = "KODE_RUMUS")
    private String kodeRumus;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "KODE_THNBUKU")
    private String kodeThnBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "KODE_DRI")
    private String kodeDRI;

    @Column(name = "URAIAN")
    private String uraian;

    @Column(name = "LEVEL_AKUN")
    private Integer levelAkun;

    @Column(name = "CETAK_TEBAL")
    private String cetakTebal;

    @Column(name = "CETAK_MIRING")
    private String cetakMiring;

    @Column(name = "CETAK_GARIS")
    private String cetakGaris;

    @Column(name = "URUTAN")
    private String urutan;

    @Column(name = "SALDO_BERJALAN")
    private BigDecimal saldoBerjalan;

    @Column(name = "SALDO_SEBELUM")
    private BigDecimal saldoSebelum;

    @Column(name = "SALDO_PROYEKSI1")
    private BigDecimal saldoProyeksi1;

    @Column(name = "SALDO_PROYEKSI2")
    private BigDecimal saldoProyeksi2;

    @Column(name = "SALDO_PROYEKSI3")
    private BigDecimal saldoProyeksi3;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

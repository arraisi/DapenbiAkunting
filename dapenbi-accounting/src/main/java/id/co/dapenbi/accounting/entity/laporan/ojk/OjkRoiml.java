package id.co.dapenbi.accounting.entity.laporan.ojk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OJK_ROIML")
public class OjkRoiml {

    @Id
    @SequenceGenerator(sequenceName = "OJK_ROIML_SEQ", allocationSize = 1, name = "ojkRoimlGenerator")
    @GeneratedValue(generator = "ojkRoimlGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Date tglLaporan;

    @Column(name = "URAIAN")
    private String uraian;

    @Column(name = "ID_REKENING")
    private Long idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "LEVEL_AKUN")
    private Integer levelAkun;

    @Column(name = "TOTAL_BUNGA")
    private BigDecimal totalBunga;

    @Column(name = "TOTAL_DEVIDEN")
    private BigDecimal totalDeviden;

    @Column(name = "TOTAL_SEWA")
    private BigDecimal totalSewa;

    @Column(name = "TOTAL_LABA")
    private BigDecimal totalLaba;

    @Column(name = "TOTAL_LAINNYA")
    private BigDecimal totalLainnya;

    @Column(name = "TOTAL_INV_BELUM")
    private BigDecimal totalInvBelum;

    @Column(name = "TOTAL_BEBAN_INV")
    private BigDecimal totalBebanInv;

    @Column(name = "TOTAL_HASIL_INV")
    private BigDecimal totalHasilInv;

    @Column(name = "TOTAL_RATA2_INV")
    private BigDecimal totalRata2Inv;

    @Column(name = "TOTAL_ROI")
    private BigDecimal totalRoi;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;
}

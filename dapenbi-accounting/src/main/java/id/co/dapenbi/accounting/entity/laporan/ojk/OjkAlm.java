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
@Table(name = "OJK_ALM")
public class OjkAlm {

    @Id
    @SequenceGenerator(sequenceName = "OJK_ALM_SEQ", allocationSize = 1, name = "ojkAlmGenerator")
    @GeneratedValue(generator = "ojkAlmGenerator")
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

    @Column(name = "TAHUN1_RP")
    private BigDecimal tahun1Rp;

    /*@Column(name = "TANUN1_NONRP")
    private BigDecimal tahun1NonRp;*/

    @Column(name = "TAHUN5_RP")
    private BigDecimal tahun5Rp;

    @Column(name = "TAHUN5_NONRP")
    private BigDecimal tahun5NonRp;

    @Column(name = "TAHUN10_RP")
    private BigDecimal tahun10Rp;

    @Column(name = "TAHUN10_NONRP")
    private BigDecimal tahun10NonRp;

    @Column(name = "TAHUN11_RP")
    private BigDecimal tahun11Rp;

    @Column(name = "TAHUN11_NONRP")
    private BigDecimal tahun11NonRp;

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

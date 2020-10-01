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
@Table(name = "OJK_REKINV")
public class OjkRekinv {

    @Id
    @SequenceGenerator(sequenceName = "OJK_REKINV_SEQ", allocationSize = 1, name = "ojkRekinvGenerator")
    @GeneratedValue(generator = "ojkRekinvGenerator")
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

    @Column(name = "TOTAL_JAN")
    private BigDecimal totJan;

    @Column(name = "TOTAL_FEB")
    private BigDecimal totFeb;

    @Column(name = "TOTAL_MAR")
    private BigDecimal totMar;

    @Column(name = "TOTAL_Apr")
    private BigDecimal totApr;

    @Column(name = "TOTAL_MAY")
    private BigDecimal totMay;

    @Column(name = "TOTAL_JUN")
    private BigDecimal totJun;

    @Column(name = "TOTAL_JUL")
    private BigDecimal totJul;

    @Column(name = "TOTAL_AUG")
    private BigDecimal totAug;

    @Column(name = "TOTAL_SEP")
    private BigDecimal totSep;

    @Column(name = "TOTAL_OCT")
    private BigDecimal totOct;

    @Column(name = "TOTAL_NOV")
    private BigDecimal totNov;

    @Column(name = "TOTAL_DES")
    private BigDecimal totDes;

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

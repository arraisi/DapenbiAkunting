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
@Table(name = "OJK_SBI")
public class OjkSbi {

    @Id
    @SequenceGenerator(sequenceName = "OJK_SBI_SEQ", allocationSize = 1, name = "ojkSbiGenerator")
    @GeneratedValue(generator = "ojkSbiGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Date tglLaporan;

    @Column(name = "KODE_SURGA")
    private String kodeSurga;

    @Column(name = "TGL_PEROLEHAN")
    private Date tglPerolehan;

    @Column(name = "NILAI_NOMINAL")
    private BigDecimal nilaiNominal;

    @Column(name = "PERSEN_KUPON")
    private Float persenKupon;

    @Column(name = "TGL_JATPO")
    private Date tglJatpo;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Column(name = "NILAI_SELINV")
    private BigDecimal nilaiSelinv;

    @Column(name = "MANFAAT_LAIN")
    private Integer manfaatLain;

    @Column(name = "KETERANGAN")
    private String keterangan;

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

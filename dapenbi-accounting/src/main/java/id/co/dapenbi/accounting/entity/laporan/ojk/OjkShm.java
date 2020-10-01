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
@Table(name = "OJK_SHM")
public class OjkShm {

    @Id
    @SequenceGenerator(sequenceName = "OJK_SHM_SEQ", allocationSize = 1, name = "ojkShmGenerator")
    @GeneratedValue(generator = "ojkShmGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Date tglLaporan;

    @Column(name = "KODE_SAHAM")
    private String kodeSaham;

    @Column(name = "NAMA_EMITEN")
    private String namaEmiten;

    @Column(name = "TGL_PEROLEHAN")
    private Date tglPerolehan;

    @Column(name = "JUMLAH_SAHAM")
    private BigDecimal jumlahSaham;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Column(name = "NILAI_SELINV")
    private BigDecimal nilaiSelinv;

    @Column(name = "PERSEN_SELINV")
    private BigDecimal persenSelinv;

    @Column(name = "SEKTOR_EKONOMI")
    private String sektorEkonomi;

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

package id.co.dapenbi.accounting.entity.laporan.ojk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OJK_SRDP")
public class OjkSrdp {

    @Id
    @SequenceGenerator(sequenceName = "OJK_ALM_SRDP", allocationSize = 1, name = "ojkSrdpGenerator")
    @GeneratedValue(generator = "ojkSrdpGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_BANK")
    private String namaBank;

    @Column(name = "TGL_PEROLEHAN")
    private String tglPerolehan;

    @Column(name = "NILAI_NOMINAL")
    private BigDecimal nilaiNominal;

    @Column(name = "JANGKA_WAKTU")
    private String jangkaWaktu;

    @Column(name = "TINGKAT_BUNGA")
    private Float tingkatBunga;

    @Column(name = "MANFAAT_LAIN")
    private Integer manfaatLain;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;
}

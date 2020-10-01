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
@Table(name = "OJK_EBA")
public class OjkEba {

    @Id
    @SequenceGenerator(sequenceName = "OJK_EBA_SEQ", allocationSize = 1, name = "ojkEbaGenerator")
    @GeneratedValue(generator = "ojkEbaGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Date tglLaporan;

    @Column(name = "KODE")
    private String kode;

    @Column(name = "NAMA_PRODUK")
    private String namaProduk;

    @Column(name = "NAMA_PENERBIT")
    private String namaPenerbit;

    @Column(name = "TGL_PEROLEHAN")
    private Date tglPerolehan;

    @Column(name = "NILAI_NOMINAL")
    private BigDecimal nilaiNominal;

    @Column(name = "PERSEN_KUPON")
    private Float persenKupon;

    @Column(name = "TGL_JATPO")
    private Date tglJatpo;

    @Column(name = "PERINGKAT_AWAL")
    private String peringkatAwal;

    @Column(name = "PERINGKAT_AKHIR")
    private String peringkatAkhir;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Column(name = "NILAI_SELINV")
    private BigDecimal nilaiSelinv;

    @Column(name = "PERSEN_SELINV")
    private Float persenSelinv;

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
}

package id.co.dapenbi.accounting.entity.laporan.ojk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OJK_PNYL")
public class OjkPnyl {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_PERUSAHAAN")
    private String namaPerusahaan;

    @Column(name = "NAMA_PERWAKILAN")
    private String namaPerwakilan;

    @Column(name = "JABATAN_PERWAKILAN")
    private String jabatanPerwakilan;

    @Column(name = "KATEGORI_PENYERTAAN")
    private String kategoriPenyertaan;

    @Column(name = "TGL_PEROLEHAN")
    private Timestamp tglPerolehan;

    @Transient
    @JsonProperty("tglPerolehanFormatted")
    public String getTglPerolehanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPerolehan.getTime()));
    }

    @Column(name = "TGL_PENEMPATAN")
    private Timestamp tglPenempatan;

    @Transient
    @JsonProperty("tglPenempatanFormatted")
    public String getTglPenempatanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPenempatan.getTime()));
    }

    @Column(name = "PERSEN_PENEMPATAN")
    private Float persenPenempatan;

    @Transient
    @JsonProperty("persenPenempatanFormatted")
    public String getPersenPenempatanFormatted() {
        return persenPenempatan.toString();
    }

    @Column(name = "TOTAL_PENEMPATAN")
    private BigDecimal totalPenempatan;

    @Transient
    @JsonProperty("totalPenempatanFormatted")
    public String getTotalPenempatanFormatted() {
        return totalPenempatan.toString();
    }

    @Column(name = "PERSEN_PEROLEHAN")
    private Float persenPerolehan;

    @Transient
    @JsonProperty("persenPerolehanFormatted")
    public String getPersenPerolehanFormatted() {
        return persenPerolehan.toString();
    }

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Transient
    @JsonProperty("nilaiPerolehanFormatted")
    public String getNilaiPerolehanFormatted() {
        return nilaiPerolehan.toString();
    }

    @Column(name = "TGL_WAJAR")
    private Timestamp tglWajar;

    @Transient
    @JsonProperty("tglWajarFormatted")
    public String getTglWajarFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglWajar.getTime()));
    }

    @Column(name = "PERSEN_WAJAR")
    private Float persenWajar;

    @Transient
    @JsonProperty("persenWajarFormatted")
    public String getPersenWajarFormatted() {
        return persenWajar.toString();
    }

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Transient
    @JsonProperty("nilaiWajarFormatted")
    public String getNilaiWajarFormatted() {
        return nilaiWajar.toString();
    }

    @Column(name = "NILAI_SELINV")
    private BigDecimal nilaiSelinv;

    @Transient
    @JsonProperty("nilaiSelinvFormatted")
    public String getNilaiSelinvFormatted() {
        return nilaiSelinv.toString();
    }

    @Column(name = "PERSEN_SELINV")
    private Float persenSelinv;

    @Transient
    @JsonProperty("persenSelinvFormatted")
    public String getPersenSelinvFormatted() {
        return persenSelinv.toString();
    }

    @Column(name = "SEKTOR_EKONOMI")
    private String sektorEkonomi;

    @Column(name = "MANFAAT_LAIN")
    private String manfaatLain;

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

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
@Table(name = "OJK_PROP")
public class OjkProp {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "JENIS_OBJEK")
    private String jenisObjek;

    @Column(name = "ALAMAT")
    private String alamat;

    @Column(name = "LOKASI")
    private String lokasi;

    @Column(name = "LUAS")
    private String luas;

    @Column(name = "JENIS_KEPEMILIKAN")
    private String jenisKepemilikan;

    @Column(name = "NO_KEPEMILIKAN")
    private String noKepemilikan;

    @Column(name = "TGL_PEROLEHAN")
    private Timestamp tglPerolehan;

    @Transient
    @JsonProperty("tglPerolehanFormatted")
    public String getTglPerolehanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPerolehan.getTime()));
    }

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Transient
    @JsonProperty("nilaiPerolehanFormatted")
    public String getNilaiPerolehanFormatted() {
        return nilaiPerolehan.toString();
    }

    @Column(name = "AKUMULASI_PENYUSUTAN")
    private BigDecimal akumulasiPenyusutan;

    @Transient
    @JsonProperty("akumulasiPenyusutanFormatted")
    public String getAkumulasiPenyusutanFormatted() {
        return akumulasiPenyusutan.toString();
    }

    @Column(name = "NILAI_BUKU")
    private BigDecimal nilaiBuku;

    @Transient
    @JsonProperty("nilaiBukuFormatted")
    public String getNilaiBukuFormatted() {
        return nilaiBuku.toString();
    }

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Transient
    @JsonProperty("nilaiWajarFormatted")
    public String getNilaiWajarFormatted() {
        return nilaiWajar.toString();
    }

    @Column(name = "TAHUN_APPRAISAL")
    private Integer tahunAppraisal;

    @Column(name = "NAMA_PENILAI")
    private String namaPenilai;

    @Column(name = "NAMA_KJPP")
    private String namaKjpp;

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

    @Column(name = "MANFAAT_LAIN")
    private String manfaatLain;

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

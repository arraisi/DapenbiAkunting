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
@Table(name = "OJK_DNFRA")
public class OjkDnfra {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "KODE")
    private String kode;

    @Column(name = "NAMA_PRODUK")
    private String namaProduk;

    @Column(name = "MANAJER_INVESTASI")
    private String manajerInvestasi;

    @Column(name = "TGL_PEROLEHAN")
    private Timestamp tglPerolehan;

    @Transient
    @JsonProperty("tglPerolehanFormatted")
    public String getTglPerolehanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPerolehan.getTime()));
    }

    @Column(name = "JUMLAH_UNIT")
    private Integer jumlahUnit;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Transient
    @JsonProperty("nilaiPerolehanFormatted")
    public String getNilaiPerolehanFormatted() {
        return nilaiPerolehan.toString();
    }

    @Column(name = "NILAI_AKTIVA")
    private BigDecimal nilaiAktiva;

    @Transient
    @JsonProperty("nilaiAktivaFormatted")
    public String getNilaiAktivaFormatted() {
        return nilaiAktiva.toString();
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

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
@Table(name = "OJK_REPO")
public class OjkRepo {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "COUNTERPARTY")
    private String counterparty;

    @Column(name = "JENIS_JAMINAN")
    private String jenisJaminan;

    @Column(name = "TGL_PEROLEHAN")
    private Timestamp tglPerolehan;

    @Transient
    @JsonProperty("tglPerolehanFormatted")
    public String getTglPerolehanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPerolehan.getTime()));
    }

    @Column(name = "NILAI_JAMINAN")
    private BigDecimal nilaiJaminan;

    @Transient
    @JsonProperty("nilaiJaminanFormatted")
    public String getNilaiJaminanFormatted() {
        return nilaiJaminan.toString();
    }

    @Column(name = "PERINGKAT_AWAL")
    private String peringkatAwal;

    @Column(name = "PERINGKAT_AKHIR")
    private String peringkatAkhir;

    @Column(name = "JANGKA_WAKTU")
    private Integer jangkaWaktu;

    @Column(name = "KATEGORI")
    private String kategori;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Transient
    @JsonProperty("nilaiPerolehanFormatted")
    public String getNilaiPerolehanFormatted() {
        return nilaiPerolehan.toString();
    }

    @Column(name = "MARGIN_NOMINAL")
    private BigDecimal marginNominal;

    @Transient
    @JsonProperty("marginNominalFormatted")
    public String getMarginNominalFormatted() {
        return marginNominal.toString();
    }

    @Column(name = "PERSEN_MARGIN")
    private Float persenMargin;

    @Transient
    @JsonProperty("persenMarginFormatted")
    public String getPersenMarginFormatted() {
        return persenMargin.toString();
    }

    @Column(name = "AMORTIZED_COST")
    private BigDecimal amortizedCost;

    @Transient
    @JsonProperty("amortizedCostFormatted")
    public String getAmortizedCostFormatted() {
        return amortizedCost.toString();
    }

    @Column(name = "NILAI_JUAL")
    private BigDecimal nilaiJual;

    @Transient
    @JsonProperty("nilaiJualFormatted")
    public String getNilaiJualFormatted() {
        return nilaiJual.toString();
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

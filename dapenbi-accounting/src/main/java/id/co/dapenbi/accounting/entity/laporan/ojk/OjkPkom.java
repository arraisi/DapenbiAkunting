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
@Table(name = "OJK_PKOM")
public class OjkPkom {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "JENIS_PERALATAN")
    private String jenisPeralatan;

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

    @Column(name = "NILAI_PENYUSUTAN")
    private BigDecimal nilaiPenyusutan;

    @Transient
    @JsonProperty("nilaiPenyusutanFormatted")
    public String getNilaiPenyusutanFormatted() {
        return nilaiPenyusutan.toString();
    }

    @Column(name = "NILAI_BUKU")
    private BigDecimal nilaiBuku;

    @Transient
    @JsonProperty("nilaiBukuFormatted")
    public String getNilaiBukuFormatted() {
        return nilaiBuku.toString();
    }

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

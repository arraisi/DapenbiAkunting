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
@Table(name = "OJK_TAB")
public class OjkTab {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_BANK")
    private String namaBank;

    @Column(name = "TGL_PEROLEHAN")
    private Timestamp tglPerolehan;

    @Transient
    @JsonProperty("tglPerolehanFormatted")
    public String getTglPerolehanFormatted() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date(tglPerolehan.getTime()));
    }

    @Column(name = "NILAI_NOMINAL")
    private BigDecimal nilaiNominal;

    @Transient
    @JsonProperty("nilaiNominalFormatted")
    private String getNilaiNominalFormatted() {
        return nilaiNominal.toString();
    }

    @Column(name = "TINGKAT_BUNGA")
    private Float tingkatBunga;

    @Transient
    @JsonProperty("tingkatBungaFormatted")
    public String getTingkatBungaFormatted() {
        return tingkatBunga.toString();
    }

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

package id.co.dapenbi.accounting.entity.laporan.ojk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OJK_PPIN")
public class OjkPpin {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "JENIS_INVESTASI")
    private String jenisInvestasi;

    @Column(name = "SELISIH_INVESTASI")
    private BigDecimal selisihInvestasi;

    @Transient
    @JsonProperty("selisihInvestasiFormatted")
    public String getSelisihInvestasiFormatted() {
        return selisihInvestasi.toString();
    }

    @Column(name = "PENINGKATAN_PENURUNAN")
    private BigDecimal peningkatanPenurunan;

    @Transient
    @JsonProperty("peningkatanPenurunanFormatted")
    public String getPeningkatanPenurunanFormatted() {
        return peningkatanPenurunan.toString();
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

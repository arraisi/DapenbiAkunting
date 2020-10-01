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
@Table(name = "OJK_PMI")
public class OjkPmi {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "MANAGER_INVESTASI")
    private String managerInvestasi;

    @Column(name = "NO_KONTRAK")
    private String noKontrak;

    @Column(name = "TGL_KONTRAK")
    private Timestamp tglKontrak;

    @Column(name = "MASA_PERJANJIAN")
    private String masaPerjanjian;

    @Column(name = "JNS_INVESTASI")
    private String jnsInvestasi;

    @Column(name = "JUMLAH_DANA")
    private BigDecimal jumlahDana;

    @Transient
    @JsonProperty("jumlahDanaFormatted")
    public String getJumlahDanaFormatted() {
        return jumlahDana.toString();
    }

    @Column(name = "TINGKAT_HASIL")
    private BigDecimal tingkatHasil;

    @Transient
    @JsonProperty("tingkatHasilFormatted")
    public String getTingkatHasilFormatted() {
        return tingkatHasil.toString();
    }

    @Column(name = "JUMLAH_BIAYA")
    private BigDecimal jumlahBiaya;

    @Transient
    @JsonProperty("jumlahBiayaFormatted")
    public String getJumlahBiayaFormatted() {
        return jumlahBiaya.toString();
    }

    @Column(name = "TERAFILIASI")
    private String terafiliasi;

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

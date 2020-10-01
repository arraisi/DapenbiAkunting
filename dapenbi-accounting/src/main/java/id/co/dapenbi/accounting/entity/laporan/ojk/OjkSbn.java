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
@Table(name = "OJK_SBN")
public class OjkSbn {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "SBN_NAMA")
    private String sbnNama;

    @Column(name = "SBN_SALDO")
    private BigDecimal sbnSaldo;

    @Transient
    @JsonProperty("sbnSaldoFormatted")
    private String getSbnSaldoFormatted() {
        return sbnSaldo.toString();
    }

    @Column(name = "OBLIGASI_JNS_INVESTASI")
    private String obligasiJnsInvestasi;

    @Column(name = "OBLIGASI_SERI_EFEK")
    private String obligasiSeriEfek;

    @Column(name = "OBLIGASI_JNS_KEPEMILIKAN")
    private String obligasiJnsKepemilikan;

    @Column(name = "OBLIGASI_RATING")
    private String obligasiRating;

    @Column(name = "OBLIGASI_SALDO")
    private BigDecimal obligasiSaldo;

    @Transient
    @JsonProperty("obligasiSaldoFormatted")
    public String getObligasiSaldoFormatted() {
        return obligasiSaldo.toString();
    }

    @Column(name = "REKSADANA_JNS_INVESTASI")
    private String reksadanaJnsInvestasi;

    @Column(name = "REKSADANA_MNG_INVESTASI")
    private String reksadanaMngInvestasi;

    @Column(name = "REKSADANA_NILAI_WAJAR")
    private Integer reksadanaNilaiWajar;

    @Column(name = "REKSADANA_PERSEN_SBN")
    private Float reksadanaPersenSbn;

    @Transient
    @JsonProperty("reksadanaPersenSbnFormatted")
    public String getReksadanaPersenSbnFormatted() {
        return reksadanaPersenSbn.toString();
    }

    @Column(name = "REKSADANA_SALDO")
    private BigDecimal reksadanaSaldo;

    @Transient
    @JsonProperty("reksadanaSaldoFormatted")
    public String getReksadanaSaldoFormatted() {
        return reksadanaSaldo.toString();
    }

    @Column(name = "REKSADANAT_JNS_INVESTASI")
    private String reksadanatJnsInvestasi;

    @Column(name = "REKSADANAT_MNG_INVESTASI")
    private String reksadanatMngInvestasi;

    @Column(name = "REKSADANAT_JNS_KEPEMILIKAN")
    private String reksadanatJnsKepemilikan;

    @Column(name = "REKSADANAT_EMITEN")
    private String reksadanatEmiten;

    @Column(name = "REKSADANAT_SALDO")
    private BigDecimal reksadanatSaldo;

    @Transient
    @JsonProperty("reksadanatSaldoFormatted")
    public String getReksadanatSaldoFormatted() {
        return reksadanatSaldo.toString();
    }

    @Column(name = "EFEK_JNS_INVESTASI")
    private String efekJnsInvestasi;

    @Column(name = "EFEK_SERI_EFEK")
    private String efekSeriEfek;

    @Column(name = "EFEK_JNS_KEPEMILIKAN")
    private String efekJnsKepemilikan;

    @Column(name = "EFEK_RATING")
    private String efekRating;

    @Column(name = "EFEK_SALDO")
    private BigDecimal efekSaldo;

    @Transient
    @JsonProperty("efekSaldoFormatted")
    public String getEfekSaldoFormatted() {
        return efekSaldo.toString();
    }

    @Column(name = "LAIN_JNS_INVESTASI")
    private String lainJnsInvestasi;

    @Column(name = "LAIN_SALDO")
    private BigDecimal lainSaldo;

    @Transient
    @JsonProperty("lainSaldoFormatted")
    public String getLainSaldoFormatted() {
        return lainSaldo.toString();
    }
    
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

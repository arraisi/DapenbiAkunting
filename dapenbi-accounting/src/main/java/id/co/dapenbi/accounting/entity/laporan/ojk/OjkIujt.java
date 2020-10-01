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
@Table(name = "OJK_IUJT")
public class OjkIujt {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_PENDIRI")
    private String namaPendiri;

    @Column(name = "PHDP")
    private String phdp;

    @Column(name = "PERSEN_IURAN_PESERTA")
    private Float persenIuranPeserta;

    @Transient
    @JsonProperty("persenIuranPesertaFormatted")
    public String getPersenIuranPesertaFormatted() {
        return persenIuranPeserta.toString();
    }

    @Column(name = "NILAI_IURAN_PESERTA")
    private BigDecimal nilaiIuranPeserta;

    @Transient
    @JsonProperty("nilaiIuranPesertaFormatted")
    public String getNilaiIuranPesertaFormatted() {
        return nilaiIuranPeserta.toString();
    }

    @Column(name = "PERSEN_IURAN_PEMKER")
    private Float persenIuranPemker;

    @Transient
    @JsonProperty("persenIuranPemkerFormatted")
    public String getPersenIuranPemkerFormatted() {
        return persenIuranPemker.toString();
    }

    @Column(name = "NILAI_IURAN_PEMKER")
    private BigDecimal nilaiIuranPemker;

    @Transient
    @JsonProperty("nilaiIuranPemkerFormatted")
    public String getNilaiIuranPemkerFormatted() {
        return nilaiIuranPemker.toString();
    }

    @Column(name = "IURAN_SUKARELA")
    private BigDecimal iuranSukarela;

    @Transient
    @JsonProperty("iuranSukarelaFormatted")
    public String getIuranSukarelaFormatted() {
        return iuranSukarela.toString();
    }

    @Column(name = "IURAN_TAMBAHAN")
    private BigDecimal iuranTambahan;

    @Transient
    @JsonProperty("iuranTamabahanFormatted")
    public String getIuranTambahanFormatted() {
        return iuranTambahan.toString();
    }

    @Column(name = "IURAN_PESERTA_TERIMA")
    private BigDecimal iuranPesertaTerima;

    @Transient
    @JsonProperty("iuranPesertaTerimaFormatted")
    public String getIuranPesertaTerimaFormatted() {
        return iuranPesertaTerima.toString();
    }

    @Column(name = "IURAN_PEMKER_TERIMA")
    private BigDecimal iuranPemkerTerima;

    @Transient
    @JsonProperty("iuranPemkerTerimaFormatted")
    public String getIuranPemkerTerimaFormatted() {
        return iuranPemkerTerima.toString();
    }

    @Column(name = "IURAN_TAMBAHAN_TERIMA")
    private BigDecimal iuranTambahanTerima;

    @Transient
    @JsonProperty("iuranTambahanTerimaFormatted")
    public String getIuranTambahanTerimaFormatted() {
        return iuranTambahanTerima.toString();
    }

    @Column(name = "IURAN_SUKARELA_TERIMA")
    private BigDecimal iuranSukarelaTerima;

    @Transient
    @JsonProperty("iuranSukarelaTerimaFormatted")
    public String getIuranSukarelaTerimaFormatted() {
        return iuranSukarelaTerima.toString();
    }

    @Column(name = "IURAN_PESERTA_KURLEB")
    private BigDecimal iuranPesertaKurleb;

    @Transient
    @JsonProperty("iuranPesertaKurlebFormatted")
    public String getIuranPesertaKurlebFormatted() {
        return iuranPesertaKurleb.toString();
    }

    @Column(name = "IURAN_PEMKER_KURLEB")
    private BigDecimal iuranPemkerKurleb;

    @Transient
    @JsonProperty("iuranPemkerKurlebFormatted")
    public String getIuranPemkerKurlebFormatted() {
        return iuranPemkerKurleb.toString();
    }

    @Column(name = "IURAN_TAMBAHAN_KURLEB")
    private BigDecimal iuranTambahanKurleb;

    @Transient
    @JsonProperty("iuranTamabahanKurlebFormatted")
    public String getIuranTambahanKurlebFormatted() {
        return iuranTambahanKurleb.toString();
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

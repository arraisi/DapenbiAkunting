package id.co.dapenbi.accounting.entity.laporan.ojk;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OJK_PIUB")
public class OjkPiub {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_PEMKER")
    private String namaPemker;

    @Column(name = "BUNGA_PESERTA")
    private Float bungaPeserta;

    @Transient
    @JsonProperty("bungaPesertaFormatted")
    public String getBungaPesertaFormatted() {
        return bungaPeserta.toString();
    }

    @Column(name = "BUNGA_PEMKER")
    private Float bungaPemker;

    @Transient
    @JsonProperty("bungaPemkerFormatted")
    public String getBungaPemkerFormatted() {
        return bungaPemker.toString();
    }

    @Column(name = "BUNGA_TAMBAHAN")
    private Float bungaTamabahan;

    @Transient
    @JsonProperty("bungaTambahanFormatted")
    public String getBungaTambahanFormatted() {
        return bungaTamabahan.toString();
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

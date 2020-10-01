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
@Table(name = "OJK_INSP")
public class OjkInsp {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_PIHAK")
    private String namaPihak;

    @Column(name = "JENIS")
    private String jenis;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "LEVEL_AKUN")
    private Integer levelAkun;

    @Column(name = "JUMLAH")
    private BigDecimal jumlah;

    @Transient()
    @JsonProperty("jumlahFormatted")
    public String getJumlahFormatted() {
        return jumlah.toString();
    }

    @Column(name = "PERSENTASE")
    private Float persentase;

    @Transient
    @JsonProperty("persentaseFormatted")
    public String getPersentaseFormatted() {
        return persentase.toString();
    }

    @Column(name = "BATASAN_ARAHAN")
    private Integer batasanArahan;

    @Column(name = "BATASAN_SESUAI")
    private Integer batasanSesuai;

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

package id.co.dapenbi.accounting.entity.laporan.ojk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OJK_PIUT")
public class OjkPiut {

    @Id
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Timestamp tglLaporan;

    @Column(name = "NAMA_PEMKER")
    private String namaPemker;

    @Column(name = "USPIU1_PEMKER")
    private Integer uspiu1Pemker;

    @Column(name = "USPIU3_PEMKER")
    private Integer uspiu3Pemker;

    @Column(name = "USPIU1_PESERTA")
    private Integer uspiu1Peserta;

    @Column(name = "USPIU3_PESERTA")
    private Integer uspiu3Peserta;

    @Column(name = "USPIU1_TAMBAHAN")
    private Integer uspiu1Tambahan;

    @Column(name = "USPIU3_TAMBAHAN")
    private Integer uspiu3Tambahan;

    @Column(name = "IURAN_SUKARELA")
    private Integer iuranSukarela;

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

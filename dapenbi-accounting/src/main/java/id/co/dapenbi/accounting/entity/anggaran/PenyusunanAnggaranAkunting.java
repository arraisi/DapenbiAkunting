package id.co.dapenbi.accounting.entity.anggaran;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_AT_HDR")
public class PenyusunanAnggaranAkunting {

    @Id
    @Column(name = "NO_ANGGARAN")
    private String noAnggaran;

    @OneToOne
    @JoinColumn(name = "KODE_THNBUKU")
    private TahunBuku kodeThnBuku;

    @OneToOne
    @JoinColumn(name = "KODE_PERIODE")
    private Periode kodePeriode;

    @Column(name = "VERSI")
    private String versi;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "TGL_VALIDASI")
    private Timestamp tglValidasi;

    @Column(name = "USER_VALIDASI")
    private String userValidasi;

    @Column(name = "CATATAN_VALIDASI")
    private String catatanValidasi;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;
}

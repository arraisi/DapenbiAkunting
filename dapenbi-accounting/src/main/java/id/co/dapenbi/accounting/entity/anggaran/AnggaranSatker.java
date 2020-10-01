package id.co.dapenbi.accounting.entity.anggaran;

import id.co.dapenbi.accounting.entity.master.LookupMaster;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACC_ANGGARAN_SATKER")
public class AnggaranSatker {

    @Id
    @Column(name = "NO_ANGGARAN")
    private String noAnggaran;

    @OneToOne
    @JoinColumn(name = "ID_REKENING")
    private Rekening idRekening;

    @OneToOne
    @JoinColumn(name = "KODE_THNBUKU")
    private TahunBuku tahunBuku;

    @OneToOne
    @JoinColumn(name = "KODE_PERIODE")
    private Periode kodePeriode;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "ANGGARAN_LALU")
    private BigDecimal anggaranLalu;

    @Column(name = "REALISASI")
    private BigDecimal realisasi;

    @Column(name = "REALISASI_BERJALAN")
    private BigDecimal realisasiBerjalan;

    @Column(name = "PERKIRAAN")
    private BigDecimal perkiraan;

    @Column(name = "TOTAL_ANGGARAN")
    private BigDecimal totalAnggaran;

    @Column(name = "TERBILANG")
    private String terbilang;

    @Column(name = "PROYEKSI1")
    private BigDecimal proyeksi1;

    @Column(name = "PROYEKSI2")
    private BigDecimal proyeksi2;

    @Column(name = "TGL_VALIDASI")
    private Date tglValidasi;

    @Column(name = "USER_VALIDASI")
    private String userValidasi;

    @Column(name = "CATATAN_VALIDASI")
    private String catatanValidasi;

    @Column(name = "FILE_LAMPIRAN")
    private String fileLampiran;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @OneToOne
    @JoinColumn(name = "ID_SATKER")
    private LookupMaster idSatker;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

package id.co.dapenbi.accounting.entity.transaksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_WARKAT")
@ToString(exclude = {"warkatJurnals"})
public class Warkat {

    @Id
    @Column(name = "NO_WARKAT")
    private String noWarkat;

    @OneToOne
    @JoinColumn(name = "KODE_TRANSAKSI", nullable = false)
    private Transaksi kodeTransaksi;

    @Column(name = "NUWP")
    private String nuwp;

    @Column(name = "JENIS_WARKAT")
    private String jenisWarkat;

    @Column(name = "ID_ORG")
    private Integer idOrg;

    @Column(name = "KODE_ORG")
    private String kodeOrg;

    @Column(name = "NAMA_ORG")
    private String namaOrg;

    @OneToOne
    @JoinColumn(name = "KODE_THNBUKU", nullable = false)
    private TahunBuku tahunBuku;

    @OneToOne
    @JoinColumn(name = "KODE_PERIODE", nullable = false)
    private Periode kodePeriode;

    @Column(name = "TGL_BUKU")
    private Timestamp tglBuku;

    @Column(name = "TGL_TRANSAKSI")
    private Timestamp tglTransaksi;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "TOTAL_TRANSAKSI")
    private BigDecimal totalTransaksi;

    @Column(name = "TERBILANG")
    private String terbilang;

    @Column(name = "NO_PENGANTARWARKAT")
    private String noPengantarWarkat;

    @Column(name = "TGL_VALIDASI")
    private Timestamp tglValidasi;

    @Column(name = "USER_VALIDASI")
    private String userValidasi;

    @Column(name = "CATATAN_VALIDASI")
    private String catatanValidasi;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "ARUSKAS")
    private String arusKas;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @OneToMany(
            mappedBy = "noWarkat",
            cascade = CascadeType.ALL)
    private List<WarkatJurnal> warkatJurnals = new ArrayList<>();
}

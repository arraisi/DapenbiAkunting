package id.co.dapenbi.accounting.entity.transaksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_SERAP")
public class Serap {

    @Id
    @Column(name = "NO_SERAP")
    private String noSerap;

    @Column(name = "TGL_SERAP")
    private Timestamp tglSerap;

    @OneToOne
    @JoinColumn(name = "KODE_THNBUKU")
    private TahunBuku tahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "KETERANGAN_DEBET")
    private String keteranganDebet;

    @Column(name = "KETERANGAN_KREDIT")
    private String keteranganKredit;

    @Column(name = "TOTAL_TRANSAKSI")
    private BigDecimal totalTransaksi;

    @Column(name = "TERBILANG")
    private String terbilang;

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

    @OneToMany(mappedBy = "noSerap", fetch = FetchType.EAGER)
//    @JsonIgnore
    private List<SerapDetail> serapDetail = new ArrayList<>();
}

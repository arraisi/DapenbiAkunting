package id.co.dapenbi.accounting.entity.anggaran;

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
@Table(name = "ACC_AT_DTL")
public class PenyusunanAnggaranAkuntingDetail {

    @Id
    @SequenceGenerator(sequenceName = "ACC_AT_DTL_SEQ", allocationSize = 1, name = "anggaranTahunanDtlGenerator")
    @GeneratedValue(generator = "anggaranTahunanDtlGenerator")
    @Column(name = "ID_ANGGARAN")
    private Integer idAnggaran;

    @Column(name = "NO_ANGGARAN")
    private String noAnggaran;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "KODE_THNBUKU")
    private String kodeThnBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "VERSI")
    private String versi;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "ANGGARAN_LALU")
    private BigDecimal anggaranLalu;

    @Column(name = "REALISASI")
    private BigDecimal realisasi;

    @Column(name = "PERKIRAAN")
    private BigDecimal perkiraan;

    @Column(name = "TOTAL_ANGGARAN")
    private BigDecimal totalAnggaran;

    @Column(name = "PROYEKSI1")
    private BigDecimal proyeksi1;

    @Column(name = "PROYEKSI2")
    private BigDecimal proyeksi2;

    @Column(name = "TGL_VALIDASI")
    private Timestamp tglValidasi;

    @Column(name = "USER_VALIDASI")
    private String userValidasi;

    @Column(name = "CATATAN_VALIDASI")
    private String catatanValidasi;

    @Column(name = "FILE_LAMPIRAN")
    private String fileLampiran;

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

package id.co.dapenbi.accounting.entity.anggaran;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACC_ANGGARAN_LOG")
public class AnggaranLog {

    @Id
    @SequenceGenerator(sequenceName = "ACC_ANGGARAN_LOG_SEQ", allocationSize = 1, name = "anggaranLogGenerator")
    @GeneratedValue(generator = "anggaranLogGenerator")
    @Column(name = "ID_ANGGARAN_LOG")
    private Integer idAnggaranLog;

    @Column(name = "NO_ANGGARAN")
    private String noAnggaran;

    @Column(name = "AKTIVITAS")
    private String aktivitas;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "TOTAL_TRANSAKSI")
    private BigDecimal totalTransaksi;

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

    public AnggaranLog(Anggaran anggaran, String aktivitas) {
        this.setNoAnggaran(anggaran.getNoAnggaran());
        this.setAktivitas(aktivitas);
        this.setKeterangan(anggaran.getKeterangan());
        this.setTotalTransaksi(anggaran.getTotalAnggaran());
        this.setStatusData(anggaran.getStatusData());
        this.setCreatedBy(anggaran.getCreatedBy());
        this.setCreatedDate(anggaran.getCreatedDate());
        this.setUpdatedBy(anggaran.getUpdatedBy());
        this.setUpdatedDate(anggaran.getUpdatedDate());
    }
}

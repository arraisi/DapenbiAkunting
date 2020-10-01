package id.co.dapenbi.accounting.entity.transaksi;

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
@Table(name = "ACC_WARKAT_LOG")
public class WarkatLog {

    @Id
    @SequenceGenerator(sequenceName = "ACC_WARKAT_LOG_SEQ", allocationSize = 1, name = "warkatLogGenerator")
    @GeneratedValue(generator = "warkatLogGenerator")
    @Column(name = "ID_WARKAT_LOG")
    private Integer idWarkatLog;

    @Column(name = "NO_WARKAT")
    private String noWarkat;

    @Column(name = "AKTIVITAS")
    private String aktivitas;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "TOTAL_TRANSAKSI")
    private BigDecimal totalTransaksi;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

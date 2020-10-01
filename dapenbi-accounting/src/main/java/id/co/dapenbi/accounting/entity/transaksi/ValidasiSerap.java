package id.co.dapenbi.accounting.entity.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_SERAP_LOG")
public class ValidasiSerap {

    @Id
    @Column(name = "ID_SERAP_LOG")
    private Long idSerapLog;

    @Column(name = "NO_SERAP")
    private String noSerap;

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
}

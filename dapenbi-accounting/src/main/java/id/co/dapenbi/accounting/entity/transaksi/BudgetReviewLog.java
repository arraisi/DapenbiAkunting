package id.co.dapenbi.accounting.entity.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_BUDGETREVIEW_LOG")
public class BudgetReviewLog {

    @Id
    @SequenceGenerator(sequenceName = "ACC_BUDGETREVIEW_LOG_SEQ", allocationSize = 1, name = "budgetReviewLogGenerator")
    @GeneratedValue(generator = "budgetReviewLogGenerator")
    @Column(name = "ID_BUDGETREVIEW_LOG")
    private Integer idBudgetReviewLog;

    @Column(name = "NO_BUDGETREVIEW")
    private String noBudgetReview;

    @Column(name = "AKTIVITAS")
    private String aktivitas;

    @Column(name = "STATUS_DATA")
    private String statusData;

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
}

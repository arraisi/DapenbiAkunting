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
@Table(name = "ACC_BUDGETREVIEW_DTL")
public class BudgetReviewDetail {

    @Id
    @SequenceGenerator(sequenceName = "ACC_BUDGETREVIEW_DTL_SEQ", allocationSize = 1, name = "budgetReviewDtlGenerator")
    @GeneratedValue(generator = "budgetReviewDtlGenerator")
    @Column(name = "ID_BUDGETREVIEW_DTL")
    private Integer idBudgetReviewDtl;

    @Column(name = "NO_BUDGETREVIEW")
    private String noBudgetReview;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "ANGGARAN_TAHUNAN")
    private BigDecimal anggaranTahunan;

    @Column(name = "REALISASI")
    private BigDecimal realisasi;

    @Column(name = "PERSEN")
    private Float persen;

    @Column(name = "SALDO")
    private BigDecimal saldo;

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

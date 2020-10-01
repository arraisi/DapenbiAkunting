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
@Table(name = "ACC_BUDGETREVIEW")
public class BudgetReview {

    @Id
    @Column(name = "NO_BUDGETREVIEW")
    private String noBudgetReview;

    @Column(name = "KODE_THNBUKU")
    private String kodeThnBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "TRIWULAN")
    private String triwulan;

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
}

package id.co.dapenbi.accounting.entity.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_BUKASISTEM")
public class BukaSistem {

    @Id
    @Column(name = "NO_BUKASISTEM")
    private Long noBukaSistem;

    @Column(name = "TGL_BUKA")
    private Date tglBuka;

    @Column(name = "NILAI_PASIVA")
    private BigDecimal nilaiPasiva;

    @Column(name = "NILAI_AKTIVA")
    private BigDecimal nilaiAktiva;

    @Column(name = "STATUS_PEMAKAIAN")
    private Integer statusPemakaian;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}

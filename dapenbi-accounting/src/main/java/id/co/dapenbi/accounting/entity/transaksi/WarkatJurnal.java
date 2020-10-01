package id.co.dapenbi.accounting.entity.transaksi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
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
@Table(name = "ACC_WARKAT_JURNAL")
public class WarkatJurnal {

    @Id
    @SequenceGenerator(sequenceName = "ACC_WARKAT_JURNAL_SEQ", allocationSize = 1, name = "warkatJurnalGenerator")
    @GeneratedValue(generator = "warkatJurnalGenerator")
    @Column(name = "ID_WARKAT_JURNAL")
    private Integer idWarkatJurnal;

    @ManyToOne()
    @JoinColumn(name = "NO_WARKAT")
    private Warkat noWarkat;

    @OneToOne
    @JoinColumn(name = "ID_REKENING")
    private Rekening idRekening;

    @Column(name = "SALDO_NORMAL")
    private String saldoNormal;

    @Column(name = "JUMLAH_DEBIT")
    private BigDecimal jumlahDebit;

    @Column(name = "JUMLAH_KREDIT")
    private BigDecimal jumlahKredit;

    @Column(name = "NO_URUT")
    private Integer noUrut;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

}

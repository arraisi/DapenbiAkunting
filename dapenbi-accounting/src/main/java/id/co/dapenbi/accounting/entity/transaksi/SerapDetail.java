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
@Table(name = "ACC_SERAP_DTL")
public class SerapDetail {
    @Id
    @Column(name = "ID_SERAP_DTL")
    private Long idSerapDetail;

    @ManyToOne
    @JoinColumn(name = "NO_SERAP")
    @JsonIgnore
    private Serap noSerap;

    @OneToOne
    @JoinColumn(name = "ID_REKENING")
    private Rekening rekening;

    @Column(name = "SALDO_ANGGARAN")
    private BigDecimal saldoAnggaran;

    @Column(name = "JUMLAH_PENGURANG")
    private BigDecimal jumlahPengurang;

    @Column(name = "JUMLAH_PENAMBAH")
    private BigDecimal jumlahPenambah;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

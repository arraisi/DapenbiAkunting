package id.co.dapenbi.accounting.entity.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_JNS_TRANSAKSI_JURNAL")
public class TransaksiJurnal {

    @Id
    @SequenceGenerator(sequenceName = "ACC_JNS_TRANSAKSI_JURNAL_SEQ", allocationSize = 1, name = "transaksiJurnalGeneraator")
    @GeneratedValue(generator = "transaksiJurnalGeneraator")
    @Column(name = "ID_TRANSAKSI_JURNAL")
    private Integer idTransaksiJurnal;

    @ManyToOne
    @JoinColumn(name = "KODE_TRANSAKSI")
    private Transaksi kodeTransaksi;

    @ManyToOne
    @JoinColumn(name = "ID_REKENING")
    private Rekening idRekening;

    @Column(name = "SALDO_NORMAL")
    private String saldoNormal;

    @Column(name = "NO_URUT")
    private String noUrut;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

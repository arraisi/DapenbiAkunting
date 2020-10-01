package id.co.dapenbi.accounting.entity.parameter;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ACC_REKENING")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rekening {

    @Id
    @SequenceGenerator(sequenceName = "ACC_REKENING_SEQ", allocationSize = 1, name = "rekeningGenerator")
    @GeneratedValue(generator = "rekeningGenerator")
    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "ID_PARENT")
    private Integer idParent;

    @Column(name = "IS_SUMMARY")
    private String isSummary;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "LEVEL_REKENING")
    private Integer levelRekening;

    @Column(name = "SALDO_NORMAL")
    private String saldoNormal;

    @Column(name = "STATUS_NERACA_ANGGARAN")
    private String statusNeracaAnggaran;

    @Column(name = "STATUS_PEMILIK_ANGGARAN")
    private String statusPemilikAnggaran;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    @Column(name = "TIPE_REKENING")
    private String tipeRekening;

    @Column(name = "URUTAN")
    private Integer urutan;

    @Column(name = "IS_EDIT")
    private Integer isEdit;

    @Transient
    private SaldoCurrent saldoCurrent;

    @Transient
    private Boolean expanded;

    public Rekening(Integer idRekening) {
        this.idRekening = idRekening;
    }

    public Rekening(Integer idRekening, String kodeRekening, String namaRekening, String saldoNormal) {
        this.idRekening = idRekening;
        this.kodeRekening = kodeRekening;
        this.namaRekening = namaRekening;
        this.saldoNormal = saldoNormal;
    }

}

package id.co.dapenbi.accounting.entity.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACC_ARUSKAS_RINCIAN")
public class ArusKasRincian {

    @Id
    @Column(name = "ID_ARUSKAS")
    private String kodeRincian;

    @Column(name = "KODE_ARUSKAS")
    private String kodeArusKas;

    @Column(name = "ID_REKENING")
    private String idRekening;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "SALDO_AWALTAHUN")
    private BigDecimal saldoAwalTahun;

    @Column(name = "FLAG_RUMUS")
    private String flagRumus;

    @Column(name = "FLAG_GROUP")
    private String flagGroup;

    @Column(name = "FLAG_REKENING")
    private String flagRekening;

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
}

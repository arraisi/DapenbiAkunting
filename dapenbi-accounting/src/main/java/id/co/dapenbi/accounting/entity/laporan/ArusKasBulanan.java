package id.co.dapenbi.accounting.entity.laporan;

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
@Table(name = "ACC_ARUSKAS_BULANAN")
public class ArusKasBulanan {

    @Id
    @SequenceGenerator(sequenceName = "ACC_ARUSKAS_BULANAN_SEQ", allocationSize = 1, name = "arusKasBulananGenerator")
    @GeneratedValue(generator = "arusKasBulananGenerator")
    @Column(name = "ID_ARUSKAS_BULANAN")
    private Integer idArusKasBulanan;

    @Column(name = "TANGGAL")
    private Timestamp tanggal;

    @Column(name = "KODE_ARUSKAS")
    private Integer kodeArusKas;

    @Column(name = "ID_ARUSKAS")
    private Integer idArusKas;

    @Column(name = "KODE_THNBUKU")
    private String kodeThnBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private Integer namaRekening;

    @Column(name = "KODE_DRI")
    private String kodeDRI;

    @Column(name = "FLAG_GROUP")
    private String flagGroup;

    @Column(name = "SALDO")
    private BigDecimal saldo;

    @Column(name = "SALDO_AWALTAHUN")
    private BigDecimal saldoAwalTahun;

    @Column(name = "SALDO_SEBELUM")
    private BigDecimal saldoSebelum;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

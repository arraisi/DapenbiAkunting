package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarkatJurnalDTO {

    private Integer idWarkatJurnal;
    private String noWarkat;
    private Rekening idRekening;
    private BigDecimal jumlahDebit;
    private BigDecimal jumlahKredit;
    private Integer noUrut;
    private String saldoNormal;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private String kodeRekening;
    private String namaRekening;
    private BigDecimal jumlah;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "ACC_WARKAT_JURNAL")
    public static class newData {

        @Id
        @SequenceGenerator(sequenceName = "ACC_WARKAT_JURNAL_SEQ", allocationSize = 1, name = "warkatJurnalGenerator")
        @GeneratedValue(generator = "warkatJurnalGenerator")
        @Column(name = "ID_WARKAT_JURNAL")
        private Integer idWarkatJurnal;

        @Column(name = "NO_WARKAT")
        private String noWarkat;

        @Column(name = "ID_REKENING")
        private String idRekening;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "ACC_WARKAT_JURNAL")
    public static class datatables {

        @Id
        @SequenceGenerator(sequenceName = "ACC_WARKAT_JURNAL_SEQ", allocationSize = 1, name = "warkatJurnalGenerator")
        @GeneratedValue(generator = "warkatJurnalGenerator")
        @Column(name = "ID_WARKAT_JURNAL")
        private Integer idWarkatJurnal;

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        @ManyToOne
        @JoinColumn(name = "NO_WARKAT")
        private Warkat noWarkat;

        @ManyToOne
        @JoinColumn(name = "ID_REKENING")
        private Rekening idRekening;

        @Column(name = "JUMLAH_DEBIT")
        private BigDecimal jumlahDebit;

        @Column(name = "JUMLAH_KREDIT")
        private BigDecimal jumlahKredit;

        @Column(name = "NO_URUT")
        private Integer noUrut;

        @Column(name = "CREATED_BY")
        private String createdBy;

        @Column(name = "CREATED_DATE")
        private Date createdDate;

        @Column(name = "UPDATED_BY")
        private String updatedBy;

        @Column(name = "UPDATED_DATE")
        private Date updatedDate;

    }
}

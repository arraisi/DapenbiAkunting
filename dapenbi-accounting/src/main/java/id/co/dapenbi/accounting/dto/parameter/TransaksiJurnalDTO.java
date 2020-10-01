package id.co.dapenbi.accounting.dto.parameter;

import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaksiJurnalDTO {
    private Integer idTransaksiJurnal;
    private TransaksiDTO kodeTransaksi;
    private RekeningDTO idRekening;
    private String saldoNormal;
    private String noUrut;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    private BigDecimal jumlahDebit;
    private BigDecimal jumlahKredit;
//    private BigDecimal saldoAkhir;

    @Data
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "ACC_JNS_TRANSAKSI_JURNAL")
    public static class DataTables {
        @Id
        @Column(name = "ID_TRANSAKSI_JURNAL")
        private Integer idTransaksiJurnal;
        @Column(name = "KODE_TRANSAKSI")
        private String kodeTransaksi;
        @ManyToOne
        @JoinColumn(name = "ID_REKENING", nullable = false)
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<TransaksiJurnalDTO.Columns> order;
        private TransaksiJurnalDTO.Search search;
        private TransaksiJurnal transaksiJurnal;
        private TransaksiJurnalDTO transaksiJurnalDTO;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Columns {
        private Long column;
        private String dir;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Search {
        private String value;
        private Boolean regex;
    }
}

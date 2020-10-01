package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class InformasiSaldoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InformasiSaldoDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private SaldoCurrent saldoCurrent;
        private Saldo saldo;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaldoCurrent {
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private BigDecimal saldoAwal;
        private BigDecimal saldoDebet;
        private BigDecimal saldoKredit;
        private BigDecimal saldoAkhir;
        private BigDecimal nilaiAnggaran;
        private BigDecimal serapTambah;
        private BigDecimal serapKurang;
        private BigDecimal saldoAnggaran;
        private String statusData;
        private String createdBy;
        private Timestamp createdDate;
        private String updatedBy;
        private Timestamp updatedDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Saldo {
        private Integer idSaldo;
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private String kodeDri;
        private String kodeTahunBuku;
        private String kodePeriode;
        private Timestamp tglSaldo;
        private BigDecimal saldoAwal;
        private BigDecimal saldoDebet;
        private BigDecimal saldoKredit;
        private BigDecimal saldoAkhir;
        private BigDecimal nilaiAnggaran;
        private BigDecimal serapTambah;
        private BigDecimal serapKurang;
        private BigDecimal saldoAnggaran;
        private String createdBy;
        private Timestamp createdDate;
        private String updatedBy;
        private Timestamp updatedDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalSaldo {
        private BigDecimal saldoAset;
        private BigDecimal saldoKewajiban;
    }
}

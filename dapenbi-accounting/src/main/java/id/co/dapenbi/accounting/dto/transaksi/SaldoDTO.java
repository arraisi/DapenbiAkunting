package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaldoDTO {

    private Integer idSaldo;
    private Integer idRekening;
    private String kodeRekening;
    private String namaRekening;
    private String kodeTahunBuku;
    private String kodePeriode;
    private String tglSaldo;
    private BigDecimal saldoAwal;
    private BigDecimal saldoDebet;
    private BigDecimal saldoKredit;
    private BigDecimal saldoAkhir;
    private BigDecimal nilaiAnggaran;
    private BigDecimal serapTambah;
    private BigDecimal serapKurang;
    private BigDecimal saldoAnggaran;
    private BigDecimal saldoJual;
    private String statusData;
    private String kodeDri;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    public SaldoDTO(
            BigDecimal saldoAwal,
            BigDecimal saldoDebet,
            BigDecimal saldoKredit,
            BigDecimal saldoAkhir,
            BigDecimal nilaiAnggaran,
            BigDecimal serapTambah,
            BigDecimal serapKurang,
            BigDecimal saldoAnggaran
    ) {
        this.saldoAwal = saldoAwal;
        this.saldoDebet = saldoDebet;
        this.saldoKredit = saldoKredit;
        this.saldoAkhir = saldoAkhir;
        this.nilaiAnggaran = nilaiAnggaran;
        this.serapTambah = serapTambah;
        this.serapKurang = serapKurang;
        this.saldoAnggaran = saldoAnggaran;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SerapDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private SaldoDTO saldoDTO;
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

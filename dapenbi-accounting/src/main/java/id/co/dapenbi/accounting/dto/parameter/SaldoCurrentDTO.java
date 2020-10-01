package id.co.dapenbi.accounting.dto.parameter;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaldoCurrentDTO {
    private String idRekening;
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

    public SaldoCurrentDTO(BigDecimal saldoAnggaran) {
        this.saldoAnggaran = saldoAnggaran;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
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

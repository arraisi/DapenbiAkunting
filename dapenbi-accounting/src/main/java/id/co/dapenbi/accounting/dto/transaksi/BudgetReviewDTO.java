package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetReviewDTO {

    private String noBudgetReview;
    private TahunBuku kodeThnBuku;
    private Periode kodePeriode;
    private String triwulan;
    private String keterangan;
    private Timestamp tglValidasi;
    private String userValidasi;
    private String catatanValidasi;
    private String statusData;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<BudgetReviewDetailDTO> budgetReviewDetails;

    private String tglTransaksi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DaftarRekeningDTO {
        private RekeningDTO idRekening;
        private String keterangan;
        private BigDecimal anggaranTahunan;
        private BigDecimal realisasi;
        private BigDecimal saldo;
        private Float persen;
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
        private BudgetReviewDTO budgetReviewDTO;
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

package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class LaporanBudgetReviewDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        private Long draw;
        private Long start;
        private Long length;
        private List<LaporanBudgetReviewDto.Columns> order;
        private LaporanBudgetReviewDto.Search search;
        private LaporanBudgetReviewDto.Request request;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String kodeTahunBuku;
        private String kodePeriode;
        private String triwulan;
        private String noBudgetReview;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Integer idRekening;
        private String idRekeningParentLvl2;
        private String idRekeningParentLvl3;
        private String noMataAnggaran;
        private String namaMataAnggaran;
        private BigDecimal anggaranTahunan;
        private BigDecimal realisasi;
        private Float persen;
        private BigDecimal saldoAnggaranTahunan;
        private String keterangan;
        private Integer levelRekening;
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

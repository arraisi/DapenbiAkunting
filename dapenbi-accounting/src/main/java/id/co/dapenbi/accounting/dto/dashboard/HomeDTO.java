package id.co.dapenbi.accounting.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class HomeDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsetNettoDateParameter {
        private String date1;
        private String date2;
        private String date3;
        private String date4;
        private String date5;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsetNettoPeriode {
        private String periode;
        private BigDecimal totalPeriodeBerjalan;
        private BigDecimal totalPeriodeSebelum;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HomeChart {
        private String uraian;
        private String periode;
        private BigDecimal totalSaldoBerjalan;
        private BigDecimal totalSaldoSebelum;
        private BigDecimal totalBeban;
        private BigDecimal totalPendapatan;
        private BigDecimal totalAnggaran;
        private BigDecimal totalRealisasi;
        private BigDecimal totalROI;
        private BigDecimal totalROA;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HomeParameter {
        private String kodeDRI;
        private String kodePeriode;
        private String kodeTahunBuku;
    }
}

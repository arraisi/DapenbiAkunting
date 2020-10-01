package id.co.dapenbi.accounting.dto.anggaran;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class AnggaranPendapatanPengeluaranDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        private Long draw;
        private Long start;
        private Long length;
        private List<AnggaranPendapatanPengeluaranDto.Columns> order;
        private AnggaranPendapatanPengeluaranDto.Search search;
        private AnggaranPendapatanPengeluaranDto.Request request;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String kodeTahunBuku;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String sandiMaPendapatan;
        private String pendapatan;
        private BigDecimal jumlahPendapatan;
        private String sandiMaPengeluaran;
        private String pengeluaran;
        private BigDecimal jumlahPengeluaran;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pendapatan {
        private String sandiMaPendapatan;
        private String pendapatan;
        private BigDecimal jumlahPendapatan;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pengeluaran {
        private String sandiMaPengeluaran;
        private String pengeluaran;
        private BigDecimal jumlahPengeluaran;
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

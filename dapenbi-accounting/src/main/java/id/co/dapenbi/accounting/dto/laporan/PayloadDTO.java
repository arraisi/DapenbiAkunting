package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PayloadDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body<T> {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private T value;
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

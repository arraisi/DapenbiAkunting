package id.co.dapenbi.accounting.dto.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanRencanaBisnis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class LaporanRencanaBisnisDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LaporanRencanaBisnisDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private LaporanRencanaBisnis laporanRencanaBisnis;
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

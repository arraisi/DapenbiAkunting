package id.co.dapenbi.accounting.dto.anggaran;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class AnggaranDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {

        private String noAT;
        private TahunBuku tahunBukuBerjalan;
        private TahunBuku tahunBukuBerikut;
        private Periode periode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Excel {

        private String filePath;
        private List<List<Object>> listData;
    }
}

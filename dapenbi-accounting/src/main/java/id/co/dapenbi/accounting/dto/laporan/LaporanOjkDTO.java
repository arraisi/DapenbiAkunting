package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class LaporanOjkDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Manual {
        private String idLaporan;
        private String uraian;
        private String kodeTahunBuku;
        private String urutan;
        private String kodePeriode;
        private BigDecimal programPensiun;
        private BigDecimal gabungan;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaveManual {
        private String namaLap;
        private String kodePeriode;
        private String tahunBuku;
        List<Manual> list;
    }
}

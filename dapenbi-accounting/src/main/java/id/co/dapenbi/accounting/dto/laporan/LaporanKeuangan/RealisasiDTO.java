package id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealisasiDTO {
    private String periode;
    private String periodeMin1;
    private String jenisRealisasi;
    private String idRekening;

    private String kodeRekening;
    private String namaRekening;
    private String tipeRekening;
    private Integer levelRekening;
    private BigDecimal anggaranTahunan;
    private BigDecimal saldoAnggaran;
    private BigDecimal serapTambah;
    private BigDecimal serapKurang;
    private BigDecimal realisasiBulanLalu;
    private BigDecimal realisasiBulanIni;
    private BigDecimal totalRealisasi;
    private Float persen;
    private BigDecimal nilaiAT;
    private String saldoNormal;
    private BigDecimal saldoDebet;
    private BigDecimal saldoKredit;
    private BigDecimal saldoJual;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {
        private BigDecimal totalAnggaran;
        private BigDecimal totalRealisasiBulanLalu;
        private BigDecimal totalRealisasiBulanIni;
        private BigDecimal totalRealisasiSum;
        private BigDecimal totalNilaiAT;
        private BigDecimal totalPercentage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<RealisasiDTO.Columns> order;
        private RealisasiDTO.Search search;
        private RealisasiDTO realisasiDTO;
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

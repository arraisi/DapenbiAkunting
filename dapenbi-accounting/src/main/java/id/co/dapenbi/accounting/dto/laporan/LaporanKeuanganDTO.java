package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class LaporanKeuanganDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LaporanKeuanganDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private LaporanKeuangan laporanKeuangan;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LaporanKeuangan {
        private String judul;
        private String namaRekening;
        private String rumus;
        private Integer idLaporanHeader;
        private BigDecimal saldoBerjalan;
        private BigDecimal saldoSebelum;
        private String urutan;
        private String cetakJudul;
        private String cetakTebal;
        private String cetakMiring;
        private String cetakGaris;
        private Integer levelAkun;
        private String tabs;
        private String warna;
        private String kodeTahunBuku;
        private String kodePeriode;
        private String kodeDRI;
        private String tanggalLaporan;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PajakPenghasilanBadan {
        private BigDecimal pendapatanObjectPajak;
        private BigDecimal pendapatanBukanObjectPajak;
        private BigDecimal jumlahPendapatan;
        private BigDecimal biayaOperasional;
        private BigDecimal biayaYangTidakBolehDikurangkan;
        private BigDecimal jumlahBiaya;
        private BigDecimal biayaYangDiperkenankan;
        private BigDecimal pendapatanKenaPajak;
        private BigDecimal pajakPenghasilanBadanTerhutang;
        private BigDecimal jumlahPajak;
        private BigDecimal kreditPajak;
        private BigDecimal uangMukaPPhPasal23;
        private BigDecimal uangMukaPPhPasal25;
        private BigDecimal jumlahKreditPajak;
        private BigDecimal pajakPenghasilanBadan;
        private Float pctPajak;
        private BigDecimal pctObjectPajak;
        private BigDecimal pctBukanObjectPajak;
        private BigDecimal pctPendapatan;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Neraca {
        private List<LaporanKeuanganDTO.LaporanKeuangan> odd;
        private List<LaporanKeuanganDTO.LaporanKeuangan> even;
        private Integer oddSubreport;
        private Integer evenSubreport;
        private String judulTotalSubreportOdd;
        private String judulTotalSubreportEven;
        private BigDecimal totalBerjalanSubreportOdd;
        private BigDecimal totalSebelumSubreportOdd;
        private BigDecimal totalBerjalanSubreportEven;
        private BigDecimal totalSebelumSubreportEven;
        private BigDecimal totalSaldoBerjalanAset;
        private BigDecimal totalSaldoBerjalanLiabilitas;
        private BigDecimal totalSaldoSebelumAset;
        private BigDecimal totalSaldoSebelumLiabilitas;
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

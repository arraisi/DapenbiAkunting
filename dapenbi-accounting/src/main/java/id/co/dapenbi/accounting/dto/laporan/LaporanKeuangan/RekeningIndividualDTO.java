package id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan;

import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RekeningIndividualDTO {

    private String noWarkat;
    private TahunBuku tahunBuku;
    private String kodeRekening;
    private String namaRekening;
    private String tglTransaksi;
    private String tglPeriode1;
    private String tglPeriode2;
    private String saldoNormal;
    private String idRekening;
    private BigDecimal jumlahDebit;
    private BigDecimal jumlahKredit;
    private BigDecimal saldoAwal;
    private BigDecimal saldo;
    private String nuwp;
    private String keterangan;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Header {
        private String kodeTahunBuku;
        private String namaTahunBuku;
        private String kodePeriode;
        private String namaPeriode;
        private String kodeRekening;
        private String namaRekening;
        private String saldoNormal;
        private BigDecimal saldoAwal;
        private BigDecimal saldoAkhir;
        private String tglPeriode1;
        private String tglPeriode2;
        private List<RekeningIndividualDTO> details;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<RekeningIndividualDTO.Columns> order;
        private Search search;
        private RekeningIndividualDTO rekeningIndividualDTO;
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

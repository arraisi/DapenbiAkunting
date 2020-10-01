package id.co.dapenbi.accounting.dto.dri;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class DRIDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DRISementara {
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private BigDecimal saldoAkhir;
        private Integer levelRekening;
        private String tglPeriode;
        private String isSummary;
        private BigDecimal saldoAkhirFormatted;
        private String saldoNormal;
        private String saldoAkhirString;
        private String cetakTebal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DRI {
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private BigDecimal saldoAkhir;
        private String kodeDRI;
        private Integer levelRekening;
        private String tglPeriode;
        private BigDecimal saldoAkhirFormatted;
        private String saldoNormal;
        private String cetakTebal;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DRIDebitKredit {
        private Integer idRekeningDebit;
        private String kodeRekeningDebit;
        private String namaRekeningDebit;
        private BigDecimal saldoAkhirDebit;
        private Integer levelRekeningDebit;
        private String tglPeriodeDebit;
        private String isSummaryDebit;
        private BigDecimal saldoAkhirDebitFormatted;
        private String saldoNormalDebit;
        private String cetakTebalDebit;
        private Integer idRekeningKredit;
        private String kodeRekeningKredit;
        private String namaRekeningKredit;
        private BigDecimal saldoAkhirKredit;
        private Integer levelRekeningKredit;
        private String tglPeriodeKredit;
        private String isSummaryKredit;
        private BigDecimal saldoAkhirKreditFormatted;
        private String saldoNormalKredit;
        private String cetakTebalKredit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportData {
        private List<Object> oddPage;
        private List<Object> evenPage;
        private String totalSaldoDebit;
        private String totalSaldoKredit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportDataSementara {
        private List<DRISementara> oddPage;
        private List<DRISementara> evenPage;
        private BigDecimal totalSaldoDebit;
        private BigDecimal totalSaldoKredit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExportDataDRI {
        private List<DRI> oddPage;
        private List<DRI> evenPage;
        private BigDecimal totalSaldoDebit;
        private BigDecimal totalSaldoKredit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DRIDatatablesBody  {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private DRISementara driSementara;
        private DRI dri;
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

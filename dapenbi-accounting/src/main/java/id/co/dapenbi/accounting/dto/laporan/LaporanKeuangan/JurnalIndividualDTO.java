package id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JurnalIndividualDTO {

    private String noWarkat;
    private String tglTransaksi;
    private String startDate;
    private String endDate;
    private String idRekening;
    private String nuwp;
    private String keterangan;
    private BigDecimal totalWarkatPembukuan;
    private BigDecimal totalMutasiDebet;
    private BigDecimal totalMutasiKredit;
    private List<JurnalIndividualDetails> jurnalIndividualDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JurnalIndividualDetails {
        private String tglTransaksi;
        private String kodeRekening;
        private String namaRekening;
        private BigDecimal jumlahDebit;
        private BigDecimal jumlahKredit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rekening {
        private String noWarkat;
        private String nuwp;
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private Integer levelRekening;
        private String tipeRekening;
        private Integer urutan;
        private String  saldoNormal;
        private String statusNeracaAnggaran;
        private String statusPemilikAnggaran;
        private String statusAktif;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<JurnalIndividualDTO.Columns> order;
        private Search search;
        private JurnalIndividualDTO jurnalIndividualDTO;
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

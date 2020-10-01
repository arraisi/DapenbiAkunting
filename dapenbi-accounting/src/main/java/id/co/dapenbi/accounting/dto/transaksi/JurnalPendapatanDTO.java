package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JurnalPendapatanDTO {

    private String noWarkat;
    @NotNull
    private Transaksi kodeTransaksi;
    private Integer idOrg;
    private String kodeOrg;
    private TahunBuku tahunBuku;
    private Periode kodePeriode;
    @NotNull
    private Timestamp tglBuku;
    @NotNull
    private Timestamp tglTransaksi;
    private String keterangan;
    private String catatanValidasi;
    private BigDecimal totalTransaksi;
    private String terbilang;
    private String noPengantarWarkat;
    private Timestamp tglValidasi;
    private String userValidasi;
    @NotNull
    private String statusData;
    private String jenisWarkat;
    private String arusKas;
    private String nuwp;
    private String namaOrg;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<WarkatJurnal> warkatJurnals;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<WarkatDTO.Columns> order;
        private WarkatDTO.Search search;
        private JurnalPendapatanDTO jurnalPendapatanDTO;
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

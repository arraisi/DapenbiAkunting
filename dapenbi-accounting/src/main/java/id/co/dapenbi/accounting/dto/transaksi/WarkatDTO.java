package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"warkatJurnals"})
public class WarkatDTO {
    private String noWarkat;
    @NotNull
    private Transaksi kodeTransaksi;
    private String nuwp;
    private String jenisWarkat;
    private Integer idOrg;
//    @NotNull
    private String kodeOrg;
    private String namaOrg;
    private TahunBuku tahunBuku;
    private Periode kodePeriode;
    @NotNull
    private Timestamp tglBuku;
    @NotNull
    private Timestamp tglTransaksi;
    private String keterangan;
    private BigDecimal totalTransaksi;
    private String terbilang;
    private String noPengantarWarkat;
    private Timestamp tglValidasi;
    private String userValidasi;
    private String catatanValidasi;
    @NotNull
    private String statusData;
    private String arusKas;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<WarkatJurnal> warkatJurnals;

    private String startDate;
    private String endDate;
    private List<WarkatJurnalDTO> debitList;
    private List<WarkatJurnalDTO> kreditList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private WarkatDTO warkatDTO;
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

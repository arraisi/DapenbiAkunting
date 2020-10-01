package id.co.dapenbi.accounting.dto.parameter;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RekeningDTO {

    private Integer idRekening;
    private Integer idParent;
    private String isSummary;
    private String kodeRekening;
    private String namaRekening;
    private Integer levelRekening;
    private String tipeRekening;
    private Integer urutan;
    private String  saldoNormal;
    private String statusNeracaAnggaran;
    private String statusPemilikAnggaran;
    private BigDecimal saldoMin;
    private BigDecimal saldoMax;
    private String kodeOrg;
    private String statusAktif;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private SaldoCurrent saldoCurrent;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<RekeningDTO.Columns> order;
        private RekeningDTO.Search search;
        private RekeningDTO rekeningDTO;
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

package id.co.dapenbi.accounting.dto.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenyusunanAnggaranAkuntingDetailDTO {

    private Integer idAnggaranDtl;
    private PenyusunanAnggaranAkunting noAnggaran;
    private Rekening idRekening;
    private String kodeThnBuku;
    private String kodePeriode;
    private String versi;
    private String keterangan;
    private BigDecimal anggaranLalu;
    private BigDecimal realisasi;
    private BigDecimal perkiraan;
    private BigDecimal totalAnggaran;
    private BigDecimal proyeksi1;
    private BigDecimal proyeksi2;
    private Timestamp tglValidasi;
    private String userValidasi;
    private String catatanValidasi;
    private String fileLampiran;
    private String statusData;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String statusAktif;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
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

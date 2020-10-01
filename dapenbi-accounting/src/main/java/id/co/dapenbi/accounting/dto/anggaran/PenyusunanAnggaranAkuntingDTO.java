package id.co.dapenbi.accounting.dto.anggaran;

import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenyusunanAnggaranAkuntingDTO {

    private String noAnggaran;
    private TahunBuku kodeThnBuku;
    private Periode kodePeriode;
    private String versi;
    private String keterangan;
    private Timestamp tglValidasi;
    private String userValidasi;
    private String catatanValidasi;
    private String statusData;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private String statusAktif;
    private List<PenyusunanAnggaranAkuntingDetailDTO> penyusunanAnggaranAkuntingDetail;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DaftarRekeningDTO {
        private RekeningDTO idRekening;
        private BigDecimal anggaranLalu;
        private BigDecimal realisasi;
        private BigDecimal perkiraan;
        private BigDecimal totalAnggaran;
        private BigDecimal proyeksi1;
        private BigDecimal proyeksi2;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private PenyusunanAnggaranAkuntingDTO penyusunanAnggaranAkuntingDTO;
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

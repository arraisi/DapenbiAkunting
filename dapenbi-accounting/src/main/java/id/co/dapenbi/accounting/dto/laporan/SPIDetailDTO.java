package id.co.dapenbi.accounting.dto.laporan;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SPIDetailDTO {
    private String _tglSPI;
    private Integer idSPIDtl;
    private Integer idSPIHdr;
    private String keteranganSPI;
    private TahunBuku kodeTahunBuku;
    private Periode kodePeriode;
    private Timestamp tglSPI;
    private String idInvestasi;
    private String idSPI;
    private String statusData;
    private BigDecimal nilaiPerolehan;
    private BigDecimal nilaiWajar;
    private BigDecimal nilaiSPI;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<SPIDetailDTO.Columns> order;
        private SPIDetailDTO.Search search;
        private SPIDetailDTO spiDetailDTO;
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

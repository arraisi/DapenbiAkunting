package id.co.dapenbi.accounting.dto.laporan;

import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SPIHeaderDTO {
    private Integer idSPIHdr;
    private TahunBuku kodeTahunBuku;
    private Periode kodePeriode;
    private String statusData;
    private Timestamp tglSPI;
    private Timestamp tglValidasi;
    private String userValidasi;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<SPIDetailDTO> spiDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<SPIHeaderDTO.Columns> order;
        private SPIHeaderDTO.Search search;
        private SPIHeaderDTO spiHeaderDTO;
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

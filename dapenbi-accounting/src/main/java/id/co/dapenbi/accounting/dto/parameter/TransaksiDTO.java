package id.co.dapenbi.accounting.dto.parameter;

import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "transaksiJurnals")
public class TransaksiDTO {
    private String jenisWarkat;
    private String kodeTransaksi;
    private String namaTransaksi;
    private String statusAktif;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private List<TransaksiJurnalDTO> transaksiJurnals = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<TransaksiDTO.Columns> order;
        private TransaksiDTO.Search search;
        private TransaksiDTO transaksiDTO;
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

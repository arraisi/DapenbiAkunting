package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SerapDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SerapDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private Serap serap;
        private RekeningDTO rekening;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Serap {
        private String noSerap;
        private Timestamp tglSerap;
        private TahunBuku tahunBuku;
        private Periode periode;
        private String keteranganDebet;
        private String keteranganKredit;
        private BigDecimal totalTransaksi;
        private String terbilang;
        private Timestamp tglValidasi;
        private String userValidasi;
        private String catatanValidasi;
        private String statusData;
        private String createdBy;
        private Timestamp createdDate;
        private String updatedBy;
        private Timestamp updatedDate;
        private List<SerapDetail> serapDetail = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RekeningDTO {
        private String tipeRekening;
        private Integer levelRekening;
        private String isSummary;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SerapExport {
        private String kodeRekening;
        private BigDecimal jumlahPenambah;
        private BigDecimal jumlahPengurang;
        private String keterangan;
    }

}

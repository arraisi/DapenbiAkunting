package id.co.dapenbi.accounting.dto.parameter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class ArusKasRincianDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArusKasRincianEntity {
        private String kodeRincian;
        private String kodeArusKas;
        private String idRekening;
        private String keterangan;
        private BigDecimal saldoAwalTahun;
        private String flagRumus;
        private String flagGroup;
        private String flagRekening;
        private String statusAktif;
        private String createdBy;
        private Timestamp createdDate;
        private String updatedBy;
        private Timestamp updatedDate;
        private String detailRekening;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArusKasRincianDatatablesBody {
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

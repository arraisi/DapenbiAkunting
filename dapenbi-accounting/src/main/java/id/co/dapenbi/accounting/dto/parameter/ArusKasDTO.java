package id.co.dapenbi.accounting.dto.parameter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArusKasDTO {
    private String kodeArusKas;
    private String keterangan;
    private String statusAktif;
    private String arusKasAktivitas;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
    private BigDecimal totalPenambahan;
    private BigDecimal totalPengurangan;
    private BigDecimal total;
    private List<RincianArusKas> dataPenambahan;
    private List<RincianArusKas> dataPengurangan;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RincianArusKas {
        private Integer idRekening;
        private String kodeRekening;
        private String namaRekening;
        private String keterangan;
        private BigDecimal saldo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalKas {
        private BigDecimal kasAktivitasInvestasi;
        private BigDecimal kasAktivitasOperasional;
        private BigDecimal kasAktivitasPendanaan;
        private BigDecimal totalKasAktivitas;
        private BigDecimal kasAwalPeriode;
        private BigDecimal kasAkhirPeriode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArusKasDatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private ArusKasDTO arusKasDTO;
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

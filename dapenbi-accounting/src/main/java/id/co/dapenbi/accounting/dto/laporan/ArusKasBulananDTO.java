package id.co.dapenbi.accounting.dto.laporan;

import id.co.dapenbi.accounting.entity.laporan.LaporanRekening;
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
public class ArusKasBulananDTO {
    private Integer idArusKasBulanan;
    private Timestamp tanggal;
    private String kodeArusKas;
    private String idArusKas;
    private TahunBuku kodeTahunBuku;
    private Periode kodePeriode;
    private Integer idRekening;
    private String kodeRekening;
    private String namaRekening;
    private String keterangan;
    private String kodeDRI;
    private String flagGroup;
    private BigDecimal saldo;
    private BigDecimal saldoAwaltahun;
    private BigDecimal saldoSebelumnya;
    private String createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatatablesBody {
        private Long draw;
        private Long start;
        private Long length;
        private List<Columns> order;
        private Search search;
        private ArusKasBulananDTO arusKasBulananDTO;
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
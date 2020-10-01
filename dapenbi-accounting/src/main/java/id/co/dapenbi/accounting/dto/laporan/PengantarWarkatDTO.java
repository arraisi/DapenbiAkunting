package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PengantarWarkatDTO {
    private String noWarkat;
    private String keterangan;
    private BigDecimal totalTransaksi;
    private String tglTransaksi;
    private String statusData;
}

package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaporanSPIDTO {
    private Integer idSPIHDR;
    private String keterangan;
    private BigDecimal nilaiPerolehan;
    private BigDecimal nilaiWajar;
    private BigDecimal nilaiSPI;
}

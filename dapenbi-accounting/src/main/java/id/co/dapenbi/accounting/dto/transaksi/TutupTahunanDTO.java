package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutupTahunanDTO {

    private Timestamp tglTransaksi;
    private String kodeThnBuku;
    private String kodePeriode;
    private Integer totalWarkat;
    private Integer totalWarkatValid;
    private Integer totalWarkatPA;
    private Integer totalWarkatFA;

}

package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BukaSistemDto {

    private Long noBukaSistem;
    private String tglTransaksiSebelum;
    private String tglTransaksiSekarang;
    private String kodeTahunBuku;
    private String kodePeriode;
    private String kodeDRI;
    private BigDecimal nilaiPasiva;
    private BigDecimal nilaiAktiva;
    private Integer statusPemakai;
    private Integer statusSistem;
    private String statusOpen;
    private Integer idOrg;
    private Integer statusPemakaian;
}

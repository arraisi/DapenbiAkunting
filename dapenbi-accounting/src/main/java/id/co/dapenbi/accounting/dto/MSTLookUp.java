package id.co.dapenbi.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSTLookUp {
    private String kodeLookUp;
    private String jenisLookUp;
    private String namaLookUp;
    private String keterangan;
    private String statusData;
}

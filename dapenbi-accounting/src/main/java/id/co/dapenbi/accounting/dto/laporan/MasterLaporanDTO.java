package id.co.dapenbi.accounting.dto.laporan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.laporan.LaporanHeader;
import lombok.Data;

import java.util.List;

@Data
public class MasterLaporanDTO {

    private Integer idLaporanHdr;
    private String namaLaporan;
    private String keterangan;
    private List<LaporanDetail> laporanDetails;

    @JsonIgnore
    public LaporanHeader getLaporanHeader() {
        return new LaporanHeader(
                idLaporanHdr, namaLaporan, keterangan
        );
    }
}

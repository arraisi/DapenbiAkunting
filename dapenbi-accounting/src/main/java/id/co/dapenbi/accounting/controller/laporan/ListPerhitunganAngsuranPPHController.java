package id.co.dapenbi.accounting.controller.laporan;

import id.co.dapenbi.accounting.dto.laporan.LaporanKeuanganDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanKeuangan;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuanganService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/akunting")
public class ListPerhitunganAngsuranPPHController {

    @Autowired
    private LaporanKeuanganService laporanKeuanganService;

    @GetMapping("/perhitungan-angsuran/list")
    public ResponseEntity<List<LaporanKeuanganDTO.LaporanKeuangan>> listPerhitunganAngsuranPPH(
            @RequestParam(defaultValue = "1") String kodeDRI,
            @RequestParam String kodePeriode,
            @RequestParam String tahun
    ) {
        List<LaporanKeuanganDTO.LaporanKeuangan> data = laporanKeuanganService.listPerhitunganAngsuranPPH(kodeDRI, kodePeriode, tahun);
        if (data.size() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return ResponseEntity.ok(data);
    };
}

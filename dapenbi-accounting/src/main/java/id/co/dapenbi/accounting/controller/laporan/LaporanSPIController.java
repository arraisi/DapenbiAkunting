package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanSPIDTO;
import id.co.dapenbi.accounting.dto.laporan.PayloadDTO;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanSPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/akunting/laporan/laporan-spi")
public class LaporanSPIController {

    @Autowired
    private LaporanSPIService laporanSPIService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/laporanSPI");
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<LaporanSPIDTO> datatables(@RequestBody PayloadDTO.Body<LaporanSPIDTO> payload) {
        return laporanSPIService.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getValue()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPDF(@RequestParam Integer kodeSPIHDR, @RequestParam String tanggal) {
        Map<String, Object> map = new HashMap<>();
        map.put("tanggal", tanggal);
        map.put("kodeSPIHDR", kodeSPIHDR);
        byte[] bytes = laporanSPIService.exportPDF("/jasper/laporan_spi.jasper", map);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportEXCEL(@RequestParam Integer kodeSPIHDR, @RequestParam String tanggal) {
        Map<String, Object> map = new HashMap<>();
        map.put("tanggal", tanggal);
        map.put("kodeSPIHDR", kodeSPIHDR);
        byte[] bytes = laporanSPIService.exportEXCEL("/jasper/laporan_spi.jasper", map);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

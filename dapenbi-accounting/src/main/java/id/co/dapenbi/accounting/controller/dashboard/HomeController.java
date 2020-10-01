package id.co.dapenbi.accounting.controller.dashboard;

import id.co.dapenbi.accounting.dto.dashboard.HomeDTO;
import id.co.dapenbi.accounting.service.impl.dashboard.HomeService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/akunting/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showHome() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/dashboard/home");
        modelAndView.addObject("listTahunBuku", tahunBukuService.listTahunSort());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping("/aset-netto/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HomeDTO.HomeChart>> listAsetNetto(@RequestBody HomeDTO.HomeParameter params) {
        List<HomeDTO.HomeChart> list = homeService.listAsetNetto(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/perubahan-aset-netto/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HomeDTO.HomeChart>> listPerubahanAsetNetto(@RequestBody HomeDTO.HomeParameter params) {
        List<HomeDTO.HomeChart> list = homeService.listPerubahanAsetNetto(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/perhitungan-hasil-usaha/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<List<HomeDTO.HomeChart>>> listPerhitunganHasilUsaha(@RequestBody HomeDTO.HomeParameter params) {
        List<List<HomeDTO.HomeChart>> list = homeService.listPerhitunganHasilUsaha(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/neraca/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HomeDTO.HomeChart>> listNeraca(@RequestBody HomeDTO.HomeParameter params) {
        List<HomeDTO.HomeChart> list = homeService.listNeraca(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/arus-kas/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HomeDTO.HomeChart>> listArusKas(@RequestBody HomeDTO.HomeParameter params) {
        List<HomeDTO.HomeChart> list = homeService.listArusKas(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/anggaran-tahunan/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<List<HomeDTO.HomeChart>>> listAnggaranTahunan(@RequestBody HomeDTO.HomeParameter params) {
        List<List<HomeDTO.HomeChart>> list = homeService.listAnggaranTahunan(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/roi-roa/get")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<HomeDTO.HomeChart>> listROIROA(@RequestBody HomeDTO.HomeParameter params) {
        List<HomeDTO.HomeChart> list = homeService.listROIROA(params);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPDF(
            @RequestBody Map<String, Object> body
    ) {
        String chart = body.get("chart").toString().replace("data:image/png;base64,", "");
        InputStream chartStream = new ByteArrayInputStream(org.apache.commons.codec.binary.Base64.decodeBase64(chart.getBytes()));
        Map<String, Object> params = new HashMap<>();
        params.put("chart", chartStream);
        params.put("namaLaporan", body.get("namaLaporan").toString());
        log.info("Chart: {}, Nama Laporan: {}", params.get("chart"), params.get("namaLaporan"));

        byte[] bytes = homeService.exportPDF(params);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

package id.co.dapenbi.accounting.controller.laporan;

import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.enums.NamaLaporan;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanOjkService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/laporan-ojk")
public class LaporanOJKController {

    @Autowired
    private LaporanOjkService service;

    @Autowired
    private TahunBukuService tahunBukuService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/laporan-ojk");
        modelAndView.addObject(
                "namaLaporanList",
                new ArrayList<>(EnumSet.allOf(NamaLaporan.class)));
        return modelAndView;
    }

    @GetMapping("/datatable")
    public ResponseEntity<TableLapOjk<?>> getDatatable(
            @RequestParam String namaLap,
            @RequestParam String periode,
            @RequestParam String tahun
    ) {
        Optional<TahunBuku> tahunBuku = tahunBukuService.findByTahun(Integer.valueOf(tahun));
        log.info("kodeTahunBuku: {}, kodePeriode: {}", tahunBuku.get().getKodeTahunBuku(), periode);
        return ResponseEntity.ok(service.getData(namaLap, periode, tahunBuku.get().getKodeTahunBuku()));
    }

    @GetMapping("/proses")
    public ResponseEntity<?> doProses(
            @RequestParam String namaLaporan,
            @RequestParam String kodePeriode,
            @RequestParam String kodeTahunBuku,
            Principal principal) throws Exception {
        return ResponseEntity.ok(service.proses(namaLaporan, kodePeriode, kodeTahunBuku, principal));
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestParam String namaLaporan,
            @RequestParam String periode,
            @RequestBody List<Object> objects) throws ParseException {

        Map<String, Object> params = new HashMap();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);
        params.put("periode", dateFormat.format(_periode));

        byte[] bytes = service.exportPdf(params, namaLaporan, objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }


    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestParam String namaLaporan,
            @RequestParam String periode,
            @RequestBody List<Object> objects) throws ParseException {

        Map<String, Object> params = new HashMap();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);
        params.put("periode", dateFormat.format(_periode));

        byte[] bytes = service.exportExcel(params, namaLaporan, objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @GetMapping("/generate-excel")
    public ResponseEntity<?> generateExel(
            @RequestParam String periode,
            @RequestParam String tahun
    ) throws IOException {
        String kodeTahunBuku;
        Optional<TahunBuku> tahunBuku = tahunBukuService.findByTahun(Integer.valueOf(tahun));
        if (tahunBuku.isPresent()) kodeTahunBuku = tahunBuku.get().getKodeTahunBuku();
        else return ResponseEntity.badRequest().build();

        Map<String, Object> map = new HashMap<>();
        map.put("data", service.generateExcel(periode, kodeTahunBuku));
        map.put("fileName", "laporan-ojk.xlsx");
        return ResponseEntity.ok().body(map);
    }
}

package id.co.dapenbi.accounting.controller.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RealisasiDTO;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan.RealisasiService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("akunting/laporan")
public class RealisasiController {

    @Autowired
    private RealisasiService service;

    @Autowired
    private RekeningService rekeningService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("/realisasi-pendapatan")
    public ModelAndView showPendapatan() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/realisasiPendapatan");
        modelAndView.addObject("rekeningList", rekeningService.findByTipeRekening("PENDAPATAN"));
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @GetMapping("/realisasi-pengeluaran")
    public ModelAndView showPengeluaran() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/realisasiPengeluaran");
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }


    @PostMapping(value = "/realisasi-pendapatan/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcelPendapatan(
            @RequestBody List<RealisasiDTO> request,
            @RequestParam String periode,
            @RequestParam String judul,
            @RequestParam String bulan,
            @RequestParam String bulanMin1,
            @RequestParam Integer idRekening,
            @RequestParam String ttd,
            @RequestParam String tanggal
    ) throws ParseException {
        Map<String, Object> params = new HashMap();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);

        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);
        params.put("periode", dateFormat.format(_periode));
        params.put("judul", judul);

        byte[] bytes = service.exportExcel(params, request, "PENDAPATAN", bulan, bulanMin1, idRekening, "PENDAPATAN");
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/realisasi-pendapatan/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdfPendapatan(
            @RequestBody List<RealisasiDTO> request,
            @RequestParam String periode,
            @RequestParam String judul,
            @RequestParam String bulan,
            @RequestParam String bulanMin1,
            @RequestParam Integer idRekening,
            @RequestParam String ttd,
            @RequestParam String tanggal) throws ParseException {
        Map<String, Object> params = new HashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);

        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);
        params.put("periode", dateFormat.format(_periode));
        params.put("judul", judul);

        byte[] bytes = service.exportPdf(params, request, "PENDAPATAN", bulan, bulanMin1, idRekening, "PENDAPATAN");
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/realisasi-pengeluaran/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcelPengeluaran(
            @RequestBody List<RealisasiDTO> request,
            @RequestParam String tipeRekening,
            @RequestParam String periode,
            @RequestParam String judul,
            @RequestParam String bulan,
            @RequestParam String bulanMin1,
            @RequestParam Integer idRekening,
            @RequestParam String ttd,
            @RequestParam String tanggal) throws ParseException {
        Map<String, Object> params = new HashMap();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);

        log.info("ttd {}, tanggal {}", ttd, tanggal);
        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);
        params.put("periode", dateFormat.format(_periode));
        params.put("judul", judul);

        byte[] bytes = service.exportExcel(params, request, "PENGELUARAN", bulan, bulanMin1, idRekening, tipeRekening);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/realisasi-pengeluaran/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdfPengeluaran(
            @RequestBody List<RealisasiDTO> request,
            @RequestParam String tipeRekening,
            @RequestParam String periode,
            @RequestParam String judul,
            @RequestParam String bulan,
            @RequestParam String bulanMin1,
            @RequestParam Integer idRekening,
            @RequestParam String ttd,
            @RequestParam String tanggal) throws ParseException {
        Map<String, Object> params = new HashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date _periode = formatter.parse(periode);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);

        log.info("ttd {}, tanggal {}", ttd, tanggal);
        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);
        params.put("periode", dateFormat.format(_periode));
        params.put("judul", judul);

        byte[] bytes = service.exportPdf(params, request, "PENGELUARAN", bulan, bulanMin1, idRekening, tipeRekening);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/realisasi/datatables")
    @ResponseBody
    public DataTablesResponse<RealisasiDTO> datatables(@RequestBody RealisasiDTO.DatatablesBody payload) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getRealisasiDTO()
                ),
                payload.getSearch().getValue()
        );
    }
}

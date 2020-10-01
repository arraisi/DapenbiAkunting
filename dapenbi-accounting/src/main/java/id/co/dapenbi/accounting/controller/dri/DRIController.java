package id.co.dapenbi.accounting.controller.dri;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.dri.DRIDTO;
import id.co.dapenbi.accounting.service.impl.dri.DRIService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import id.co.dapenbi.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/akunting/dri")
public class DRIController {

    @Autowired
    private DRIService driService;

    @Autowired
    private RekeningService rekeningService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("/sementara")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/dri/driSementara");
        modelAndView.addObject("listRekening", rekeningService.findAllByOrder());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @GetMapping("/dri-{no}")
    public ModelAndView showPageDRI(@PathVariable("no") String no) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/dri/dri");
        modelAndView.addObject("no", no);
        modelAndView.addObject("listRekening", rekeningService.findAllByOrder());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getDRISementaraDatatables")
    @ResponseBody
    public DataTablesResponse<DRIDTO.DRIDebitKredit> sementara(@RequestBody DRIDTO.DRIDatatablesBody payload) {
        DRIDTO.DRISementara driSementara = new DRIDTO.DRISementara();
        driSementara.setTglPeriode(payload.getDriSementara().getTglPeriode());
        driSementara.setIdRekening(payload.getDriSementara().getIdRekening());
        driSementara.setKodeRekening(payload.getDriSementara().getKodeRekening());
        driSementara.setSaldoNormal("");
        return driService.sementara(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        driSementara
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getDRIDatatables")
    @ResponseBody
    public DataTablesResponse<DRIDTO.DRIDebitKredit> dri(@RequestBody DRIDTO.DRIDatatablesBody payload) {
        DRIDTO.DRI dri = new DRIDTO.DRI();
        dri.setKodeDRI(payload.getDri().getKodeDRI());
        dri.setTglPeriode(payload.getDri().getTglPeriode());
        dri.setIdRekening(payload.getDri().getIdRekening());
        dri.setKodeRekening(payload.getDri().getKodeRekening());
        return driService.dri(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        dri
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody List<Object> objects,
            @RequestParam String periode,
            @RequestParam String ttd,
            @RequestParam String tanggal
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);

//        byte[] bytes = driService.exportPdfSementara(params, "/jasper/laporan_dri_sementara.jasper", objects);
        byte[] bytes = driService.exportPDFSementara(params, "/jasper/laporan_dri_sementara_new.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestBody List<Object> objects,
            @RequestParam String periode,
            @RequestParam String ttd,
            @RequestParam String tanggal
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("kepalaDivisi", ttd);
        params.put("tanggal", tanggal);

//        byte[] bytes = driService.exportExcelSementara(params, "/jasper/laporan_dri_sementara.jasper", objects);
        byte[] bytes = driService.exportEXCELSementara(params, "/jasper/laporan_dri_sementara_new.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-pdf-dri", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdfDRI(
            @RequestBody List<Object> objects,
            @RequestParam String periode,
            @RequestParam String kodeDRI,
            @RequestParam String ttd,
            @RequestParam String jenisDRI,
            @RequestParam String tanggal
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("datePeriode", periode);
        params.put("kodeDRI", kodeDRI);
        params.put("kepalaDivisi", ttd);
        params.put("jenisDRI", jenisDRI);
        params.put("tanggal", tanggal);

//        byte[] bytes = driService.exportPdfDRI(params, "/jasper/laporan_dri_sementara.jasper", objects);
        byte[] bytes = driService.exportPdfDRI(params, "/jasper/laporan_dri_sementara_new.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel-dri", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcelDRI(
            @RequestBody List<Object> objects,
            @RequestParam String periode,
            @RequestParam String kodeDRI,
            @RequestParam String ttd,
            @RequestParam String jenisDRI,
            @RequestParam String tanggal
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("datePeriode", periode);
        params.put("kodeDRI", kodeDRI);
        params.put("kepalaDivisi", ttd);
        params.put("jenisDRI", jenisDRI);
        params.put("tanggal", tanggal);

//        byte[] bytes = driService.exportExcelDRI(params, "/jasper/laporan_dri_sementara.jasper", objects);
        byte[] bytes = driService.exportExcelDRI(params, "/jasper/laporan_dri_sementara_new.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

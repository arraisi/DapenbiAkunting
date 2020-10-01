package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.PayloadDTO;
import id.co.dapenbi.accounting.dto.laporan.PengantarWarkatDTO;
import id.co.dapenbi.accounting.service.impl.master.LookupMasterService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import id.co.dapenbi.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/akunting/transaksi/laporan-pengantar-warkat")
@Slf4j
public class PengantarWarkatController {

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private LookupMasterService lookupMasterService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/pengantarWarkat");
//        modelAndView.addObject("lookup", lookupMasterService.findByKodeLookup("PENGANTAR_WARKAT").get());
        return modelAndView;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<PengantarWarkatDTO> pengantarWarkatDTODatatables(@RequestBody PayloadDTO.Body<PengantarWarkatDTO> payload) {
//        log.info("Object: {}", payload.getValue());
        return warkatService.pengantarWarkatDTODatatables(
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
    public ResponseEntity<String> exportPDF(
            @RequestBody List<Object> object,
            @RequestParam String periode,
            @RequestParam String tanggal,
            @RequestParam String tanggalPengantarWarkat
    ) throws ParseException {
        Map<String, Object> params = new HashMap<>();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("tanggal", tanggal);
        params.put("tanggalPengantarWarkat", tanggalPengantarWarkat);

        byte[] bytes = warkatService.exportPengantarWarkatPDF(object, params, "/jasper/laporan_pengantar_warkat.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportEXCEL(
            @RequestBody List<Object> object,
            @RequestParam String periode,
            @RequestParam String tanggal,
            @RequestParam String tanggalPengantarWarkat
    ) throws ParseException {
        Map<String, Object> params = new HashMap<>();
        params.put("periode", DateUtil.stringToDate(periode));
        params.put("tanggal", tanggal);
        params.put("tanggalPengantarWarkat", tanggalPengantarWarkat);

        byte[] bytes = warkatService.exportPengantarWarkatEXCEL(object, params, "/jasper/laporan_pengantar_warkat.jasper");
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

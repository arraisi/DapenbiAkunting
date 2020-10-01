package id.co.dapenbi.accounting.controller.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan.RekeningIndividualService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.core.util.DateUtil;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("akunting/laporan/buku-besar")
public class RekeningIndividualController {

    @Autowired
    private RekeningIndividualService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showWarkat() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/laporan-keuangan/rekeningIndividual");
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<RekeningIndividualDTO> datatables(@RequestBody RekeningIndividualDTO.DatatablesBody payload) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getRekeningIndividualDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping(value = "/all/findExportData")
    public ResponseEntity<?> findExportData(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam String kodeRekening
    ) {
        try {
            final List<RekeningIndividualDTO.Header> rekeningList = service.findExportData(startDate, endDate, kodeRekening);
            if (kodeRekening.isEmpty()) {
                return new ResponseEntity<>(rekeningList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.singletonList(rekeningList.get(0)), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        }
    }

    @GetMapping(value = "/all/findRekeningByRange")
    public ResponseEntity<?> findRekeningByRange(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam String kodeRekening
    ) {
        try {
            final List<RekeningIndividualDTO.Header> rekeningList = service.findRekeningByRange(startDate, endDate, kodeRekening);
            return new ResponseEntity<>(rekeningList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        }
    }


    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam String kodeRekening
    ) {
        try {
            final List<RekeningIndividualDTO.Header> rekeningList = service.findRekeningByRange(startDate, endDate, kodeRekening);

            Map<String, Object> params = new HashMap<>();

            byte[] bytes = service.exportPdf(params, kodeRekening.isEmpty() ? rekeningList : Collections.singletonList(rekeningList.get(0)));

            String encode = Base64.getEncoder().encodeToString(bytes);
            return ResponseEntity.ok(encode);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestParam String startDate, @RequestParam String endDate, @RequestParam String kodeRekening
    ) {
        try {
            final List<RekeningIndividualDTO.Header> rekeningList = service.findRekeningByRange(startDate, endDate, kodeRekening);

            Map<String, Object> params = new HashMap<>();

            byte[] bytes = service.exportExcel(params, kodeRekening.isEmpty() ? rekeningList : Collections.singletonList(rekeningList.get(0)));
            String encode = Base64.getEncoder().encodeToString(bytes);

            return ResponseEntity.ok(encode);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }
}

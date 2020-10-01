package id.co.dapenbi.accounting.controller.laporan.LaporanKeuangan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuangan.JurnalIndividualService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("akunting/laporan/laporan-individual")
public class JurnalIndividualController {

    @Autowired
    private JurnalIndividualService service;

    @Autowired
    private RekeningService rekeningService;

    @GetMapping("")
    public ModelAndView showWarkat() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/laporan-keuangan/jurnalIndividual");
        modelAndView.addObject("rekeningList", rekeningService.findAll());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<JurnalIndividualDTO> datatables(@RequestBody JurnalIndividualDTO.DatatablesBody payload) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getJurnalIndividualDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<?> findAll() {
        List<JurnalIndividualDTO> list = this.service.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/listRekening")
    @ResponseBody
    public ResponseEntity<?> findListRekening() {
        List<JurnalIndividualDTO.Rekening> list = this.service.findListRekening();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/listByDate")
    public ResponseEntity<?> findAllByDate(@RequestBody JurnalIndividualDTO jurnalIndividual) {
        try {
            List<JurnalIndividualDTO> list = this.service.findAllByDate(jurnalIndividual);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody List<JurnalIndividualDTO> jurnalIndividuals,
            @RequestParam String periode,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Integer idRekening
    ) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date _startDate = formatter.parse(startDate);
        Date _endDate = formatter.parse(endDate);

        Map<String, Object> params = new HashMap<>();
        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);
        params.put("startDate", dateFormat.format(_startDate));
        params.put("endDate", dateFormat.format(_endDate));

        final JurnalIndividualDTO summaryValue = service.getSummaryValue(startDate, endDate, idRekening);
        params.put("totalWarkatPembukuan", summaryValue.getTotalWarkatPembukuan());
        params.put("totalMutasiDebet", summaryValue.getTotalMutasiDebet());
        params.put("totalMutasiKredit", summaryValue.getTotalMutasiKredit());

        byte[] bytes = service.exportPdf(params, jurnalIndividuals);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestBody List<JurnalIndividualDTO> jurnalIndividuals,
            @RequestParam String periode,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Integer idRekening) {
        Map<String, Object> params = new HashMap();
        params.put("periode", periode);

        final JurnalIndividualDTO summaryValue = service.getSummaryValue(startDate, endDate, idRekening);
        params.put("totalWarkatPembukuan", summaryValue.getTotalWarkatPembukuan());
        params.put("totalMutasiDebet", summaryValue.getTotalMutasiDebet());
        params.put("totalMutasiKredit", summaryValue.getTotalMutasiKredit());

        byte[] bytes = service.exportExcel(params, jurnalIndividuals);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }
}

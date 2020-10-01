package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RealisasiDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.mapper.WarkatMapper;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.TransaksiService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatJurnalService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("")
public class WarkatController {

    @Autowired
    private WarkatService service;

    @Autowired
    private WarkatJurnalService warkatJurnalService;

    @Autowired
    private TransaksiService transaksiService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("/akunting/transaksi/saldo/saldo-warkat")
    public ModelAndView showWarkat() {
        final List<Transaksi> listJenisTransaksi = transaksiService.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listJenisTransaksi", listJenisTransaksi);
        modelAndView.setViewName("transaksi/saldoWarkat");
        return modelAndView;
    }

    @GetMapping("/akunting/laporan/tolak-warkat")
    public ModelAndView showWarkatLog() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/laporanTolakWarkat");
        return modelAndView;
    }

    @GetMapping("/akunting/transaksi/saldo/validasi-warkat")
    public ModelAndView showValidasiWarkat() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/validasiWarkat");
        return modelAndView;
    }

    @GetMapping("/akunting/transaksi/saldo/saldo-pre-approval")
    public ModelAndView showValidasiPreApproval() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/validasiPreApproval");
        return modelAndView;
    }

    @GetMapping("/akunting/transaksi/saldo/saldo-final-approval")
    public ModelAndView showValidasiFinalApproval() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/validasiFinalApproval");
        return modelAndView;
    }

    @PostMapping("/api/akunting/transaksi/saldo/validasi")
    public ResponseEntity<?> validasi(@RequestBody @Valid WarkatDTO request, Principal principal) {
        final Boolean statusOpen = service.checkStatusOpen();
        final Optional<Warkat> warkat = service.findById(request.getNoWarkat());
        if (!statusOpen && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_PAJAK") && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_TRANSAKSI")) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
        } else if (warkat.isPresent()) {
            if (warkat.get().getStatusData().equals(request.getStatusData())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }
        request.setCreatedBy(principal.getName());
        if (request.getStatusData().equalsIgnoreCase("VALID")) {
            String response = service.validasi(request, "VALIDASI"); // VALIDASI
            if (response.equalsIgnoreCase("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            try {
                service.updateStatusWarkat(request, "REJECT-WARKAT");
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
            }
        }
    }

    @PostMapping("/akunting/transaksi/saldo/validasi/pre-approval")
    public ResponseEntity<?> validasiPreApproval(@RequestBody @Valid WarkatDTO request, Principal principal) {
        final Boolean statusOpen = service.checkStatusOpen();
        final Optional<Warkat> warkat = service.findById(request.getNoWarkat());
        if (!statusOpen && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_PAJAK") && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_TRANSAKSI")) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
        } else if (warkat.isPresent()) {
            if (warkat.get().getStatusData().equals(request.getStatusData())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }
        request.setCreatedBy(principal.getName());
        if (request.getStatusData().equalsIgnoreCase("PA")) {
            String response = service.validasiPreApproval(request, "APPROVE-PA"); // VALIDASI PA
            if (response.equalsIgnoreCase("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            String response = service.rejectPreApproval(request, "REJECT-PA"); // REJECT & RETRIEVE SALDO CURRENT
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/akunting/transaksi/saldo/validasi/final-approval")
    public ResponseEntity<?> validasFinalApproval(@RequestBody @Valid WarkatDTO request, Principal principal) {
        final Boolean statusOpen = service.checkStatusOpen();
        final Optional<Warkat> warkat = service.findById(request.getNoWarkat());
        if (!statusOpen && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_PAJAK") && !request.getJenisWarkat().equalsIgnoreCase("JURNAL_TRANSAKSI")) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
        } else if (warkat.isPresent()) {
            if (warkat.get().getStatusData().equals(request.getStatusData())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }
        request.setCreatedBy(principal.getName());
        if (request.getStatusData().equalsIgnoreCase("FA")) {
            final String nuwp = service.generateNuwp();
            String response = service.validasiFinalApproval(request, "APPROVE-FA", nuwp); // VALIDASI FA
            if (response.equalsIgnoreCase("OK")) {
                return new ResponseEntity<>(nuwp, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            String response = service.rejectFinalApproval(request, "REJECT-FA");
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
    }

    @GetMapping("/api/akunting/transaksi/saldo/generateNoWarkat")
    public ResponseEntity<String> generateNoWarkat(@RequestParam String satker, Principal principal) {
        String noWarkat = service.getNoWarkat(satker);
        if (noWarkat != null) {
            return new ResponseEntity<>(noWarkat, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/transaksi/saldo/statusWarkatList")
    public ResponseEntity<?> getStatusWarkatList() {
        List<MSTLookUp> statusWarkatList = service.getStatusWarkatList();
        if (!statusWarkatList.isEmpty()) {
            return new ResponseEntity<>(statusWarkatList, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/api/akunting/transaksi/saldo/saldo-warkat/and/jurnals")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveWarkatAndJurnals(@RequestBody @Valid WarkatDTO request, Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (request.getStatusData().equalsIgnoreCase("SUBMIT")) {
            if (pengaturanSistem.isPresent()) {
                if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("O"))
                {         // CHECK STATUS OPEN
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String today = format.format(request.getTglTransaksi());
                    String tglTransaksi = format.format(pengaturanSistem.get().getTglTransaksi());
                    if (!today.equalsIgnoreCase(tglTransaksi)) {                                        // CHECK TANGGAL TRANSAKSI
                        return new ResponseEntity<>("Transaksi sebelumnya belum ditutup.", HttpStatus.ACCEPTED);
                    }
                } else
                    return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
            }
        }

        try {
            Warkat warkat = service.update(pengaturanSistem.get(), request, principal != null ? principal.getName() : "PTR");
            return new ResponseEntity<>(warkat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    @DeleteMapping("/api/akunting/transaksi/saldo/saldo-warkat/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Warkat warkat, Principal principal) {
        service.delete(warkat.getNoWarkat(), principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/transaksi/saldo/saldo-warkat/datatables")
    @ResponseBody
    public DataTablesResponse<WarkatDTO> datatables(
            @RequestBody WarkatDTO.DatatablesBody payload
    ) {
        final DataTablesResponse<WarkatDTO> datatables = service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getWarkatDTO()
                ),
                payload.getSearch().getValue()
        );
        return datatables;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/akunting/transaksi/saldo/saldo-warkat/datatables/log")
    @ResponseBody
    public DataTablesResponse<WarkatDTO> datatablesLog(
            @RequestBody WarkatDTO.DatatablesBody payload, Principal principal
    ) {
        payload.getWarkatDTO().setCreatedBy(principal.getName());
        return service.datatablesLog(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getWarkatDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/akunting/transaksi/saldo/saldo-warkat/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody WarkatDTO warkat
    ) {
        List<WarkatDTO> data = new ArrayList<>();
        warkat.setDebitList(warkatJurnalService.findByNoWarkatAndSaldoNormal(warkat.getNoWarkat(), "D"));
        warkat.setKreditList(warkatJurnalService.findByNoWarkatAndSaldoNormal(warkat.getNoWarkat(), "K"));
        data.add(warkat);

        Map<String, Object> params = new HashMap<>();
        byte[] bytes = service.exportPdf(params, data);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/akunting/transaksi/saldo/saldo-warkat/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestBody WarkatDTO warkat) {
        List<WarkatDTO> data = new ArrayList<>();
        warkat.setDebitList(warkatJurnalService.findByNoWarkatAndSaldoNormal(warkat.getNoWarkat(), "D"));
        warkat.setKreditList(warkatJurnalService.findByNoWarkatAndSaldoNormal(warkat.getNoWarkat(), "K"));
        data.add(warkat);

        Map<String, Object> params = new HashMap<>();
        byte[] bytes = service.exportExcel(params, data);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }


    @PostMapping(value = "/akunting/transaksi/saldo/saldo-warkat/log/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcelLog(@RequestBody List<WarkatDTO> request) {
        byte[] bytes = service.exportExcelLog(request);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/akunting/transaksi/saldo/saldo-warkat/log/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdfLog(@RequestBody List<WarkatDTO> request) {
        byte[] bytes = service.exportPdfLog(request);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }
}

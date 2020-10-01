package id.co.dapenbi.accounting.controller.sse;

import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.service.impl.sse.SubSequenceEventService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/sse")
public class SubSequenceEventController {

    @Autowired
    WarkatService warkatService;

    @Autowired
    SubSequenceEventService service;

    @GetMapping("/subsequentevent")
    public ModelAndView showSSE() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/subSequenceEvent");
        return modelAndView;
    }

    @GetMapping("/validasi-sse")
    public ModelAndView showValidasiSSE() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/validasiSubSequenceEvent");
        return modelAndView;
    }

    @GetMapping("/approval-sse")
    public ModelAndView showPreApprovalSSE() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/preApprovalSubSequenceEvent");
        return modelAndView;
    }

    @GetMapping("/final-approval-sse")
    public ModelAndView showFinalApprovalSSE() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/finalApprovalSubSequenceEvent");
        return modelAndView;
    }

    @GetMapping("/jenisWarkat")
    public ResponseEntity<List<MSTLookUp>> findJenisWarkat() {
        return new ResponseEntity<>(warkatService.findJenisWarkat(), HttpStatus.OK);
    }

    @PostMapping("/validasi")
    public ResponseEntity<?> validasi(@RequestBody @Valid WarkatDTO request, Principal principal) {

        final Boolean statusOpen = warkatService.checkStatusOpen();

        final Optional<Warkat> warkat = warkatService.findById(request.getNoWarkat());

        if (statusOpen) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di tutup.", HttpStatus.ACCEPTED);
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
                warkatService.updateStatusWarkat(request, "REJECT-WARKAT");
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
            }
        }
    }

    @PostMapping("/pre-approval")
    public ResponseEntity<?> validasiPreApproval(@RequestBody @Valid WarkatDTO request, Principal principal) {

        final Boolean statusOpen = warkatService.checkStatusOpen();

        final Optional<Warkat> warkat = warkatService.findById(request.getNoWarkat());

        if (statusOpen) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di tutup.", HttpStatus.ACCEPTED);
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
            String response = service.reject(request, "REJECT-PA"); // REJECT & RETRIEVE SALDO CURRENT
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/final-approval")
    public ResponseEntity<?> validasFinalApproval(@RequestBody @Valid WarkatDTO request, Principal principal) {

        final Boolean statusOpen = warkatService.checkStatusOpen();

        final Optional<Warkat> warkat = warkatService.findById(request.getNoWarkat());

        if (statusOpen) {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di tutup.", HttpStatus.ACCEPTED);
        } else if (warkat.isPresent()) {
            if (warkat.get().getStatusData().equals(request.getStatusData())) {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }

        request.setCreatedBy(principal.getName());

        if (request.getStatusData().equalsIgnoreCase("FA")) {

            String response = service.validasiFinalApproval(request, "APPROVE-FA"); // VALIDASI FA

            // RETURN VALUE
            if (response.equalsIgnoreCase("OK")) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        } else {

            String response = service.reject(request, "REJECT-FA");
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        }
    }
}

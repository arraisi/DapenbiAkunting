package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.mapper.TransaksiJurnalMapper;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.JurnalPajakService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatJurnalService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatLogService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/akunting/transaksi/jurnal-transaksi")
@Slf4j
public class JurnalTransaksiController {

    @Autowired
    private WarkatJurnalService warkatJurnalService;

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private WarkatLogService warkatLogService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showJurnalTransaksi() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/jurnalTransaksi");
        modelAndView.addObject("jenisJurnalList", warkatService.findJenisJurnal());
        return modelAndView;
    }

    @PostMapping("/saldo-warkat/and/jurnals")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveWarkatAndJurnals(@RequestBody @Valid WarkatDTO request, Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (request.getStatusData().equalsIgnoreCase("SUBMIT")) {
            if (pengaturanSistem.isPresent()) {
                if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("C")) {         // CHECK STATUS OPEN
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String today = format.format(new Date());
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
            Warkat warkat = warkatService.update(pengaturanSistem.get(), request, principal != null ? principal.getName() : "PTR");
            return new ResponseEntity<>(warkat, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/validasi")
    public ResponseEntity<?> validasi(@RequestBody @Valid WarkatDTO request, Principal principal) {
        request.setCreatedBy(principal.getName());
        if (request.getStatusData().equalsIgnoreCase("FA")) {
            String response = warkatService.validasiJurnal(request, "APPROVE-FA"); // VALIDASI
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            String response = warkatService.rejectFinalApproval(request, "REJECT-FA");
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
    }
}

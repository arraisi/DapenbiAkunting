package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.JurnalBiayaService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatJurnalService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi/")
public class JurnalBiayaController {

    @Autowired
    private JurnalBiayaService service;

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("jurnal-biaya")
    public ModelAndView showJurnalBiaya() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/jurnalBiaya");
        return modelAndView;
    }

    @GetMapping("validasi-jurnal-biaya")
    public ModelAndView showValidasiJurnalBiaya() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/validasiJurnalBiaya");
        return modelAndView;
    }

    @PostMapping("jurnal-biaya/validasi")
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

    @PostMapping("jurnal-biaya/saldo-warkat/and/jurnals")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveWarkatAndJurnals(@RequestBody @Valid WarkatDTO request, Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (pengaturanSistem.isPresent()) {
            if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("O")) {
                try {
                    Warkat warkat = warkatService.update(pengaturanSistem.get(), request, principal.getName());
                    return new ResponseEntity<>(warkat, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
                }
            }
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum di buka.", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Tidak ada transaksi aktif", HttpStatus.ACCEPTED);
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "jurnal-biaya/datatables")
    @ResponseBody
    public DataTablesResponse<JurnalBiayaDTO> datatables(
            @RequestBody JurnalBiayaDTO.DatatablesBody payload
    ) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getJurnalBiayaDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping("jurnal-biaya/findDebits")
    @ResponseBody
    public ResponseEntity<List<WarkatJurnal>> findJurnalBiayaDebits() {
        List<WarkatJurnal> jurnalBiayaDebits = this.service.findJurnalBiayaDebits();
        return new ResponseEntity<>(jurnalBiayaDebits, HttpStatus.OK);
    }
}

package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPendapatanDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.JurnalPendapatanService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatJurnalService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatLogService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
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

@Controller
@RequestMapping("/akunting/transaksi/jurnal-pendapatan")
public class JurnalPendapatanController {

    @Autowired
    private JurnalPendapatanService jurnalPendapatanService;

    @Autowired
    private WarkatJurnalService warkatJurnalService;

    @Autowired
    private WarkatLogService warkatLogService;

    @Autowired
    private WarkatService warkatService;


    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/jurnalPendapatan");
        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid Warkat warkat, Principal principal) {
        warkat.setCreatedBy(principal.getName());
        List<WarkatJurnal> warkatJurnals = warkat.getWarkatJurnals();
        Warkat data = jurnalPendapatanService.save(warkat);
        for (WarkatJurnal detail : warkatJurnals) {
            detail.setNoWarkat(data);
            detail.setCreatedBy(principal.getName());
            detail.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            warkatJurnalService.save(detail);
        }
        WarkatLog warkatLog = new WarkatLog();
        warkatLog.setNoWarkat(data.getNoWarkat());
        warkatLog.setAktivitas("CREATE / UPDATE");
        warkatLog.setCreatedBy(principal.getName());
        warkatLog.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        warkatLogService.save(warkatLog);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/findById")
    public ResponseEntity<Warkat> findById(@PathVariable("id") String id) {
        Optional<Warkat> data = jurnalPendapatanService.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody @Valid Warkat warkat, Principal principal) {
        warkat.setUpdatedBy(principal.getName());
        List<WarkatJurnal> warkatJurnals = warkat.getWarkatJurnals();
        Warkat data = jurnalPendapatanService.update(warkat);
        warkatJurnalService.deleteByIdWarkat(warkat.getNoWarkat());
        for (WarkatJurnal detail : warkatJurnals) {
            detail.setNoWarkat(data);
            detail.setUpdatedBy(principal.getName());
            detail.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
            warkatJurnalService.querySave(detail);
        }
        WarkatLog warkatLog = new WarkatLog();
        warkatLog.setNoWarkat(data.getNoWarkat());
        warkatLog.setAktivitas("CREATE / UPDATE");
        warkatLog.setCreatedBy(principal.getName());
        warkatLog.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        warkatLogService.save(warkatLog);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update-status-data")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateStatusData(@RequestBody @Valid Warkat warkat) {
        jurnalPendapatanService.updateStatudData(warkat);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Warkat warkat) {
        jurnalPendapatanService.delete(warkat.getNoWarkat());
        warkatJurnalService.deleteByIdWarkat(warkat.getNoWarkat());
        warkatLogService.deleteByNoWarkat(warkat.getNoWarkat());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<JurnalPendapatanDTO> datatables(
            @RequestBody JurnalPendapatanDTO.DatatablesBody payload
    ) {
        return jurnalPendapatanService.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getJurnalPendapatanDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping("/saldo-warkat/and/jurnals")
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

    @GetMapping("/findKredits")
    @ResponseBody
    public ResponseEntity<List<WarkatJurnal>> findJurnalBiayaDebits() {
        List<WarkatJurnal> jurnalBiayaKredits = this.jurnalPendapatanService.findJurnalPendapatanKredits();
        return new ResponseEntity<>(jurnalBiayaKredits, HttpStatus.OK);
    }
}

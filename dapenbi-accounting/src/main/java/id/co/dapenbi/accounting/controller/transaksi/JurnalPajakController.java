package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalBiayaDTO;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.mapper.JurnalPajakMapper;
import id.co.dapenbi.accounting.mapper.TransaksiJurnalMapper;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.*;
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
@RequestMapping("/akunting/transaksi/jurnal-pajak")
@Slf4j
public class JurnalPajakController {

    @Autowired
    private JurnalPajakService jurnalPajakService;

    @Autowired
    private WarkatJurnalService warkatJurnalService;

    @Autowired
    private SaldoService saldoService;

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private WarkatLogService warkatLogService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showJurnalPajak() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/jurnalPajakNew");

        return modelAndView;
    }

    @PostMapping("/saldo-warkat/and/jurnals")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveWarkatAndJurnals(@RequestBody @Valid WarkatDTO request, Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        final Integer integer = saldoService.countByKodeDRI("1");
        if (request.getStatusData().equalsIgnoreCase("SUBMIT")) {
            if (pengaturanSistem.isPresent()) {
                if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("O")) {                                     // CHECK TANGGAL TRANSAKSI
                    return new ResponseEntity<>("Transaksi belum ditutup.", HttpStatus.ACCEPTED);
                }
            } else {
                return new ResponseEntity<>("Tidak ada transaksi pada hari ini.", HttpStatus.ACCEPTED);
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
        if (request.getStatusData().equalsIgnoreCase("VALID")) {
            String response = warkatService.validasiJurnal(request, "VALIDASI"); // VALIDASI
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } else {
            String response = warkatService.rejectFinalApproval(request, "REJECT-WARKAT");
            if (response.equals("OK")) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid Warkat warkat, Principal principal) {
        warkat.setCreatedBy(principal.getName());
        List<WarkatJurnal> warkatJurnals = warkat.getWarkatJurnals();
        Warkat data = jurnalPajakService.save(warkat);
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

    @GetMapping("/findById")
    public ResponseEntity<Warkat> findById(@RequestParam String id) {
        Optional<Warkat> data = jurnalPajakService.findById(id);
        if (data.isPresent()) {
            log.info("{}", data.get());
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
        Warkat data = jurnalPajakService.update(warkat);
        warkatJurnalService.deleteByIdWarkat(warkat.getNoWarkat());
        for (WarkatJurnal detail : warkatJurnals) {
//            log.info("{}", detail);
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
        jurnalPajakService.updateStatudData(warkat);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Warkat warkat) {
        jurnalPajakService.delete(warkat.getNoWarkat());
        warkatJurnalService.deleteByIdWarkat(warkat.getNoWarkat());
        warkatLogService.deleteByNoWarkat(warkat.getNoWarkat());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<JurnalPajakDTO> datatables(
            @RequestBody JurnalPajakDTO.DatatablesBody payload
    ) {
        return jurnalPajakService.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getJurnalPajakDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getTransaksiJurnalDatatables")
    @ResponseBody
    public DataTablesResponse<TransaksiJurnal> datatables(@RequestBody TransaksiJurnalDTO.DatatablesBody payload) {
        TransaksiJurnal transaksiJurnal = TransaksiJurnalMapper.INSTANCE.dtoToEntity(payload.getTransaksiJurnalDTO());
        log.info("{}", transaksiJurnal);
        return jurnalPajakService.transaksiJurnalDataTables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        transaksiJurnal
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getJurnalPajakDataTablesHibernate")
    @ResponseBody
    public DataTablesOutput<Warkat> datatablesHibernate(@RequestBody @Valid DataTablesInput input) {
        return jurnalPajakService.datatablesHibernate(input);
    }
}

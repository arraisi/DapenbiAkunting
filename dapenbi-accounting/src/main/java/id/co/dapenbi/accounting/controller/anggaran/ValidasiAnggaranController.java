package id.co.dapenbi.accounting.controller.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.Anggaran;
import id.co.dapenbi.accounting.entity.anggaran.AnggaranLog;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranLogService;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/akunting/anggaran/validasi-anggaran")
public class ValidasiAnggaranController {

    @Autowired
    private AnggaranService service;

    @Autowired
    private AnggaranLogService logService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("anggaran/validasiAnggaran");
        modelAndView.addObject("periodeList", service.findAllPeriode());
        modelAndView.addObject("tahunBukuList", tahunBukuService.listByStatusAktif());
        return modelAndView;
    }

    @PostMapping("/{noAnggaran}/approve")
    public ResponseEntity<?> approve(
            @PathVariable String noAnggaran,
            @RequestParam Integer idRekening,
            @RequestParam String kdTahunBuku,
            Principal principal) {
        Anggaran anggaran = service.updateStatusData(noAnggaran, "APPROVE", idRekening, kdTahunBuku.toString(), principal.getName());
        if (anggaran != null) {
            AnggaranLog anggaranLog = new AnggaranLog(anggaran, "VALIDASI");
            anggaranLog.setCreatedBy(principal.getName());
            anggaranLog.setCreatedDate(new Timestamp(new Date().getTime()));
            anggaranLog.setUpdatedBy(null);
            anggaranLog.setUpdatedDate(null);
            logService.save(anggaranLog);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{noAnggaran}/reject")
    public ResponseEntity<?> reject(
            @PathVariable String noAnggaran,
            @RequestParam String kdTahunBuku,
            Principal principal) {
        Anggaran anggaran = service.updateStatusData(noAnggaran, "REJECT", null, kdTahunBuku.toString(), principal.getName());
        if (anggaran != null) {
            AnggaranLog anggaranLog = new AnggaranLog(anggaran, "VALIDASI");
            anggaranLog.setCreatedBy(principal.getName());
            anggaranLog.setCreatedDate(new Timestamp(new Date().getTime()));
            anggaranLog.setUpdatedBy(null);
            anggaranLog.setUpdatedDate(null);
            logService.save(anggaranLog);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectWithDescription(@RequestBody @Valid Anggaran anggaran, Principal principal) {
        anggaran.setUpdatedBy(principal.getName());
        Anggaran data = service.rejectWithDescription(anggaran);
        if (anggaran != null) {
            AnggaranLog anggaranLog = new AnggaranLog(data, "VALIDASI");
            anggaranLog.setCreatedBy(principal.getName());
            anggaranLog.setCreatedDate(new Timestamp(new Date().getTime()));
            anggaranLog.setUpdatedBy(null);
            anggaranLog.setUpdatedDate(null);
            logService.save(anggaranLog);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<Anggaran> datatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return service.validasiDataTable(input);
    }
}

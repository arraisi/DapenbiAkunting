package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.service.impl.transaksi.BukaSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.MonitoringTransaksiService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi/monitoring-transaksi")
public class MonitoringTransaksiController {

    @Autowired
    private MonitoringTransaksiService service;

    @Autowired
    private BukaSistemService bukaSistemService;

    @Autowired
    private WarkatService warkatService;

    @GetMapping("")
    public ModelAndView showMonitoringPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/monitoringTransaksi");
        modelAndView.addObject("statusKerjaList", bukaSistemService.getStatusPemakai());
        modelAndView.addObject("statusDataList", service.findAllStatusData());
        return modelAndView;
    }

    @GetMapping("/default-value")
    public ResponseEntity<Warkat> getDefaultValue(Principal principal) {
        Warkat warkat = new Warkat();
        warkat.setIdOrg(bukaSistemService.getStatusPemakai(principal.getName()));
        warkat.setTglTransaksi(new Timestamp(new Date().getTime()));
        return ResponseEntity.ok(warkat);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public List<Warkat> getDataTable(
            @RequestBody Warkat warkat
    ) {
        return service.warkatList(warkat);
    }
}

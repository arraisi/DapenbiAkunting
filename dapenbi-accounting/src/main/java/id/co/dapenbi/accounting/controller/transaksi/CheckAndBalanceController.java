package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.CheckAndBalanceDTO;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.CheckAndBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi/check-and-balance")
public class CheckAndBalanceController {

    @Autowired
    private CheckAndBalanceService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/checkAndBalance");
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @GetMapping("/default-value")
    public ResponseEntity<CheckAndBalanceDTO.Value> getDefaultValue(Principal principal) {
        return ResponseEntity.ok(service.getDefaultValue(principal.getName()));
    }

    @PostMapping("/rekening-datatables")
    public ResponseEntity<List<CheckAndBalanceDTO.Rekening>> getDatatableValue(
            @RequestParam String status
    ) {
        return ResponseEntity.ok(service.getList(status));
    }
}

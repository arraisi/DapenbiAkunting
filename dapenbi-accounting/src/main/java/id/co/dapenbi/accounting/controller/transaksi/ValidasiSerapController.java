package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import id.co.dapenbi.accounting.service.impl.transaksi.ValidasiSerapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/akunting/transaksi/validasi-serap")
public class ValidasiSerapController {

    @Autowired
    private ValidasiSerapService validasiSerapService;

    @GetMapping("")
    public ModelAndView showValidasiSerap() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/validasiSerapAnggaran");
        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> save(@RequestBody @Valid ValidasiSerap request, Principal principal) {
        request.setCreatedBy(principal.getName());
        validasiSerapService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

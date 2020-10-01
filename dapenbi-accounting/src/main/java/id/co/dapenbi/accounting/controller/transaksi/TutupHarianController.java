package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.TutupHarianDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.TutupHarianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/akunting/transaksi/tutup-pembukuan-harian")
@Slf4j
public class TutupHarianController {

    @Autowired
    private TutupHarianService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showValidasiBudgetReview() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/tutup-harian");
        return modelAndView;
    }

    @GetMapping("/")
    public ResponseEntity<TutupHarianDTO> findTutupHarian() {
        TutupHarianDTO tutupHarian = service.findTutupHarian();
        if (tutupHarian != null) {
            return new ResponseEntity<>(tutupHarian, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody TutupHarianDTO request, Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (pengaturanSistem.isPresent()) {
            if (pengaturanSistem.get().getStatusOpen().equalsIgnoreCase("C")) {
                return new ResponseEntity<>("Transaksi sudah ditutup.", HttpStatus.ACCEPTED);
            }
        }

        try {
            return new ResponseEntity<>(service.post(request, principal), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

}

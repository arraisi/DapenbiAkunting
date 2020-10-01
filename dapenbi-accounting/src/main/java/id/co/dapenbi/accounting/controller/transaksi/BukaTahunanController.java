package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.BukaSistemDto;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.BukaSistemService;
import id.co.dapenbi.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi/buka-tahunan")
public class BukaTahunanController {

    @Autowired
    private BukaSistemService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPembukuan() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/bukaTahunan");
        return modelAndView;
    }

    @GetMapping("/lastDRI")
    public ResponseEntity<?> findLastDRI(@RequestParam String tglTransaksi) {
        try {
            String data = service.findLastDRI(tglTransaksi);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/buka")
    public ResponseEntity<?> bukaSistem(@RequestBody BukaSistemDto sistem) {
        final int i = service.bukaSistem(sistem);
        if (i == 1) {
            return new ResponseEntity<>(i, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}

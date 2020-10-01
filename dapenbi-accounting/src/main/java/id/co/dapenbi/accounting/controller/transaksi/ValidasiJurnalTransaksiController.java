package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/akunting/transaksi/validasi-jurnal-transaksi")
public class ValidasiJurnalTransaksiController {

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/validasiJurnalTransaksi");
        return modelAndView;
    }
}

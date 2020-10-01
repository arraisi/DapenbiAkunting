package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
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
@RequestMapping("/akunting/transaksi/validasi-jurnal-pajak")
public class ValidasiJurnalPajakController {

    @Autowired
    private WarkatLogService warkatLogService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/validasiJurnalPajakNew");
        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid WarkatLog warkatLog, Principal principal) {
        warkatLog.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        warkatLog.setCreatedBy(principal.getName());
        warkatLogService.save(warkatLog);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update-saldo-current")
    public ResponseEntity<?> updateSaldoCurrent(@RequestBody List<WarkatJurnal> warkatJurnals) {
        for(WarkatJurnal warkatJurnal : warkatJurnals) {
            SaldoCurrent saldoCurrent = saldoCurrentService.findByIdRekening(warkatJurnal.getIdRekening().getIdRekening()).get();
//            log.info("{}", saldoCurrent);
            BigDecimal saldoDebit = saldoCurrent.getSaldoDebet() == null ? new BigDecimal(0) : saldoCurrent.getSaldoDebet();
            BigDecimal saldoKredit = saldoCurrent.getSaldoKredit() == null ? new BigDecimal(0) : saldoCurrent.getSaldoKredit();
            BigDecimal saldoAwal = saldoCurrent.getSaldoAwal() == null ? new BigDecimal(0) : saldoCurrent.getSaldoAwal();
            saldoCurrent.setSaldoDebet(saldoDebit.add(warkatJurnal.getJumlahDebit()));
            saldoCurrent.setSaldoKredit(saldoKredit.add(warkatJurnal.getJumlahKredit()));
            saldoCurrent.setSaldoAkhir(saldoAwal.add(saldoCurrent.getSaldoDebet()).subtract(saldoCurrent.getSaldoKredit()));
            saldoCurrentService.updateSaldoCurrentWarkatJurnal(saldoCurrent);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.service.impl.transaksi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/akunting/transaksi/approval-serap")
@Slf4j
public class ApprovalSerapController {

    @Autowired
    private ValidasiSerapService validasiSerapService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private SerapDetailService serapDetailService;

    @Autowired
    private SaldoPAService saldoPAService;

    @Autowired
    private SaldoFAService saldoFAService;

    @GetMapping("")
    public ModelAndView showApprovalSerapAnggaran() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/approvalSerapAnggaran");
        return modelAndView;
    }

    @PostMapping("/update-saldo-current-serap")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateSaldoCurrentSerap(@RequestBody List<SerapDetail> serapDetails) {
        for(SerapDetail serapDetail : serapDetails) {
            SaldoCurrent saldoCurrent = saldoCurrentService.findByIdRekening(serapDetail.getRekening().getIdRekening()).get();
//            log.info("{}", saldoCurrent);
            BigDecimal serapTambah = saldoCurrent.getSerapTambah() == null ? new BigDecimal(0) : saldoCurrent.getSerapTambah();
            BigDecimal serapKurang = saldoCurrent.getSerapKurang() == null ? new BigDecimal(0) : saldoCurrent.getSerapKurang();
            BigDecimal saldoAnggaran = saldoCurrent.getSaldoAnggaran() == null ? new BigDecimal(0) : saldoCurrent.getSaldoAnggaran();
            saldoCurrent.setSerapTambah(serapTambah.add(serapDetail.getJumlahPenambah()));
            saldoCurrent.setSerapKurang(serapKurang.add(serapDetail.getJumlahPengurang()));
            saldoCurrent.setSaldoAnggaran(saldoAnggaran.add(serapDetail.getJumlahPenambah()).subtract(serapDetail.getJumlahPengurang()));
            saldoCurrentService.updateSaldoCurrentSerap(saldoCurrent);
            serapDetailService.updateSaldoAnggaran(serapDetail.getRekening().getIdRekening(), saldoCurrent.getSaldoAnggaran());
            saldoPAService.updateFromApprovalSerap(serapDetail);
            saldoFAService.updateFromApprovalSerap(serapDetail);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

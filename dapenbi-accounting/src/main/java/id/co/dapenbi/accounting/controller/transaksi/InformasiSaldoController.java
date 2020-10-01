package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.transaksi.InformasiSaldoDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.SaldoFA;
import id.co.dapenbi.accounting.entity.transaksi.SaldoPA;
import id.co.dapenbi.accounting.mapper.SaldoCurrentMapper;
import id.co.dapenbi.accounting.mapper.SaldoMapper;
import id.co.dapenbi.accounting.service.impl.parameter.JenisDRIService;
import id.co.dapenbi.accounting.service.impl.transaksi.InformasiSaldoService;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
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
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/akunting/transaksi/informasi-saldo")
@Slf4j
public class InformasiSaldoController {

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private InformasiSaldoService informasiSaldoService;

    @Autowired
    private JenisDRIService jenisDRIService;

    @GetMapping("")
    public ModelAndView showInformasiSaldo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/transaksi/informasiSaldo");
        modelAndView.addObject("jenisDRIList", jenisDRIService.getAll());

        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getSaldoCurrentDatatables")
    @ResponseBody
    public DataTablesResponse<SaldoCurrent> datatablesSaldoCurrent(@RequestBody InformasiSaldoDTO.InformasiSaldoDatatablesBody payload) {
        SaldoCurrent saldoCurrent = SaldoCurrentMapper.INSTANCE.InformasiSaldoDTOToSaldoCurrent(payload.getSaldoCurrent());
        return informasiSaldoService.datatablesSaldoCurrent(
               new DataTablesRequest<>(
                       payload.getDraw(),
                       payload.getLength(),
                       payload.getStart(),
                       payload.getOrder().get(0).getDir(),
                       payload.getOrder().get(0).getColumn(),
                       saldoCurrent
               ),
               payload.getSearch().getValue()
       );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getSaldoDatatables")
    @ResponseBody
    public DataTablesResponse<Saldo> datatablesSaldo(@RequestBody InformasiSaldoDTO.InformasiSaldoDatatablesBody payload) {
        Saldo saldo = SaldoMapper.INSTANCE.InformasiSaldoDTOToSaldo(payload.getSaldo());
        return informasiSaldoService.datatablesSaldo(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        saldo
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping("/saldo-aset-saldo-current/{param}")
    public ResponseEntity<?> totalSaldoCurrent(@PathVariable String param) {
        InformasiSaldoDTO.TotalSaldo totalSaldo = informasiSaldoService.totalSaldoCurrent(param);
        return ResponseEntity.ok(totalSaldo);
    }

    @GetMapping("/saldo-aset-saldo")
    public ResponseEntity<?> totalSaldo(@RequestParam String kodeDri, @RequestParam String tglSaldo) {
        InformasiSaldoDTO.TotalSaldo totalSaldo = informasiSaldoService.totalSaldo(kodeDri, tglSaldo);
        return ResponseEntity.ok(totalSaldo);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getPreApprovalDataTables")
    @ResponseBody
    public DataTablesOutput<SaldoPA> saldoPADataTablesOutput(@Valid @RequestBody DataTablesInput input) {
        return informasiSaldoService.saldoPADataTablesOutput(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getFinalApprovalDataTables")
    @ResponseBody
    public DataTablesOutput<SaldoFA> saldoFADataTablesOutput(@Valid @RequestBody DataTablesInput input) {
        return informasiSaldoService.saldoFADataTablesOutput(input);
    }

    @GetMapping("/total-aset-kewajiban-saldo-warkat")
    public ResponseEntity<?> totalAsetKewajibanSaldoWarkat(
            @RequestParam String tableName,
            @RequestParam String kodeDRI,
            @RequestParam String tglTransaksi
    ) {
        Map<String, BigDecimal> result =  informasiSaldoService.totalAsetKewajibanSaldoWarkat(tableName, kodeDRI, tglTransaksi);
        return ResponseEntity.ok(result);
    }
}

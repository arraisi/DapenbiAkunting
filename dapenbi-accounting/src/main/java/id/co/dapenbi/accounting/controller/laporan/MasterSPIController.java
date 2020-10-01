package id.co.dapenbi.accounting.controller.laporan;

import id.co.dapenbi.accounting.entity.laporan.InvestasiDetail;
import id.co.dapenbi.accounting.entity.laporan.InvestasiHeader;
import id.co.dapenbi.accounting.service.impl.laporan.MasterLaporanService;
import id.co.dapenbi.accounting.service.impl.laporan.MasterSPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/master-spi")
public class MasterSPIController {

    @Autowired
    private MasterSPIService spiService;

    @Autowired
    private MasterLaporanService masterLaporanService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/master-spi");
        modelAndView.addObject("lapHeaderList", masterLaporanService.findAllHeader());
        return modelAndView;
    }

    @GetMapping("/findAllDetails/{idInvestasi}")
    public ResponseEntity<?> findAllDetails(@PathVariable String idInvestasi) {
        return new ResponseEntity<>(spiService.findAllInvestasiDetails(idInvestasi), HttpStatus.OK);
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(
            @RequestBody @Valid InvestasiHeader request,
            Principal principal) {
        log.info("Request :{}", request);
        request.setCreatedBy(principal.getName());
        request.setCreatedDate(new Date());
        spiService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(
            @RequestBody @Valid InvestasiHeader request,
            Principal principal) {
        log.info("Request :{}", request);
        request.setUpdatedBy(principal.getName());
        request.setUpdatedDate(new Date());
        spiService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update/status-investasi")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateStatusInv(
            @RequestParam String idInvestasi,
            @RequestParam String status,
            Principal principal) {
        InvestasiHeader investasiHeader = spiService.updateStatusInv(status, idInvestasi, principal.getName());
        if (investasiHeader == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{idInvestasi}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(
            @PathVariable String idInvestasi) {
        spiService.deleteMasterInv(idInvestasi);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/spi")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateSpi(
            @RequestBody @Valid InvestasiDetail request,
            Principal principal) {
        log.info("Request :{}", request);
        request.setUpdatedBy(principal.getName());
        request.setUpdatedDate(new Date());
        spiService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update/status-spi")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateStatusSpi(
            @RequestParam Integer idInvestasiDtl,
            @RequestParam String status,
            Principal principal) {
        InvestasiDetail investasiDetail = spiService.updateStatusSpi(status, idInvestasiDtl, principal.getName());
        if (investasiDetail == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{idInvestasiDtl}/spi")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteSpi(
            @PathVariable Integer idInvestasiDtl) {
        spiService.deleteSpi(idInvestasiDtl);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/spi")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveDetail(
            @RequestBody @Valid InvestasiDetail request,
            Principal principal) {
        log.info("Request :{}", request);
        request.setCreatedBy(principal.getName());
        request.setCreatedDate(new Date());
        spiService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<InvestasiHeader> headerDatatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        return spiService.getInvestasiHeaderDataTables(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables/{idInvestasi}/detail")
    @ResponseBody
    public DataTablesOutput<InvestasiDetail> detailDatatables(
            @Valid @RequestBody DataTablesInput input,
            @PathVariable String idInvestasi
    ) {
        return spiService.getInvestasiDetailDataTables(input, idInvestasi);
    }
}

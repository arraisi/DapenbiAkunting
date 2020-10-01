package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.SPIDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIHeaderDTO;
import id.co.dapenbi.accounting.entity.laporan.InvestasiHeader;
import id.co.dapenbi.accounting.entity.laporan.SPIHeader;
import id.co.dapenbi.accounting.repository.laporan.InvestasiHeaderRepository;
import id.co.dapenbi.accounting.service.impl.laporan.TransaksiSPIService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/transaksi-spi")
public class TransaksiSPIController {
    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private TransaksiSPIService service;

    @Autowired
    private InvestasiHeaderRepository investasiHeaderRepository;

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("")
    public ModelAndView showTransaksiSPI() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/transaksiSPI");
        modelAndView.addObject("periodeList", periodeService.findAll());
        modelAndView.addObject("investasiHeaderList", investasiHeaderRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/save/header/details")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid SPIHeaderDTO request, Principal principal) {
        try {
            final SPIHeader save = service.save(request, principal);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }

    @GetMapping("/findByIdSPIHdr/{id}")
    public ResponseEntity<?> findByIdSPIHdr(@PathVariable Integer id) {
        return new ResponseEntity<>(service.findDetailByIdSPIHdr(id), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Integer idSPIHdr, Principal principal) {
        service.delete(idSPIHdr);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/detail/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteDetail(@RequestBody @Valid Integer idSPIDtl, Principal principal) {
        service.deleteDetil(idSPIDtl);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<SPIHeaderDTO> datatables(
            @RequestBody SPIHeaderDTO.DatatablesBody payload
    ) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getSpiHeaderDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables/detail")
    @ResponseBody
    public DataTablesResponse<SPIDetailDTO> datatables(
            @RequestBody SPIDetailDTO.DatatablesBody payload
    ) {
        return service.SPIDetaildatatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getSpiDetailDTO()
                ),
                payload.getSearch().getValue()
        );
    }
}

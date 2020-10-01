package id.co.dapenbi.accounting.controller.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.mapper.TransaksiMapper;
import id.co.dapenbi.accounting.service.impl.parameter.TransaksiJurnalService;
import id.co.dapenbi.accounting.service.impl.parameter.TransaksiService;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatService;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("")
public class TransaksiController {

    @Autowired
    private WarkatService warkatService;

    @Autowired
    private TransaksiService service;

    @Autowired
    private TransaksiJurnalService transaksiJurnalService;

    @GetMapping("/akunting/parameter/jenis-transaksi")
    public ModelAndView showTransaksi() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/jenisTransaksi");
        modelAndView.addObject("jenisWarkatList", warkatService.findJenisWarkat());
        return modelAndView;
    }

    @PostMapping("/api/akunting/parameter/jenis-transaksi/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid TransaksiDTO request, Principal principal) {
        if (request.getCreatedBy() == null) {
            request.setCreatedBy(principal.getName());
            request.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            request.setUpdatedBy(principal.getName());
            request.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        Transaksi transaksi = TransaksiMapper.INSTANCE.dtoToTransaksi(request);
        service.save(transaksi);
        transaksiJurnalService.deleteByKodeTransaksi(request.getKodeTransaksi());
        transaksiJurnalService.saveAll(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/akunting/parameter/jenis-transaksi/{id}/findById")
    public ResponseEntity<Transaksi> findById(@PathVariable("id") String id) {
        Optional<Transaksi> data = service.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/jenis-transaksi/findAll")
    public ResponseEntity<Iterable<Transaksi>> findAll() {
        List<Transaksi> data = service.findAll();
        if (!data.isEmpty()) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/jenis-transaksi/findAllWithJurnals")
    public ResponseEntity<Iterable<TransaksiDTO>> findAllWithJurnals() {
        List<TransaksiDTO> data = service.findAllWithJurnals();
        if (!data.isEmpty()) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/api/akunting/parameter/jenis-transaksi/update")
    public ResponseEntity<?> update(@RequestBody @Valid Transaksi request) {
        service.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/akunting/parameter/jenis-transaksi/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Transaksi transaksi) {
        try {
            service.delete(transaksi);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("{}", e);
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/jenis-transaksi/getTransaksiDataTables")
    @ResponseBody
    public DataTablesOutput<Transaksi> getTransaksiDataTable(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return this.service.findForDataTable(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/jenis-transaksi/datatables")
    @ResponseBody
    public DataTablesResponse<TransaksiDTO> datatables(
            @RequestBody TransaksiDTO.DatatablesBody payload
    ) {
        TransaksiDTO transaksiDTO = payload.getTransaksiDTO();
        DataTablesResponse<TransaksiDTO> datatables = service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        transaksiDTO
                ),
                payload.getSearch().getValue()
        );
        return datatables;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/jenis-transaksi/datatablesForJurnalBiaya")
    @ResponseBody
    public DataTablesResponse<Transaksi> datatablesForJurnalBiaya(
            @RequestBody TransaksiDTO.DatatablesBody payload
    ) {
        DataTablesResponse<Transaksi> datatables = service.datatablesForJurnalBiaya(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        new TransaksiDTO()
                ),
                payload.getSearch().getValue()
        );
        return datatables;
    }
}

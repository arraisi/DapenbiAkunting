package id.co.dapenbi.accounting.controller.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.RekeningIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.service.impl.master.LookupMasterService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
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
public class RekeningController {

    @Autowired
    private RekeningService service;

    @Autowired
    private LookupMasterService lookupMasterService;

    @GetMapping("/akunting/parameter/rekening")
    public ModelAndView showRekening() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/rekening");
        modelAndView.addObject("listTipeRekening", lookupMasterService.findByJenisLookup("TIPE_REKENING"));
        return modelAndView;
    }

    @PostMapping("/api/akunting/parameter/rekening/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid Rekening request, Principal principal) {
        if (request.getCreatedBy() == null || request.getCreatedDate() == null) {
            request.setCreatedBy(principal.getName());
            request.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            request.setUpdatedBy(principal.getName());
            request.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/akunting/parameter/rekening/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody @Valid Rekening request, Principal principal) {
        int response = service.update(request, principal);
        if (response == 1)
            return ResponseEntity.status(HttpStatus.CREATED).build();
        else return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PutMapping("/akunting/parameter/rekening/updateEdited")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateEdited(@RequestParam Integer idRekening, @RequestParam Integer status) {
        int response = service.updateEdited(idRekening, status);
        if (response == 1)
            return ResponseEntity.status(HttpStatus.CREATED).build();
        else return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/api/akunting/parameter/rekening/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Rekening request) {
        service.delete(request.getIdRekening());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/api/akunting/parameter/rekening/by/kodeRekening")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteByKodeRekening(@RequestBody @Valid Rekening request) {
        service.deleteByKodeRekening(request.getKodeRekening());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/rekening/datatables")
    @ResponseBody
    public DataTablesOutput<Rekening> datatables(
            @Valid @RequestBody DataTablesInput input) {
        return this.service.findForDataTable(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/rekening/getDatatables")
    @ResponseBody
    public DataTablesResponse<RekeningDTO> datatables(
            @RequestBody RekeningDTO.DatatablesBody payload
    ) {
        DataTablesResponse<RekeningDTO> datatables = service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getRekeningDTO()
                ),
                payload.getSearch().getValue()
        );
        return datatables;
    }

    @GetMapping("/api/akunting/parameter/rekening/findAll")
    @ResponseBody
    public ResponseEntity<Iterable<Rekening>> getAllRekening() {
        return new ResponseEntity<>(this.service.findAll(), HttpStatus.OK);
    }


    @GetMapping("/api/akunting/parameter/rekening/findByListTipeRekening")
    @ResponseBody
    public ResponseEntity<List<Rekening>> findByTipeRekening(@RequestParam String tipeRekening) {
        return new ResponseEntity<>(this.service.findByListTipeRekening(tipeRekening), HttpStatus.OK);
    }

    // rekening tanpa saldo current
    @GetMapping("/api/akunting/parameter/rekening/findAllRekening")
    @ResponseBody
    public ResponseEntity<Iterable<Rekening>> getAll() {
        return new ResponseEntity<>(this.service.findAllRekenings(), HttpStatus.OK);
    }

    @GetMapping("/api/akunting/parameter/rekening/findAllDTO")
    @ResponseBody
    public ResponseEntity<Iterable<RekeningDTO>> getAllRekeningDTO() {
        return new ResponseEntity<>(this.service.findAllDTO(), HttpStatus.OK);
    }

    @GetMapping("/api/akunting/parameter/rekening/findAllParent")
    @ResponseBody
    public ResponseEntity<Iterable<Rekening>> getAllParentRekening() {
        Iterable<Rekening> allParent = this.service.getAllParent();
        return new ResponseEntity<>(allParent, HttpStatus.OK);
    }

    @GetMapping("/api/akunting/parameter/rekening/findAllIdRekening")
    @ResponseBody
    public ResponseEntity<List<String>> getAllIdRekening() {
        return new ResponseEntity<>(this.service.getAllIdRekening(), HttpStatus.OK);
    }

    @GetMapping("/api/akunting/parameter/rekening/findById/{id}")
    @ResponseBody
    public ResponseEntity<RekeningDTO> findById(@PathVariable("id") Integer id) {
        RekeningDTO data = service.findByIdDTO(id);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/api/akunting/parameter/rekening/findSaldoAwalAkhir")
    @ResponseBody
    public ResponseEntity<?> findSaldoAwalAkhir(@RequestParam String kodeRekening, @RequestParam String startDate, @RequestParam String endDate) {
        SaldoDTO data = service.findSaldoAwalAkhir(kodeRekening, startDate, endDate);
        return ResponseEntity.ok(data);
    }

}

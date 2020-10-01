package id.co.dapenbi.accounting.controller.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.parameter.ArusKasRincianDTO;
import id.co.dapenbi.accounting.entity.parameter.ArusKasRincian;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.service.impl.parameter.ArusKasRincianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/parameter/arus-kas-rincian")
public class ArusKasRincianController {

    @Autowired
    private ArusKasRincianService arusKasRincianService;

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid ArusKasRincianDTO.ArusKasRincianEntity request, Principal principal) {
        request.setCreatedBy(principal.getName());
        Optional<ArusKasRincian> data = arusKasRincianService.idChecker(request.getKodeRincian(), request.getKodeArusKas());
        if(data.isPresent()) {
            return ResponseEntity.ok("Exist");
        } else {
            arusKasRincianService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/{kodeArusKas}/{kodeRincian}/findByKodeArusKasAndKodeRincian")
    public ResponseEntity<ArusKasRincianDTO.ArusKasRincianEntity> findById(@PathVariable("kodeArusKas") String kodeArusKas, @PathVariable("kodeRincian") String kodeRincian) {
        Optional<ArusKasRincianDTO.ArusKasRincianEntity> data = arusKasRincianService.findByKodeArusKasAndKodeArusKasRincian(kodeArusKas, kodeRincian);
        return ResponseEntity.ok(data.get());
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody @Valid ArusKasRincianDTO.ArusKasRincianEntity request, Principal principal) {
        request.setUpdatedBy(principal.getName());
        arusKasRincianService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid ArusKasRincian arusKasRincian) {
        arusKasRincianService.delete(arusKasRincian.getKodeRincian(), arusKasRincian.getKodeArusKas());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getArusKasRincianDataTables")
    @ResponseBody
    public DataTablesOutput<ArusKasRincian> getArusKasRincianDataTable(
            @RequestBody @Valid DataTablesInput input
    ) {
        return arusKasRincianService.findForDataTable(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/{id}/getByArusKasIdDatatables")
    @ResponseBody
    public DataTablesResponse<ArusKasRincian> datatablesByArusKasId(
            @RequestBody ArusKasRincianDTO.ArusKasRincianDatatablesBody payload,
            @PathVariable String id
    ) {
//        if(params == null) params = new ArusKasRincian();
//        log.info("draw: {}, start: {}, length: {}, orderBy: {}, orderDir: {},  type: {}", draw, start, length, iSortCol0, sSortDir0, params);
        return arusKasRincianService.datatableByArusKasId(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        new ArusKasRincian()
                ),
                id,
                payload.getSearch().getValue()
        );
    }

    @GetMapping("/{kode}/get-id-arus-kas-rincian")
    public ResponseEntity<?> getIdArusKasRincian(@PathVariable("kode") String kodeArusKas) {
        Integer data = arusKasRincianService.getIdArusKasRincian(kodeArusKas);
        return ResponseEntity.ok(data);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getRekeningDataTables")
    @ResponseBody
    public DataTablesOutput<Rekening> rekeningDataTablesOutput(@Valid @RequestBody DataTablesInput input) {
        return arusKasRincianService.rekeningDataTablesOutput(input);
    }
}

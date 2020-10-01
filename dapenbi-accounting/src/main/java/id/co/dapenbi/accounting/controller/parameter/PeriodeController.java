package id.co.dapenbi.accounting.controller.parameter;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("")
public class PeriodeController {

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("/akunting/parameter/periode")
    public ModelAndView showPeriode() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/periode");
        return modelAndView;
    }

    @PostMapping("/api/akunting/parameter/periode/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid Periode request, Principal principal) {
        Optional<Periode> data = periodeService.findById(request.getKodePeriode());
        if (data.isPresent()) {
            return ResponseEntity.ok("Exist");
        } else {
            request.setCreatedBy(principal.getName());
            periodeService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/api/akunting/parameter/periode/{id}/findById")
    public ResponseEntity<Periode> findById(@PathVariable("id") String id) {
        Optional<Periode> data = periodeService.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @GetMapping("/api/akunting/parameter/periode/{namePeriode}/findByNamePeriode")
    public ResponseEntity<Periode> findByNamePeriode(@PathVariable("namePeriode") String namePeriode) {
        Optional<Periode> data = periodeService.findByNamaPeriode(namePeriode);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/periode/list")
    public ResponseEntity<?> findAll() {
        try {
            Iterable<Periode> data = periodeService.findAll();
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/api/akunting/parameter/periode/update")
    public ResponseEntity<?> update(@RequestBody @Valid Periode request, Principal principal) {
        request.setUpdatedBy(principal.getName());
        periodeService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/akunting/parameter/periode/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Periode periode) {
        periodeService.delete(periode.getKodePeriode());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/periode/getPeriodeDataTables")
    @ResponseBody
    public DataTablesOutput<Periode> getCompanyDataTable(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return this.periodeService.findForDataTable(input);
    }
}

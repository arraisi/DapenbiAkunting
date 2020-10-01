package id.co.dapenbi.accounting.controller.parameter;

import id.co.dapenbi.accounting.dto.MSTLookUp;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
import id.co.dapenbi.accounting.entity.parameter.ArusKas;
import id.co.dapenbi.accounting.service.impl.parameter.ArusKasService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
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
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/akunting/parameter/arus-kas")
public class ArusKasController {

    @Autowired
    private ArusKasService arusKasService;

    @Autowired
    private RekeningService rekeningService;

    @GetMapping("")
    public ModelAndView showArusKas() throws SQLException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/parameter/arusKas");
        modelAndView.addObject("aktivitasList", arusKasService.findJenisAktivitas());
        modelAndView.addObject("rekeningList", rekeningService.findAllByOrder());

        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid ArusKas request, Principal principal) {
        request.setCreatedBy(principal.getName());
        Optional<ArusKas> data = arusKasService.idChecker(request.getKodeArusKas());
        if (data.isPresent()) {
            return ResponseEntity.ok("Exist");
        } else {
            arusKasService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/{id}/findById")
    public ResponseEntity<ArusKas> findById(@PathVariable("id") String id) {
        Optional<ArusKas> data = arusKasService.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/findJenisAktivitas")
    public ResponseEntity<?> findJenisAktivitas() {
        try {
            List<MSTLookUp> listAktivitas = arusKasService.findJenisAktivitas();
            return new ResponseEntity<>(listAktivitas, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid ArusKas request, Principal principal) {
        request.setUpdatedBy(principal.getName());
        arusKasService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid ArusKas arusKas) {
        arusKasService.deleteById(arusKas.getKodeArusKas());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getArusKasDataTables")
    @ResponseBody
    public DataTablesOutput<ArusKas> getArusKasDataTable(
            @RequestBody @Valid DataTablesInput input
    ) {
        return arusKasService.findForDataTable(input);
    }
}

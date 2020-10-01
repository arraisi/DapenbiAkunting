package id.co.dapenbi.accounting.controller.parameter;

import id.co.dapenbi.accounting.entity.parameter.JenisDRI;
import id.co.dapenbi.accounting.service.impl.parameter.JenisDRIService;
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
import java.util.Optional;

@Controller
@RequestMapping("/akunting/parameter/jenis-dri")
public class JenisDRIController {

    @Autowired
    private JenisDRIService jenisDRIService;

    @GetMapping("")
    public ModelAndView showPeriode() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/jenisDRI");

        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid JenisDRI request, Principal principal) {
        request.setCreatedBy(principal.getName());
        Optional<JenisDRI> data = jenisDRIService.findById(request.getKodeDRI());
        if(data.isPresent()) {
            return ResponseEntity.ok("Exist");
        } else {
            jenisDRIService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/{id}/findById")
    public ResponseEntity<JenisDRI> findById(@PathVariable("id") String id) {
        Optional<JenisDRI> data = jenisDRIService.findById(id);
        if(data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid JenisDRI request, Principal principal) {
        request.setUpdatedBy(principal.getName());
        jenisDRIService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid JenisDRI jenisDRI) {
        jenisDRIService.delete(jenisDRI.getKodeDRI());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getJenisDRIDataTables")
    @ResponseBody
    public DataTablesOutput<JenisDRI> getCompanyDataTable(
            @Valid @RequestBody DataTablesInput input
    ) {
        return jenisDRIService.findForDataTable(input);
    }
}

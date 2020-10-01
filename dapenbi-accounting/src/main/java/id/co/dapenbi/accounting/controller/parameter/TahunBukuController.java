package id.co.dapenbi.accounting.controller.parameter;

import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
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
@RequestMapping("")
public class TahunBukuController {

    @Autowired
    private TahunBukuService tahunBukuService;

    @GetMapping("/akunting/parameter/tahun-buku")
    public ModelAndView showTahunBuku() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/tahunBuku");
        return modelAndView;
    }

    @PostMapping("/api/akunting/parameter/tahun-buku/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid TahunBuku request, Principal principal) {
        Optional<TahunBuku> data = tahunBukuService.findById(request.getKodeTahunBuku());
        if (data.isPresent()) {
            return ResponseEntity.ok("Exist");
        } else {
//            if(request.getStatusAktif() == "1") {
//                tahunBukuService.resetAllStatusAktif(request.getKodeTahunBuku());
//            }
            request.setCreatedBy(principal.getName());
            tahunBukuService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping("/api/akunting/parameter/tahun-buku/{id}/findById")
    public ResponseEntity<TahunBuku> findById(@PathVariable("id") String id) {
        Optional<TahunBuku> data = tahunBukuService.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/tahun-buku/findByStatusAktif")
    public ResponseEntity<TahunBuku> findByStatusAktif() {
        Optional<TahunBuku> data = tahunBukuService.findByStatusAktif();
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/tahun-buku/getList")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAll() {
        Iterable<TahunBuku> tahunBukus = tahunBukuService.getAll();
        return ResponseEntity.ok(tahunBukus);
    }


    @GetMapping("/api/akunting/parameter/tahun-buku/findByTahun")
    public ResponseEntity<?> findByTahun(@RequestParam Integer tahun) {
        try {
            Optional<TahunBuku> tahunBuku = tahunBukuService.findByTahun(tahun);
            return new ResponseEntity<>(tahunBuku.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/api/akunting/parameter/tahun-buku/update")
    public ResponseEntity<?> update(@RequestBody @Valid TahunBuku request, Principal principal) {
//        if(request.getStatusAktif().equals("1")) {
//            tahunBukuService.resetAllStatusAktif(request.getKodeTahunBuku());
//        }
        request.setUpdatedBy(principal.getName());
        tahunBukuService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/akunting/parameter/tahun-buku/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid TahunBuku tahunBuku) {
        tahunBukuService.delete(tahunBuku.getKodeTahunBuku());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/tahun-buku/getTahunBukuDataTables")
    @ResponseBody
    public DataTablesOutput<TahunBuku> getCompanyDataTable(
            @Valid @RequestBody DataTablesInput input
    ) {
        return this.tahunBukuService.findForDataTable(input);
    }

    @PostMapping("/api/akunting/parameter/tahun-buku/{id}/reset-status-aktif")
    public ResponseEntity<?> resetAllStatusAktif(@PathVariable("id") String id) {
        tahunBukuService.resetAllStatusAktif(id);
        return ResponseEntity.ok().build();
    }
}

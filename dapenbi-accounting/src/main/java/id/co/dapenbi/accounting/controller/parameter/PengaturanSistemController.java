package id.co.dapenbi.accounting.controller.parameter;

import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/akunting/parameter/pengaturan-sistem")
public class PengaturanSistemController {

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("")
    public ModelAndView showPengaturanSistem() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/parameter/pengaturanSistem");
        modelAndView.addObject("tahunBukuList", tahunBukuService.getAll());
        modelAndView.addObject("periodeList", periodeService.getAll());

        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid PengaturanSistem request, Principal principal) {
        request.setCreatedBy(principal.getName());
        pengaturanSistemService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{username}/findByUsername")
    public ResponseEntity<PengaturanSistem> findByUsername(@PathVariable("username") String username) {
        Optional<PengaturanSistem> data = pengaturanSistemService.findByUsername(username);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/findByCreatedDate")
    public ResponseEntity<PengaturanSistemDTO> findByCreatedDate() {
        Optional<PengaturanSistemDTO> data = pengaturanSistemService.findByCreatedDate();
        return ResponseEntity.ok(data.get());
    }

    @GetMapping("/findByStatusAktif")
    public ResponseEntity<PengaturanSistem> findByStatusAktif() {
        Optional<PengaturanSistem> data = pengaturanSistemService.findByStatusAktif();
        return ResponseEntity.ok(data.get());
    }

    @GetMapping("/findDTOByStatusAktif")
    public ResponseEntity<PengaturanSistemDTO> findDTOByStatusAktif() {
        try {
            PengaturanSistemDTO data = pengaturanSistemService.findDTOByStatusAktif();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody @Valid PengaturanSistem request) {
        pengaturanSistemService.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/findLatestKodeDRI")
    public ResponseEntity<String> findLatestKodeDRI() {
        Optional<String> optional = pengaturanSistemService.findLatestKodeDRI();
        if (!optional.isPresent())
            return ResponseEntity.ok("1");

        String data = optional.get();
        String dataSend;
        if (data.equals("1"))
            dataSend = "2";
        else if (data.equals("2"))
            dataSend = "3";
        else
            dataSend = "0";
        return ResponseEntity.ok(dataSend);
    }
}

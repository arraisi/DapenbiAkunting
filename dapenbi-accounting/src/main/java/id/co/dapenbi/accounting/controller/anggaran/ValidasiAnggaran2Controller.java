package id.co.dapenbi.accounting.controller.anggaran;

import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDetailDTO;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.anggaran.PenyusunanAnggaranAkuntingService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/anggaran/validasi-anggaran-new")
public class ValidasiAnggaran2Controller {

    @Autowired
    private PenyusunanAnggaranAkuntingService service;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("anggaran/validasiAnggaran2");
        modelAndView.addObject("periodeList", service.findAllPeriode());
        modelAndView.addObject("tahunBukuList", tahunBukuService.listByStatusAktif());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping("/{noAnggaran}/approve")
    public ResponseEntity<?> approve(
            @PathVariable String noAnggaran,
            Principal principal) {
        Optional<PenyusunanAnggaranAkunting> penyusunanAnggaranAkuntingOptional = service.findById(noAnggaran);
        if (!penyusunanAnggaranAkuntingOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        PenyusunanAnggaranAkunting penyusunanAnggaranAkunting = penyusunanAnggaranAkuntingOptional.get();
        PengaturanSistem pengaturanSistem =pengaturanSistemService.findByStatusAktif().get();
        if (penyusunanAnggaranAkunting.getKodeThnBuku().getKodeTahunBuku().equals(pengaturanSistem.getKodeTahunBuku())){
            PenyusunanAnggaranAkunting anggaran = service.updateStatusData(noAnggaran, "APPROVE", principal.getName());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.ok("Kode Tahun Buku Tidak Sama Dengan Parameter");
        }
    }

    @PostMapping("/{noAnggaran}/reject")
    public ResponseEntity<?> reject(
            @PathVariable String noAnggaran,
            Principal principal) {
        PenyusunanAnggaranAkunting anggaran = service.updateStatusData(noAnggaran, "REJECT", principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<PenyusunanAnggaranAkunting> datatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return service.validasiDataTable(input);
    }

    @PostMapping("/update-saldo-current-pa-fa")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateSaldoCurrentPAFA(@RequestParam String noAnggaran) {
        PengaturanSistem pengaturanSistem =pengaturanSistemService.findByStatusAktif().get();
        List<PenyusunanAnggaranAkuntingDetailDTO> penyusunanAnggaranAkuntings = service.findDetails(noAnggaran);
        if (penyusunanAnggaranAkuntings.size() != 0){
            log.info("no anggaran {}", noAnggaran);
            log.info("no anggaran list 0 {}", penyusunanAnggaranAkuntings.get(0).getNoAnggaran());
            log.info("kode tahun buku {}", penyusunanAnggaranAkuntings.get(0).getKodeThnBuku());
            if (penyusunanAnggaranAkuntings.get(0).getKodeThnBuku().equals(pengaturanSistem.getKodeTahunBuku())) {
                service.updateSaldoCurrentPAFA(penyusunanAnggaranAkuntings);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.ok("Kode Tahun Buku Tidak Sama Dengan Parameter");
            }
        } else {
            return ResponseEntity.ok("List Data Kosong");
        }
    }
}

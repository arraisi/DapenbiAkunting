package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.BukaSistemDto;
import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.transaksi.BukaSistemService;
import id.co.dapenbi.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi/buka-sistem")
public class BukaSistemController {

    @Autowired
    private BukaSistemService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPembukuan() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/bukaSistem");
        modelAndView.addObject("statusPemakaiList", service.getStatusPemakai());

        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        if (/*service.libur() || */currentDay.name().equals("SATURDAY") || currentDay.equals("SUNDAY")) {
            modelAndView.addObject("showMessage", true);
            modelAndView.addObject("message", "Hari Libur, Tidak dapat melakukan Buka Sistem");
        } else {
            modelAndView.addObject("showMessage", false);
            modelAndView.addObject("message", "");
        }

        return modelAndView;
    }

    @GetMapping("/default-value")
    public ResponseEntity<BukaSistemDto> getDefaultValue(Principal principal) {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        String statusSistem = pengaturanSistem.isPresent() ? pengaturanSistem.get().getStatusOpen() : "None";
//        log.info("{}", principal);
        BukaSistemDto dto = new BukaSistemDto();
        dto.setNoBukaSistem(service.latestId() + 1);
        dto.setStatusSistem(statusSistem.equals("O") ? 1 : 0);
        dto.setTglTransaksiSebelum(DateUtil.format(pengaturanSistem.get().getTglTransaksi(), "yyyy-MM-dd"));
        dto.setTglTransaksiSekarang(DateUtil.format(new Date(), "yyyy-MM-dd"));
        dto.setStatusPemakai(service.getStatusPemakai(principal.getName()));
        dto.setStatusOpen(statusSistem);
        dto.setIdOrg(service.getIdOrgByNip(principal.getName()).get());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid BukaSistemDto request, Principal principal) throws ParseException {
//        final Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemService.findByStatusAktif();
        if (!request.getTglTransaksiSekarang().isEmpty()){
            final Boolean tglSaldoValid = service.cekTglOpen(request.getTglTransaksiSekarang());
            if (!tglSaldoValid){
                return new ResponseEntity<>("Transaksi hari ini sudah ditutup.", HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>("Transaksi tidak dapat dilakkukan karena transaksi hari ini belum dibuka.", HttpStatus.CONFLICT);
        }

//        log.info("Buka Sistem :{}", request);
        if(!request.getTglTransaksiSebelum().isEmpty()) {
            BukaSistemDto saldo = service.aktivaPasiva(request.getTglTransaksiSebelum());
            if (saldo.getNilaiAktiva().equals(request.getNilaiAktiva())
                    && saldo.getNilaiPasiva().equals(request.getNilaiPasiva())) {
                pengaturanSistemService.setStatusOpen();
                pengaturanSistemService.setTanggalTransaksi(request.getTglTransaksiSekarang());
                service.save(request, principal.getName());
//            service.saveUpdateParam(principal.getName());
                return ResponseEntity.ok("Sistem Dibuka");
            } else {
//            return ResponseEntity.ok("*Data Input Total Aktiva dan Total Pasiva dengan Data pada Database Tidak Sama");
                return new ResponseEntity<>("*Data Input Total Aktiva dan Total Pasiva dengan Data pada Database Tidak Sama", HttpStatus.CONFLICT);
            }
        } else {
            if (request.getNilaiAktiva().equals(request.getNilaiPasiva())) {
                pengaturanSistemService.setStatusOpen();
                pengaturanSistemService.setTanggalTransaksi(request.getTglTransaksiSekarang());
                service.save(request, principal.getName());
//            service.saveUpdateParam(principal.getName());
                return ResponseEntity.ok("Sistem Dibuka");
            } else {
//            return ResponseEntity.ok("*Data Input Total Aktiva dan Total Pasiva dengan Data pada Database Tidak Sama");
                return new ResponseEntity<>("*Data Input Total Aktiva dan Total Pasiva dengan Data pada Database Tidak Sama", HttpStatus.CONFLICT);
            }
        }
    }
}

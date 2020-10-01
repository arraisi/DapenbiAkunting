package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.TutupTahunanDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import id.co.dapenbi.accounting.service.impl.transaksi.TutupTahunanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/akunting/transaksi/tutup-tahunan")
@Slf4j
public class TutupTahunanController {

    @Autowired
    private TutupTahunanService service;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("")
    public ModelAndView showValidasiBudgetReview() {
        PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
        String kodePeriode = pengaturanSistem.getKodePeriode();
        String kodeTahunBuku = pengaturanSistem.getKodeTahunBuku();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/tutup-tahunan");
        modelAndView.addObject("pengaturanSistem", pengaturanSistem);
        modelAndView.addObject("totalTransaksiWarkatDRI2", service.findTutupTahunan("JURNAL_PAJAK"));
        modelAndView.addObject("totalTransaksiWarkatDRI3", service.findTutupTahunan("JURNAL_TRANSAKSI"));
        modelAndView.addObject("totalTransaksiSaldoDRI1", service.checkTransaksiSaldo("1", pengaturanSistem.getTglTransaksi()));
        modelAndView.addObject("totalTransaksiSaldoDRI2", service.checkTransaksiSaldo("2", pengaturanSistem.getTglTransaksi()));
        modelAndView.addObject("totalTransaksiSaldoDRI3", service.checkTransaksiSaldo("3", pengaturanSistem.getTglTransaksi()));
        modelAndView.addObject("periode", periodeService.findById(kodePeriode).get());
        modelAndView.addObject("tahunBuku", tahunBukuService.findById(kodeTahunBuku).get());

        return modelAndView;
    }

//    @GetMapping("/")
//    public ResponseEntity<TutupTahunanDTO> findTutupHarian() {
//        TutupTahunanDTO tutupTahunanDTO = service.findTutupTahunan();
//        if(tutupTahunanDTO != null) {
//            return new ResponseEntity<>(tutupTahunanDTO, HttpStatus.OK);
//        } else {
//            return ResponseEntity.noContent().build();
//        }
//    }

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody TutupTahunanDTO request, Principal principal) {
        List<Saldo> saldos = service.post(request, principal);
        if(!saldos.isEmpty()) {
            return new ResponseEntity<>(saldos, HttpStatus.OK);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/post-tahunan")
    public ResponseEntity<?> postTahunan(@RequestBody Map<String, String> object, Principal principal) {
        service.postTahunan(object.get("tglTransaksi"), object.get("kodeTahunBuku"), object.get("kodePeriode"), object.get("kodeDRI"), principal);
        return ResponseEntity.ok().build();
    }

}

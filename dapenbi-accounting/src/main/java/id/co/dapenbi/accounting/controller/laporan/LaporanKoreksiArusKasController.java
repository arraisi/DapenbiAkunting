package id.co.dapenbi.accounting.controller.laporan;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/akunting/laporan/laporan-koreksi-arus-kas")
public class LaporanKoreksiArusKasController {

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/laporanKoreksiArusKas");
        return modelAndView;
    }
}

package id.co.dapenbi.accounting.controller.sse;

import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/akunting/sse/serap-sse")
public class SerapAnggaranSSEController {

    @Autowired
    private RekeningService rekeningService;

    @GetMapping("")
    public ModelAndView showSerap() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/serapAnggaran");
        modelAndView.addObject("rekeningList", rekeningService.findByTipeRekening("BIAYA"));
        return modelAndView;
    }
}

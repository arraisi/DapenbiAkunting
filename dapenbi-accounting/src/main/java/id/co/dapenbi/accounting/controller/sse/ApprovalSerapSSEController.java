package id.co.dapenbi.accounting.controller.sse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/akunting/sse/approval-serap-sse")
public class ApprovalSerapSSEController {

    @GetMapping("")
    public ModelAndView showSerap() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sse/approvalSerapAnggaran");
        return modelAndView;
    }
}

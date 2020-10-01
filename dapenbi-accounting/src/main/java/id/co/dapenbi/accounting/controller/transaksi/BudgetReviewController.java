package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO.DaftarRekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.transaksi.BudgetReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/akunting/transaksi")
public class BudgetReviewController {

    @Autowired
    private BudgetReviewService service;

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("/validasi-budget-review")
    public ModelAndView showValidasiBudgetReview() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listPeriode", periodeService.findAll());
        modelAndView.setViewName("transaksi/validasiBudgetReview");
        return modelAndView;
    }

    @GetMapping("/budget-review")
    public ModelAndView showBudgetReview() {
        ModelAndView modelAndView = new ModelAndView();
        final Iterable<Periode> all = periodeService.findAll();
        modelAndView.addObject("periodeList", all);
        modelAndView.setViewName("transaksi/budgetReview");
        return modelAndView;
    }

    @PutMapping("/budget-review/validasi")
    public ResponseEntity<?> validasi(@RequestBody BudgetReviewDTO budgetReview, Principal principal) {
        try {
            BudgetReview _budgetReview = service.validasiBudgetReview(budgetReview, principal);
            return new ResponseEntity<>(_budgetReview, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Transaksi gagal.", HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/budget-review/find/daftar-rekening")
    public ResponseEntity<List<DaftarRekeningDTO>> getDaftarRekening(@RequestBody BudgetReviewDTO request) throws ParseException {
        try {
            List<DaftarRekeningDTO> daftarRekening = service.getDaftarRekening(request);
            return new ResponseEntity<>(daftarRekening, HttpStatus.OK);
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/budget-review/generateNumber")
    public ResponseEntity<String> generateNumberBudgetReview() {
        return new ResponseEntity<>(service.generateNumberBudgetReview(), HttpStatus.OK);
    }

    @GetMapping("/budget-review/details")
    public ResponseEntity<?> findDetails(@RequestParam String noBudgetReview) {
        return new ResponseEntity<>(service.findDetails(noBudgetReview), HttpStatus.OK);
    }

    @PostMapping("/budget/and/details")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveBudgetAndDetails(@RequestBody @Valid BudgetReviewDTO request, Principal principal) {
        if (request.getNoBudgetReview().isEmpty()) {
            String noBudgetReview = service.generateNumberBudgetReview();
            request.setNoBudgetReview(noBudgetReview);
        }
        try {
            service.saveBudgetAndDetails(request, principal);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }

    @PostMapping("/budget-review/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid BudgetReview request, Principal principal) {
        service.save(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/budget-review/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid BudgetReview request, Principal principal) {
        service.delete(request.getNoBudgetReview(), principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/budget-review/datatables")
    @ResponseBody
    public DataTablesResponse<BudgetReviewDTO> datatables(
            @RequestBody BudgetReviewDTO.DatatablesBody payload
    ) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getBudgetReviewDTO()
                ),
                payload.getSearch().getValue()
        );
    }
}

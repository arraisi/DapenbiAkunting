package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanBudgetReviewDto;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanBudgetReviewService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/laporan-budget-review")
public class LaporanBudgetReviewController {

    @Autowired
    private LaporanBudgetReviewService service;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPage() {
        PengaturanSistem pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();

        String sistemKodePeriode = pengaturanSistem.getKodePeriode();
        String sistemTriwulan;
        if (sistemKodePeriode.equals("01") || sistemKodePeriode.equals("02") || sistemKodePeriode.equals("03")) sistemTriwulan = "TW1";
        else if (sistemKodePeriode.equals("04") || sistemKodePeriode.equals("05") || sistemKodePeriode.equals("06")) sistemTriwulan = "TW2";
        else if (sistemKodePeriode.equals("07") || sistemKodePeriode.equals("08") || sistemKodePeriode.equals("09")) sistemTriwulan = "TW3";
        else sistemTriwulan = "TW4";
        List<String> listNoBudgetReview = service.findListBudgetReviewValid(pengaturanSistem.getKodeTahunBuku(), sistemTriwulan);
        if (listNoBudgetReview.size() == 0) listNoBudgetReview.add(0, "No Data Available");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/laporan-budget-review");
        modelAndView.addObject("listTahunBuku", tahunBukuService.listByStatusAktif());
        modelAndView.addObject("listPeriode", periodeService.getAll());
        modelAndView.addObject("pengaturanSistem", pengaturanSistem);
        modelAndView.addObject("listNoBudgetReview", listNoBudgetReview);
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<LaporanBudgetReviewDto.Response> datatables(@RequestBody LaporanBudgetReviewDto.Body params) {
        return service.datatables(
                new DataTablesRequest<>(
                        params.getDraw(),
                        params.getLength(),
                        params.getStart(),
                        params.getOrder().get(0).getDir(),
                        params.getOrder().get(0).getColumn(),
                        params.getRequest()
                )
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody List<Object> objects,
            @RequestParam String triwulan,
            @RequestParam String tahunBuku
    ) throws ParseException, IOException {

        if (triwulan.equals("TW1"))
            triwulan = "I";
        else if (triwulan.equals("TW2"))
            triwulan = "II";
        else if (triwulan.equals("TW3"))
            triwulan = "III";
        else if (triwulan.equals("TW4"))
            triwulan = "IV";

        BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

        Map<String, Object> params = new HashMap();
        params.put("triwulan", triwulan);
        params.put("tahunBuku", tahunBuku);
        params.put("logoLocation", logoImage);

        byte[] bytes = service.exportPdf(params, "/jasper/laporan_budget_review.jasper", objects);
        log.info("isi byte : {}", bytes);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportExcel(
            @RequestBody List<Object> objects,
            @RequestParam String triwulan,
            @RequestParam String tahunBuku
    ) throws ParseException, IOException {

        if (triwulan.equals("TW1"))
            triwulan = "I";
        else if (triwulan.equals("TW2"))
            triwulan = "II";
        else if (triwulan.equals("TW3"))
            triwulan = "III";
        else if (triwulan.equals("TW4"))
            triwulan = "IV";

        BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));

        Map<String, Object> params = new HashMap();
        params.put("triwulan", triwulan);
        params.put("tahunBuku", tahunBuku);
        params.put("logoLocation", logoImage);

        byte[] bytes = service.exportExcel(params, "/jasper/laporan_budget_review.jasper", objects);
        log.info("isi byte : {}", bytes);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @GetMapping("/listBudgetReview")
    @ResponseBody
    public ResponseEntity<?> findListBudgetReviewValid(
            @RequestParam String kodeTahunBuku,
            @RequestParam String triwulan
    ) {
        List<String> list = service.findListBudgetReviewValid(kodeTahunBuku, triwulan);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}

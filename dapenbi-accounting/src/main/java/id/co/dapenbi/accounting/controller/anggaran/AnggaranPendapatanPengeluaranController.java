package id.co.dapenbi.accounting.controller.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.anggaran.AnggaranPendapatanPengeluaranDto;
import id.co.dapenbi.accounting.dto.laporan.LaporanBudgetReviewDto;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranPendapatanPengeluaranService;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanBudgetReviewService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/akunting/anggaran/anggaran-pendapatan-pengeluaran")
public class AnggaranPendapatanPengeluaranController {

    @Autowired
    private AnggaranPendapatanPengeluaranService service;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/anggaran/anggaranPendapatanPengeluaran");
        modelAndView.addObject("listTahunBuku", tahunBukuService.listByStatusAktif());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<AnggaranPendapatanPengeluaranDto.Response> datatables(@RequestBody AnggaranPendapatanPengeluaranDto.Body params) {
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
            @RequestParam String tahunBuku
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("tahunBuku", tahunBuku);

        byte[] bytes = service.exportPdf(params, "/jasper/anggaran_pendapatan_pengeluaran.jasper", objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportExcel(
            @RequestBody List<Object> objects,
            @RequestParam String tahunBuku
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("tahunBuku", tahunBuku);

        byte[] bytes = service.exportExcel(params, "/jasper/anggaran_pendapatan_pengeluaran.jasper", objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

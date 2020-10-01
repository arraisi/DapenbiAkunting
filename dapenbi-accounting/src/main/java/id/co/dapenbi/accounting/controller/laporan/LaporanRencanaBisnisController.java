package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanRencanaBisnisDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanRencanaBisnis;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanRencanaBisnisService;
import id.co.dapenbi.accounting.service.impl.laporan.MasterLaporanService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/akunting/laporan/laporan-rencana-bisnis")
public class LaporanRencanaBisnisController {

    @Autowired
    private LaporanRencanaBisnisService laporanRencanaBisnisService;

    @Autowired
    private MasterLaporanService masterLaporanService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/laporan-rencana-bisnis");
        modelAndView.addObject("listLaporan", masterLaporanService.listByNamaTabelSortByUrutan("LAP_RB"));
        modelAndView.addObject("listTahunBuku", tahunBukuService.listTahunSort());
        modelAndView.addObject("listPeriode", periodeService.findAll());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getLaporanRencanaBisnisDatatables")
    @ResponseBody
    public DataTablesResponse<LaporanRencanaBisnis> datatables(@Valid @RequestBody LaporanRencanaBisnisDTO.LaporanRencanaBisnisDatatablesBody payload) {
        LaporanRencanaBisnis laporanRencanaBisnis = new LaporanRencanaBisnis();
        laporanRencanaBisnis.setIdLaporanHdr(payload.getLaporanRencanaBisnis().getIdLaporanHdr());
        laporanRencanaBisnis.setKodeThnBuku(payload.getLaporanRencanaBisnis().getKodeThnBuku());
        laporanRencanaBisnis.setKodePeriode(payload.getLaporanRencanaBisnis().getKodePeriode());
        laporanRencanaBisnis.setKodeDRI(payload.getLaporanRencanaBisnis().getKodeDRI());

        return laporanRencanaBisnisService.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        laporanRencanaBisnis
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody List<Object> objects,
            @RequestParam String namaLaporan
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("namaLaporan", namaLaporan);
//        params.put("periode", DateUtil.stringToDate(periode));

        byte[] bytes = laporanRencanaBisnisService.exportPdf(params, "/jasper/laporan_rencana_bisnis.jasper", objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportExcel(
            @RequestBody List<Object> objects,
            @RequestParam String namaLaporan
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("namaLaporan", namaLaporan);
//        params.put("periode", DateUtil.stringToDate(periode));

        byte[] bytes = laporanRencanaBisnisService.exportExcel(params, "/jasper/laporan_rencana_bisnis.jasper", objects);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

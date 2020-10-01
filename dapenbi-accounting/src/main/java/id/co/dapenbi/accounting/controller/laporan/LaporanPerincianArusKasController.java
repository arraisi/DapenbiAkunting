package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.ArusKasBulananDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuangan.JurnalIndividualDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIHeaderDTO;
import id.co.dapenbi.accounting.dto.parameter.ArusKasDTO;
import id.co.dapenbi.accounting.entity.laporan.SPIHeader;
import id.co.dapenbi.accounting.repository.laporan.InvestasiHeaderRepository;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanPerincianArusKasService;
import id.co.dapenbi.accounting.service.impl.laporan.TransaksiSPIService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/laporan-aruskas")
public class LaporanPerincianArusKasController {
    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private LaporanPerincianArusKasService service;

    @Autowired
    private PeriodeService periodeService;

    @GetMapping("")
    public ModelAndView showPerincianArusKas() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/laporanPerincianArusKas");
        modelAndView.addObject("periodeList", periodeService.findAll());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<ArusKasBulananDTO> datatables(
            @RequestBody ArusKasBulananDTO.DatatablesBody payload
    ) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getArusKasBulananDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestParam String periode
    ) {
        final List<ArusKasDTO> allForExport;
        final ArusKasDTO.TotalKas totalKas;
        try {
            allForExport = service.findAllForExport(periode);
            totalKas = service.findTotalKas(periode);
        } catch (SQLException e) {
            return ResponseEntity.noContent().build();
        }

        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        String[] tanggal = periode.split("-");
        Map<String, Object> params = new HashMap();
        params.put("periode", tanggal[2]+" "+month[Integer.parseInt(tanggal[1])-1]+" "+tanggal[0]);
        params.put("kasAwalPeriode", totalKas.getKasAwalPeriode());
        params.put("kasAkhirPeriode", totalKas.getKasAkhirPeriode());
        params.put("kasAktivitasInvestasi", totalKas.getKasAktivitasInvestasi());
        params.put("kasAktivitasOperasional", totalKas.getKasAktivitasOperasional());
        params.put("kasAktivitasPendanaan", totalKas.getKasAktivitasPendanaan());
        params.put("totalKasAktivitas", totalKas.getTotalKasAktivitas());

        byte[] bytes = service.exportPdf(params, allForExport);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestParam String periode
    ) {

        final List<ArusKasDTO> allForExport;
        final ArusKasDTO.TotalKas totalKas;
        try {
            allForExport = service.findAllForExport(periode);
            totalKas = service.findTotalKas(periode);
        } catch (SQLException e) {
            return ResponseEntity.noContent().build();
        }

        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        String[] tanggal = periode.split("-");
        Map<String, Object> params = new HashMap();
        params.put("periode", tanggal[2]+" "+month[Integer.parseInt(tanggal[1])-1]+" "+tanggal[0]);
        params.put("kasAwalPeriode", totalKas.getKasAwalPeriode());
        params.put("kasAkhirPeriode", totalKas.getKasAkhirPeriode());
        params.put("kasAktivitasInvestasi", totalKas.getKasAktivitasInvestasi());
        params.put("kasAktivitasOperasional", totalKas.getKasAktivitasOperasional());
        params.put("kasAktivitasPendanaan", totalKas.getKasAktivitasPendanaan());
        params.put("totalKasAktivitas", totalKas.getTotalKasAktivitas());

        byte[] bytes = service.exportExcel(params, allForExport);
        String encode = Base64.getEncoder().encodeToString(bytes);

        return ResponseEntity.ok(encode);
    }
}

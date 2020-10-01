package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.LaporanKeuanganDao;
import id.co.dapenbi.accounting.dto.laporan.LaporanKeuanganDTO;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.service.impl.laporan.LaporanKeuanganService;
import id.co.dapenbi.accounting.service.impl.laporan.MasterLaporanService;
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

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/laporan-keuangan")
public class LaporanKeuanganController {

    @Autowired
    private MasterLaporanService masterLaporanService;

    @Autowired
    private LaporanKeuanganService laporanKeuanganService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private LaporanKeuanganDao laporanKeuanganDao;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/laporan/laporan-keuangan");
        modelAndView.addObject("listLaporan", masterLaporanService.listByNamaTabelSortByUrutan("LAP_KEU"));
        modelAndView.addObject("listTahunBuku", tahunBukuService.listTahunSort());
        modelAndView.addObject("listPeriode", periodeService.findAll());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        return modelAndView;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getLaporanKeuanganDatatables")
    @ResponseBody
    public DataTablesResponse<LaporanKeuanganDTO.LaporanKeuangan> datatables(@Valid @RequestBody LaporanKeuanganDTO.LaporanKeuanganDatatablesBody payload) {
        LaporanKeuanganDTO.LaporanKeuangan laporanKeuangan = new LaporanKeuanganDTO.LaporanKeuangan();
        laporanKeuangan.setIdLaporanHeader(payload.getLaporanKeuangan().getIdLaporanHeader());
        laporanKeuangan.setKodeTahunBuku(payload.getLaporanKeuangan().getKodeTahunBuku());
        laporanKeuangan.setKodePeriode(payload.getLaporanKeuangan().getKodePeriode());
        laporanKeuangan.setKodeDRI(payload.getLaporanKeuangan().getKodeDRI());

        return laporanKeuanganService.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        laporanKeuangan
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestBody List<Object> objects,
            @RequestParam String namaLaporan,
            @RequestParam String idLaporanHeader,
            @RequestParam String kodeTahunBuku,
            @RequestParam String kodePeriode,
            @RequestParam String kodeDRI
    ) throws ParseException {

        List<String> tanggalLaporan = laporanKeuanganService.findTanggalLaporan(idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        log.info(" tanggal Laporan: {}", tanggalLaporan.get(0));

        log.info(" id header: {}, kodeTahunBuku: {}, kode periode:{}, kodeDRI: {}", idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);

        Map<String, Object> params = new HashMap();
        params.put("namaLaporan", namaLaporan);
//        params.put("periode", DateUtil.stringToDate(periode));

        PengaturanSistem pengaturanSistem = new PengaturanSistem();
        pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        LocalDate localDate = LocalDateTime.now().toLocalDate();

        Optional<TahunBuku> tahunBukuOptional = tahunBukuService.findById(kodeTahunBuku);
        TahunBuku tahunBuku = tahunBukuOptional.get();

        params.put("tanggalAkhir", "31 DESEMBER " + String.valueOf(Integer.parseInt(tahunBuku.getTahun()) -1));
        params.put("dateNow", localDate.getDayOfMonth() + " " + month[localDate.getMonthValue()-1]  + " " + localDate.getYear());
        params.put("direkturUtama", pengaturanSistem.getDirut());
        params.put("direktur", pengaturanSistem.getDiv());
        params.put("tanggalLaporan", tanggalLaporan.get(0));

        final byte[] bytes;
        if (namaLaporan.equals("PAJAK PENGHASILAN BADAN")) {
            params.put("periode", tanggalLaporan.get(0));
            final List<LaporanKeuanganDTO.PajakPenghasilanBadan> pajakPenghasilanBadan = laporanKeuanganService.findPajakPenghasilanBadan(kodePeriode, kodeDRI);
            bytes = laporanKeuanganService.exportPdfPajakPenghasilanBadan(params, pajakPenghasilanBadan);
        } else if (namaLaporan.equals("NERACA"))
            bytes = laporanKeuanganService.exportPdfNeraca(params, "/jasper/laporan_keuangan_neraca.jasper", idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        else
            bytes = laporanKeuanganService.exportPdf(params, "/jasper/laporan_keuangan_aset_neto.jasper", objects);
        

        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportExcel(
            @RequestBody List<Object> objects,
            @RequestParam String namaLaporan,
            @RequestParam String idLaporanHeader,
            @RequestParam String kodeTahunBuku,
            @RequestParam String kodePeriode,
            @RequestParam String kodeDRI
    ) throws ParseException {

        Map<String, Object> params = new HashMap();
        params.put("namaLaporan", namaLaporan);
//        params.put("periode", DateUtil.stringToDate(periode));

        PengaturanSistem pengaturanSistem = new PengaturanSistem();
        pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        LocalDate localDate = LocalDateTime.now().toLocalDate();

        Optional<TahunBuku> tahunBukuOptional = tahunBukuService.findById(kodeTahunBuku);
        TahunBuku tahunBuku = tahunBukuOptional.get();

        params.put("tanggalAkhir", "31 DESEMBER " + String.valueOf(Integer.parseInt(tahunBuku.getTahun()) -1));
        params.put("dateNow", localDate.getDayOfMonth() + " " + month[localDate.getMonthValue()-1]  + " " + localDate.getYear());
        params.put("direkturUtama", pengaturanSistem.getDirut());
        params.put("direktur", pengaturanSistem.getDiv());

        final byte[] bytes;
        if (namaLaporan.equals("PAJAK PENGHASILAN BADAN")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            params.put("periode", dateFormat.format(date));
            final List<LaporanKeuanganDTO.PajakPenghasilanBadan> pajakPenghasilanBadan = laporanKeuanganService.findPajakPenghasilanBadan(kodePeriode, kodeDRI);
            bytes = laporanKeuanganService.exportExcelPajakPenghasilanBadan(params, pajakPenghasilanBadan);
        } else if (namaLaporan.equals("NERACA")) {
            bytes = laporanKeuanganService.exportExcelNeraca(params, "/jasper/laporan_keuangan_neraca.jasper", idLaporanHeader, kodeTahunBuku, kodePeriode, kodeDRI);
        } else {
            bytes = laporanKeuanganService.exportExcel(params, "/jasper/laporan_keuangan_aset_neto.jasper", objects);
        }

        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @GetMapping("/perhitungan-angsuran/list")
    public ResponseEntity<List<LaporanKeuanganDTO.LaporanKeuangan>> listPerhitunganAngsuranPPH(
            @RequestParam String kodeDRI,
            @RequestParam String kodePeriode,
            @RequestParam String kodeTahunBuku
    ) {
        List<LaporanKeuanganDTO.LaporanKeuangan> data = laporanKeuanganService.listPerhitunganAngsuranPPH(kodeDRI, kodePeriode, kodeTahunBuku);
        if (data.size() == 0)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return ResponseEntity.ok(data);
    };
}

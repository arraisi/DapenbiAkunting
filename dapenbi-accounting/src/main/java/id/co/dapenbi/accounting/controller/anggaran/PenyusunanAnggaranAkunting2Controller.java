package id.co.dapenbi.accounting.controller.anggaran;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDTO;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.service.impl.anggaran.PenyusunanAnggaranAkuntingService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/akunting/anggaran/penyusunan-anggaran-new")
public class PenyusunanAnggaranAkunting2Controller {

    @Autowired
    private PenyusunanAnggaranAkuntingService service;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @GetMapping("")
    public ModelAndView showPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("listTahunBuku", tahunBukuService.listByStatusAktif());
        modelAndView.addObject("listPeriode", periodeService.getAll());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        modelAndView.setViewName("anggaran/penyusunanAnggaranAkunting2");
        return modelAndView;
    }

    @PostMapping("/find/daftar-rekening")
    public ResponseEntity<List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO>> getDaftarRekening(@RequestParam String kodeTahunBuku) throws ParseException {
        try {
            int tahunBuku = Integer.parseInt(kodeTahunBuku) - 1;
            log.info("tahun buku: {}", tahunBuku);
            List<PenyusunanAnggaranAkuntingDTO.DaftarRekeningDTO> daftarRekening = service.getDaftarRekening(String.valueOf(tahunBuku));
            return new ResponseEntity<>(daftarRekening, HttpStatus.OK);
        } catch (IncorrectResultSizeDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> findDetails(@RequestParam String noAnggaran) {
        return new ResponseEntity<>(service.findDetails(noAnggaran), HttpStatus.OK);
    }

    @PostMapping("/budget-review/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid PenyusunanAnggaranAkunting request, Principal principal) {
        service.save(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid PenyusunanAnggaranAkunting request, Principal principal) {
        service.delete(request.getNoAnggaran(), principal);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesResponse<PenyusunanAnggaranAkuntingDTO> datatables(
            @RequestBody PenyusunanAnggaranAkuntingDTO.DatatablesBody payload
    ) {
        return service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        payload.getPenyusunanAnggaranAkuntingDTO()
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping("/save-header-and-detail")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveBudgetAndDetails(@RequestBody(required = false) PenyusunanAnggaranAkuntingDTO request, Principal principal) {
        if (request.getNoAnggaran().isEmpty()) {
            String noAnggaran = service.generateNumberAnggaran(request.getKodeThnBuku().getKodeTahunBuku(), request.getKodePeriode().getKodePeriode());
            request.setNoAnggaran(noAnggaran);
        }
        try {
            log.info("masuk controller");
            service.saveHeaderAndDetails(request, principal);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatable")
    @ResponseBody
    public DataTablesOutput<PenyusunanAnggaranAkunting> datatable(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return service.dataTable(input);
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPdf(
            @RequestParam String noAnggaran,
            @RequestParam String tipeRekening,
            @RequestParam String kodeTahunBuku
    ) throws IOException {

        Map<String, Object> params = new HashMap();

        PengaturanSistem pengaturanSistem = new PengaturanSistem();
        pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        LocalDate localDate = LocalDateTime.now().toLocalDate();

        Optional<TahunBuku> tahunBukuOptional = tahunBukuService.findById(kodeTahunBuku);
        TahunBuku tahunBuku = tahunBukuOptional.get();

        params.put("dateNow", localDate.getDayOfMonth() + " " + month[localDate.getMonthValue()-1]  + " " + localDate.getYear());
        params.put("direkturUtama", pengaturanSistem.getDirut());
        params.put("direktur", pengaturanSistem.getDiv());
        BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
        params.put("logoLocation", logoImage);
        params.put("tipeRekening", tipeRekening);

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        params.put("tahun", tahunBuku.getTahun());

        byte[] bytes = service.exportPdf(params, noAnggaran, tipeRekening);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }


    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<String> exportExcel(
            @RequestParam String noAnggaran,
            @RequestParam String tipeRekening,
            @RequestParam String kodeTahunBuku,
            @RequestParam String periode
    ) throws ParseException, IOException {

        Map<String, Object> params = new HashMap();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date _periode = formatter.parse(periode);

        PengaturanSistem pengaturanSistem = new PengaturanSistem();
        pengaturanSistem = pengaturanSistemService.findByStatusAktif().get();
        String[] month = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        LocalDate localDate = LocalDateTime.now().toLocalDate();

        Optional<TahunBuku> tahunBukuOptional = tahunBukuService.findById(kodeTahunBuku);
        TahunBuku tahunBuku = tahunBukuOptional.get();

        params.put("dateNow", localDate.getDayOfMonth() + " " + month[localDate.getMonthValue()-1]  + " " + localDate.getYear());
        params.put("direkturUtama", pengaturanSistem.getDirut());
        params.put("direktur", pengaturanSistem.getDiv());

        Locale id = new Locale("in", "ID");
        String pattern = "dd MMMM yyyy";
        DateFormat dateFormat = new SimpleDateFormat(pattern, id);
        params.put("periode", dateFormat.format(_periode));
        params.put("tahun", tahunBuku.getNamaTahunBuku());
        BufferedImage logoImage = ImageIO.read(getClass().getResource("/jasper/images/dapenbi-logo.png"));
        params.put("logoLocation", logoImage);

        byte[] bytes = service.exportExcel(params, noAnggaran, tipeRekening);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

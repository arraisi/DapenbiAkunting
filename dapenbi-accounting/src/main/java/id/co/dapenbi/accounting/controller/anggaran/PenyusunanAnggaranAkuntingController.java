package id.co.dapenbi.accounting.controller.anggaran;

import id.co.dapenbi.accounting.dto.anggaran.AnggaranDTO;
import id.co.dapenbi.accounting.entity.anggaran.Anggaran;
import id.co.dapenbi.accounting.entity.anggaran.AnggaranLog;
import id.co.dapenbi.accounting.entity.anggaran.AnggaranSatker;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranLogService;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranSatkerService;
import id.co.dapenbi.accounting.service.impl.anggaran.AnggaranService;
import id.co.dapenbi.accounting.service.impl.master.LookupMasterService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import id.co.dapenbi.core.property.FileStorageProperties;
import id.co.dapenbi.core.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/akunting/anggaran/anggaran-akunting")
public class PenyusunanAnggaranAkuntingController {

    @Autowired
    private AnggaranService service;

    @Autowired
    private AnggaranSatkerService anggaranSatkerService;

    @Autowired
    private AnggaranLogService logService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private LookupMasterService lookupMasterService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("anggaran/penyusunanAnggaranAkunting");
        modelAndView.addObject("periodeList", service.findAllPeriode());
        modelAndView.addObject("tahunBukuList", tahunBukuService.listByStatusAktif());
        modelAndView.addObject("satkerList", lookupMasterService.findByJenisLookup("SATKER"));
        return modelAndView;
    }

    @GetMapping("/data-relasi")
    public ResponseEntity<BigDecimal> dataRelasi(@RequestParam Integer mataAnggaran, @RequestParam Integer kodeTahunBuku) {
        return ResponseEntity.ok(service.getRealisasiBerjalan(mataAnggaran, kodeTahunBuku.toString()));
    }

    @GetMapping("/data-at")
    public ResponseEntity<BigDecimal> dataAt(@RequestParam Long mataAnggaran, @RequestParam String kodeTahunBuku) {
        return ResponseEntity.ok(service.getDataAT(mataAnggaran, kodeTahunBuku));
    }

    @GetMapping("/no-at")
    public ResponseEntity<String> noAt() {
        return ResponseEntity.ok(service.generateNoAT());
    }

    @GetMapping("/default-value")
    public ResponseEntity<AnggaranDTO.Value> defaultValue() {
        return ResponseEntity.ok(service.getValue());
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(
            @RequestBody @Valid Anggaran request,
            @RequestParam String statusData,
            Principal principal) {
//        log.info("Request :{}", request);
        request.setStatusData(statusData);
        request.setCreatedBy(principal.getName());
        request.setCreatedDate(new Timestamp(new Date().getTime()));
        service.save(request);
        logService.save(new AnggaranLog(request, "CREATE"));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(
            @RequestBody @Valid Anggaran request,
            @RequestParam String statusData,
            Principal principal) {
        log.info("Request :{}", request);
        request.setStatusData(statusData); //"DRAFT"
        request.setUpdatedBy(principal.getName());
        request.setUpdatedDate(new Timestamp(new Date().getTime()));
        service.update(request);
        logService.save(new AnggaranLog(request, "UPDATE"));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/delete/{noAnggaran}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(
            @PathVariable String noAnggaran) {
        service.delete(noAnggaran);
        logService.deleteByNoAnggaran(noAnggaran);
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<Anggaran> datatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return service.penyusunanDataTable(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables-satker")
    @ResponseBody
    public DataTablesOutput<AnggaranSatker> datatablesSatker(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return anggaranSatkerService.datatables(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/mata-anggaran/datatables")
    @ResponseBody
    public DataTablesOutput<Rekening> mataAnggaranDatatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return service.findForMataAnggaranDataTable(input);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(required = false, value = "kodeRekening") String kodeRekening,
            @RequestPart(required = false, value = "tahunAnggaran") String tahunAnggaran
    ) {
        log.info("upload document: {}", file.getOriginalFilename());
        int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        String tahun = tahunAnggaran == null ? Integer.toString(year) : tahunAnggaran.equals("null") ? Integer.toString(year) : tahunAnggaran;
        try {
            fileStorageService = new FileStorageService(fileStorageProperties);
            String storeFile = fileStorageService.storeFile(file, kodeRekening, tahun);
            AnggaranDTO.Excel excel = new AnggaranDTO.Excel();
            excel.setFilePath(fileStorageProperties.getUploadDir() + File.separator + storeFile);
            excel.setListData(service.getExcelValue(excel.getFilePath()));
            return ResponseEntity.ok(excel);
        } catch (DataAccessException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/create-excel/{kodeRekening}")
    public ResponseEntity<?> createExcel(
            @PathVariable String kodeRekening,
            @RequestParam(required = false) String tahunAnggaran,
            @RequestParam(required = false) String kdThnBuku,
            @RequestParam(required = false) Integer idRekening) {
        log.info("Kode Rekening: {}", kodeRekening);
        int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        String tahun = tahunAnggaran == null ? Integer.toString(year) : tahunAnggaran;
        String version = service.getVersion(idRekening, kdThnBuku);
        try {
            String storeFile = fileStorageService.createExcelMataAnggaran(kodeRekening, tahun, version);
            AnggaranDTO.Excel excel = new AnggaranDTO.Excel();
            excel.setFilePath(storeFile);
            excel.setListData(service.getExcelValue(excel.getFilePath()));
            return ResponseEntity.ok(excel);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping(value = "/reupload")
    public ResponseEntity<?> reupload(
            @RequestBody AnggaranDTO.Excel excelValue
    ) {
        log.info("reupload {}", excelValue);
        try {
            service.reWriteExcel(excelValue);
            return ResponseEntity.ok("excel");
        } catch (DataAccessException | IOException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/excel-json")
    public ResponseEntity<?> excelJson(
            @RequestParam Integer idRekening,
            @RequestParam String kodeTahunBuku
    ) {
        try {
            List<List<Object>> lists = new ArrayList<>();
            Iterable<AnggaranSatker> anggaranSatkers = anggaranSatkerService.findAnggaranByIdRekening(idRekening, kodeTahunBuku);
            for (AnggaranSatker anggaran : anggaranSatkers) {
                List<List<Object>> listData = service.getExcelValue(anggaran.getFileLampiran());
                lists.addAll(listData);
            }
            AnggaranDTO.Excel excel = new AnggaranDTO.Excel();
            excel.setListData(lists);
            return ResponseEntity.ok(excel);
        } catch (DataAccessException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/excel-json/file-path")
    public ResponseEntity<?> excelJson(
            @RequestParam String filePath
    ) {
        try {
            AnggaranDTO.Excel excel = new AnggaranDTO.Excel();
            excel.setFilePath(filePath);
            excel.setListData(service.getExcelValue(filePath));
            return ResponseEntity.ok(excel);
        } catch (DataAccessException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/findByIdRekening/{idRekening}")
    public ResponseEntity<?> findByIdRekening(
            @PathVariable Integer idRekening
    ) {
        try {
            final Optional<Anggaran> anggaran = service.findByIdRekening(idRekening);
            if (anggaran.isPresent()) {
                return ResponseEntity.ok(anggaran);
            } else return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-saldo-current-pa-fa")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateSaldoCurrentPAFA(@RequestBody Anggaran anggaran) {
        service.updateSaldoCurrentPAFA(anggaran.getIdRekening(), anggaran.getTotalAnggaran());
        return ResponseEntity.ok().build();
    }
}

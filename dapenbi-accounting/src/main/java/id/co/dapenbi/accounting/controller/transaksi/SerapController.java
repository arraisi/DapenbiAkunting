package id.co.dapenbi.accounting.controller.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.transaksi.SerapDTO;
import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import id.co.dapenbi.accounting.mapper.RekeningMapper;
import id.co.dapenbi.accounting.mapper.SerapMapper;
import id.co.dapenbi.accounting.mapper.ValidasiSerapMapper;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import id.co.dapenbi.accounting.service.impl.transaksi.SerapDetailService;
import id.co.dapenbi.accounting.service.impl.transaksi.SerapService;
import id.co.dapenbi.accounting.service.impl.transaksi.ValidasiSerapService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/akunting/transaksi/serap")
@Slf4j
public class SerapController {

    @Autowired
    private SerapService serapService;

    @Autowired
    private SerapDetailService serapDetailService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private ValidasiSerapService validasiSerapService;

    @Autowired
    private RekeningService rekeningService;

    @GetMapping("")
    public ModelAndView showSerap() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaksi/serapAnggaran");
        modelAndView.addObject("rekeningList", rekeningService.findByTipeRekening("BIAYA"));
        modelAndView.addObject("tahunBukuList", tahunBukuService.listTahunSort());
        modelAndView.addObject("periodeList", periodeService.findAll());
        return modelAndView;
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid Serap request, Principal principal) {
        request.setCreatedBy(principal.getName());

        ValidasiSerap validasiSerap = ValidasiSerapMapper.instance.serapToValidasiSerap(request);
        validasiSerap.setAktivitas("CREATE");

        List<SerapDetail> serapDetails = request.getSerapDetail();
        Serap serap = serapService.save(request);
//        log.info("{}", request);
        for (SerapDetail detail : serapDetails) {
//            detail.setIdSerapDetail(UUID.randomUUID().toString());
//            log.info("{}", detail.getRekening().getIdRekening());
            detail.setNoSerap(serap);
            detail.setCreatedBy(principal.getName());
            serapDetailService.save(detail);
        }
        validasiSerapService.save(validasiSerap);
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@RequestBody @Valid Serap request, Principal principal) {
        request.setUpdatedBy(principal.getName());

        ValidasiSerap validasiSerap = ValidasiSerapMapper.instance.serapToValidasiSerap(request);
        validasiSerap.setAktivitas("UPDATE");

        List<SerapDetail> serapDetails = request.getSerapDetail();
        Serap serap = serapService.update(request);
        serapDetailService.deleteByNoSerap(serap.getNoSerap());
        for(SerapDetail detail : serapDetails) {
//            log.info("{}", detail);
            detail.setNoSerap(serap);
            detail.setUpdatedBy(principal.getName());
            serapDetailService.update(detail);
        }
        validasiSerapService.save(validasiSerap);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update-status-data")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateStatusData(@RequestBody Serap serap, Principal principal) {
        serap.setUpdatedBy(principal.getName());
        serap.setUserValidasi(principal.getName());
        serap.setTglValidasi(Timestamp.valueOf(LocalDateTime.now()));
        serapService.updateStatusData(serap);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/findById")
    public ResponseEntity<SerapDTO.Serap> findById(@PathVariable("id") String id) {
        Optional<Serap> data = serapService.findById(id);
//        log.info("{}", data.get().getTahunBuku());
//        TahunBuku tahunBuku = tahunBukuService.findById(data.get().getKodeTahunBuku()).get();
        Periode periode = periodeService.findById(data.get().getKodePeriode()).get();
        SerapDTO.Serap serapDTO = SerapMapper.INSTANCE.SerapToSerapDTO(data.get());
        serapDTO.setTahunBuku(data.get().getTahunBuku());
        serapDTO.setPeriode(periode);
        if(data.isPresent()) {
            return ResponseEntity.ok(serapDTO);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}/findByIdDTO")
    public ResponseEntity<SerapDTO.Serap> findByIdDTO(@PathVariable("id") String id) {
        Optional<SerapDTO.Serap> data = serapService.findByIdDTO(id);

        if(data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid Serap serap) {
        serapDetailService.deleteByNoSerap(serap.getNoSerap());
        serapService.delete(serap.getNoSerap());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getSerapDatatables")
    @ResponseBody
    public DataTablesOutput<Serap> datatables(@RequestBody @Valid DataTablesInput input) {
        return serapService.datatables(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getSerapDTODatatables")
    @ResponseBody
    public DataTablesResponse<Serap> datatables(@RequestBody SerapDTO.SerapDatatablesBody payload) {
        Serap serap = SerapMapper.INSTANCE.SerapDTOToSerap(payload.getSerap());
        return serapService.datatablesService(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        serap
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/getSerapRekeningDatatables")
    @ResponseBody
    public DataTablesResponse<Rekening> datatablesRekening(@RequestBody SerapDTO.SerapDatatablesBody payload) {
        Rekening rekening = RekeningMapper.INSTANCE.rekeningDTOToRekening(payload.getRekening());
//        rekening.setTipeRekening(payload.getRekening().getTipeRekening());
//        rekening.setLevelRekening(payload.getRekening().getLevelRekening());
        return serapService.datatablesRekeningService(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        rekening
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping("/getNoSerap")
    public ResponseEntity<String> getNoSerap() {
        return ResponseEntity.ok(serapService.getNoSerap());
    }

    @PostMapping(value = "/export-pdf", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportPDF(@RequestBody Map<String, Object> object) {
        byte[] bytes = serapService.exportPDF("/jasper/laporan_serap.jasper", object);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }

    @PostMapping(value = "/export-excel", headers = "Accept=*/*", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> exportEXCEL(@RequestBody Map<String, Object> object) {
        byte[] bytes = serapService.exportEXCEL("/jasper/laporan_serap.jasper", object);
        String encode = Base64.getEncoder().encodeToString(bytes);
        return ResponseEntity.ok(encode);
    }
}

package id.co.dapenbi.accounting.controller.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.laporan.LaporanDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanRekeningDTO;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.laporan.LaporanHeader;
import id.co.dapenbi.accounting.entity.laporan.LaporanRekening;
import id.co.dapenbi.accounting.service.impl.laporan.MasterLaporanService;
import id.co.dapenbi.accounting.service.impl.master.LookupMasterService;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import id.co.dapenbi.accounting.service.impl.parameter.PeriodeService;
import id.co.dapenbi.accounting.service.impl.parameter.RekeningService;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
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
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/akunting/laporan/master-laporan")
public class MasterLaporanController {

    @Autowired
    private MasterLaporanService service;

    @Autowired
    private RekeningService rekeningService;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private PeriodeService periodeService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    @Autowired
    private LookupMasterService lookupMasterService;

    @GetMapping("")
    public ModelAndView getPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("laporan/master-laporan");
        modelAndView.addObject("listRekening", rekeningService.findAllByOrder());
        modelAndView.addObject("listTahunBuku", tahunBukuService.listTahunSort());
        modelAndView.addObject("listPeriode", periodeService.findAll());
        modelAndView.addObject("pengaturanSistem", pengaturanSistemService.findByStatusAktif().get());
        modelAndView.addObject("listLookup", lookupMasterService.findByJenisLookup("JENIS_LAPORAN"));
        return modelAndView;
    }

    @GetMapping("/detail/{idLapHeader}")
    public ResponseEntity<?> getDetail(@PathVariable Integer idLapHeader) {
        Iterable<LaporanDetail> details = service.findByLapHeader(idLapHeader);
        if (IterableUtils.toList(details).size() > 0) return ResponseEntity.ok(IterableUtils.toList(details));
        else return ResponseEntity.noContent().build();
    }

    @PostMapping("/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(
            @RequestBody @Valid LaporanHeader request,
            Principal principal) {
//        log.info("Request :{}", request);
//        service.save(request, principal.getName());
        request.setCreatedBy(principal.getName());
        request.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.saveLaporanHeader(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(
            @RequestBody @Valid LaporanHeader request,
            Principal principal) {
//        log.info("Request :{}", request);
//        service.update(request, principal.getName());
        request.setUpdatedBy(principal.getName());
        request.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.updateLaporanHeader(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/delete/{idLapHeader}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(
            @PathVariable Integer idLapHeader) {
        service.delete(idLapHeader);
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<LaporanHeader> datatables(
            @Valid @RequestBody DataTablesInput input
    ) {
        return service.getDataTablesOutput(input);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/laporan-rumus/datatables")
    @ResponseBody
    public DataTablesResponse<LaporanDetail> laporanRumusDatatables(@RequestBody LaporanDetailDTO.DatatablesBody payload) {
        LaporanDetail laporanDetail = new LaporanDetail();
        laporanDetail.setLaporanHeader(payload.getLaporanDetail().getLaporanHeader());
        return service.laporanRumusDatatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        laporanDetail
                ),
                payload.getSearch().getValue()
        );
    }

    @GetMapping("/laporan-rumus/{id}")
    public ResponseEntity<?> findByIdLaporanDetail(@PathVariable("id") Integer idLaporanDetail) {
        Optional<LaporanDetail> data = service.findByIdLaporanDetail(idLaporanDetail);
        if (data.isPresent())
            return ResponseEntity.ok(data.get());
        else
            return ResponseEntity.noContent().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/laporan-rekening/datatables")
    @ResponseBody
    public DataTablesResponse<LaporanRekening> laporanRekeningDatatables(@RequestBody LaporanRekeningDTO.DatatablesBody payload) {
        LaporanRekening laporanRekening = new LaporanRekening();
        laporanRekening.setIdLaporanDtl(payload.getLaporanRekening().getIdLaporanDtl());
        return service.laporanRekeningDatatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        laporanRekening
                ),
                payload.getSearch().getValue()
        );
    }

    @PostMapping("/laporan-detail/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveLaporanDetail(@RequestBody LaporanDetail laporanDetail, Principal principal) {
        laporanDetail.setCreatedBy(principal.getName());
        laporanDetail.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.saveLaporanDetail(laporanDetail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/laporan-detail/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateLaporanDetail(@RequestBody LaporanDetail laporanDetail, Principal principal) {
        laporanDetail.setUpdatedBy(principal.getName());
        laporanDetail.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.updateLaporanDetail(laporanDetail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/laporan-detail/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> deleteLaporanDetail(@RequestBody LaporanDetail laporanDetail) {
        service.deleteLaporanDetail(laporanDetail.getIdLaporanDtl());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/laporan-rekening/save")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveLaporanRekening(@RequestBody LaporanRekening laporanRekening, Principal principal) {
        laporanRekening.setCreatedBy(principal.getName());
        laporanRekening.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.saveLaporanRekening(laporanRekening);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/laporan-rekening/update")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateLaporanRekening(@RequestBody LaporanRekening laporanRekening, Principal principal) {
        laporanRekening.setUpdatedBy(principal.getName());
        laporanRekening.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.updateLaporanRekening(laporanRekening);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/laporan-rekening/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> deleteLaporanRekening(@RequestBody LaporanRekening laporanRekening) {
        service.deleteLaporanRekening(laporanRekening.getIdLaporanRek());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/proses")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> prosesMasterLaporan(@RequestBody Map<String, String> body, Principal principal) throws ParseException {
//        String namaTabel = body.get("namaTabel");
//        if (namaTabel.equals("LAP_KEU")) {
//            List<LaporanHeader> laporanHeaderList = service.listByNamaTabelSortByUrutan(namaTabel);
//            for (LaporanHeader laporanHeader : laporanHeaderList) {
//                body.put("namaLaporan", laporanHeader.getNamaLaporan());
//                body.put("idLaporanHdr", laporanHeader.getIdLaporanHdr().toString());
//                body.put("namaTabel", laporanHeader.getNamaTabel());
//                service.prosesMasterLaporanKeuangan(body, principal);
//            }
//        } else if (namaTabel.equals("LAP_RB")) {
//            List<LaporanHeader> laporanHeaderList = service.listByNamaTabelSortByUrutan(namaTabel);
//            for (LaporanHeader laporanHeader : laporanHeaderList) {
//                body.put("namaLaporan", laporanHeader.getNamaLaporan());
//                body.put("idLaporanHdr", laporanHeader.getIdLaporanHdr().toString());
//                body.put("namaTabel", laporanHeader.getNamaTabel());
//                service.prosesMasterLaporanRencanaBisnis(body, principal);
//            }
//        }
        List<LaporanHeader> laporanHeaderList = service.listByNamaTabelSortByUrutan("LAP_KEU");
        service.deleteByTahunBukuAndPeriodeAndDRI(body.get("kodeTahunBuku"), body.get("kodePeriode"), body.get("dri"));
        for (LaporanHeader laporanHeader : laporanHeaderList) {
            body.put("namaLaporan", laporanHeader.getNamaLaporan());
            body.put("idLaporanHdr", laporanHeader.getIdLaporanHdr().toString());
            body.put("namaTabel", laporanHeader.getNamaTabel());
            service.prosesMasterLaporanKeuangan(body, principal);
        }
        return ResponseEntity.ok().build();
    }
}

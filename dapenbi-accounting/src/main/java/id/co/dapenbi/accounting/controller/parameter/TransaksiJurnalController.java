package id.co.dapenbi.accounting.controller.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.mapper.SerapMapper;
import id.co.dapenbi.accounting.mapper.TransaksiJurnalMapper;
import id.co.dapenbi.accounting.service.impl.parameter.TransaksiJurnalService;
import lombok.extern.slf4j.Slf4j;
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
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("")
public class TransaksiJurnalController {

    @Autowired
    private TransaksiJurnalService service;

    @GetMapping("/akunting/parameter/transaksi-jurnal")
    public ModelAndView showTransaksiJurnal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/transaksi-jurnal");
        return modelAndView;
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/add")
    public ModelAndView showAddJurnal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("parameter/transaksiJurnalAdd");
        return modelAndView;
    }

    @PostMapping("/api/akunting/parameter/transaksi-jurnal/")
    public ResponseEntity<Integer> save(@RequestBody @Valid TransaksiJurnalDTO request, Principal principal) {
        return new ResponseEntity<>(service.save(request), HttpStatus.CREATED);
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/{id}/findById")
    public ResponseEntity<TransaksiJurnal> findById(@PathVariable("id") Integer id) {
        Optional<TransaksiJurnal> data = service.findById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/akunting/parameter/transaksi-jurnal/findDataJurnal")
    public ResponseEntity<?> findDataJurnal(@RequestParam String tipeRekening) {
        List<TransaksiJurnalDTO> data = service.findDataJurnal(tipeRekening);
        if (!data.isEmpty()) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/findAll")
    public ResponseEntity<Iterable<TransaksiJurnal>> findAll() {
        Iterable<TransaksiJurnal> data = service.findAll();
        if (data != null) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/findByKodeTransaksi/{kodeTransaksi}")
    public ResponseEntity<List<TransaksiJurnalDTO>> findByKodeTransaksi(@PathVariable String kodeTransaksi) {
        List<TransaksiJurnalDTO> data = service.findByKodeTransaksi(kodeTransaksi);
        if (!data.isEmpty()) {
            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/findPajakPph")
    public ResponseEntity<BigDecimal> findPajakPph(@RequestParam String kodePeriode, @RequestParam String tahunBuku) {
        final BigDecimal pajakPph = service.findPajakPph(kodePeriode, tahunBuku);
        return ResponseEntity.ok(pajakPph);
    }

    @GetMapping("/api/akunting/parameter/transaksi-jurnal/findHutangPph")
    public ResponseEntity<BigDecimal> findHutangPph(@RequestParam String kodePeriode, @RequestParam String tahunBuku) {
        final BigDecimal pajakPph = service.findHutangPph(kodePeriode, tahunBuku);
        return ResponseEntity.ok(pajakPph);
    }

    @PostMapping("/api/akunting/parameter/transaksi-jurnal/update")
    public ResponseEntity<?> update(@RequestBody @Valid TransaksiJurnal request) {
        service.update(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/akunting/parameter/transaksi-jurnal/delete")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid TransaksiJurnal request) {
        service.delete(request.getIdTransaksiJurnal());
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/transaksi-jurnal/getDatatables")
    @ResponseBody
    public DataTablesOutput<TransaksiJurnalDTO.DataTables> getTransaksiJurnalDataTables(
            @Valid @RequestBody DataTablesInput input
    ) {
        return this.service.findForDataTable(input);
    }

//    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/transaksi-jurnal/datatables")
//    @ResponseBody
//    public DataTablesResponse<TransaksiJurnal> datatables(@RequestBody TransaksiJurnalDTO.DatatablesBody payload) {
////        TransaksiJurnal transaksiJurnal = TransaksiJurnalMapper.INSTANCE.dtoToEntity(payload.getTransaksiJurnal());
//        TransaksiJurnal transaksiJurnal = payload.getTransaksiJurnal();
//
//        return service.datatables(
//                new DataTablesRequest<>(
//                        payload.getDraw(),
//                        payload.getLength(),
//                        payload.getStart(),
//                        payload.getOrder().get(0).getDir(),
//                        payload.getOrder().get(0).getColumn(),
//                        transaksiJurnal
//                ),
//                payload.getSearch().getValue()
//        );
//    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/api/akunting/parameter/transaksi-jurnal/datatables")
    @ResponseBody
    public DataTablesResponse<TransaksiJurnal> datatables(@RequestBody TransaksiJurnalDTO.DatatablesBody payload) {
        TransaksiJurnal transaksiJurnal = payload.getTransaksiJurnal();
        log.info("{}", transaksiJurnal);
        DataTablesResponse<TransaksiJurnal> datatables = service.datatables(
                new DataTablesRequest<>(
                        payload.getDraw(),
                        payload.getLength(),
                        payload.getStart(),
                        payload.getOrder().get(0).getDir(),
                        payload.getOrder().get(0).getColumn(),
                        transaksiJurnal
                ),
                payload.getSearch().getValue()
        );
        return datatables;
    }
}

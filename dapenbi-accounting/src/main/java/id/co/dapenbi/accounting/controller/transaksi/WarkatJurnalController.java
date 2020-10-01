package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.service.impl.transaksi.WarkatJurnalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/api/akunting/warkat-jurnal")
public class WarkatJurnalController {

    @Autowired
    private WarkatJurnalService service;

    @PostMapping("/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody @Valid WarkatJurnal request, Principal principal) {
        request.setCreatedBy(principal.getName());
        request.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/save/all")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveAll(@RequestBody @Valid List<WarkatJurnal> request, Principal principal) {
        Stream<WarkatJurnal> warkatJurnalStream = request.stream().map(warkatJurnal -> {
            warkatJurnal.setCreatedBy(principal.getName());
            warkatJurnal.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            return warkatJurnal;
        });
        service.saveAll(warkatJurnalStream.collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> delete(@RequestBody @Valid WarkatJurnal warkatJurnal) {
        service.delete(warkatJurnal.getIdWarkatJurnal());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/findByNoWarkat")
    @ResponseBody
    public ResponseEntity<List<WarkatJurnal>> findByNoWarkat(@RequestParam String noWarkat) {
        return new ResponseEntity<>(this.service.findByNoWarkat(noWarkat), HttpStatus.OK);
    }

    @GetMapping("/findByNoWarkatDRI2")
    @ResponseBody
    public ResponseEntity<List<WarkatJurnal>> findByNoWarkatDRI2(@RequestParam String noWarkat) {
        return new ResponseEntity<>(this.service.findByNoWarkatDRI2(noWarkat), HttpStatus.OK);
    }

    @GetMapping("/findAllByNoWarkat")
    @ResponseBody
    public ResponseEntity<Iterable<WarkatJurnalDTO.datatables>> getAllByNoWarkat(@RequestParam String noWarkat) {
        return new ResponseEntity<>(this.service.getAllByNoWarkat(noWarkat), HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/datatables")
    @ResponseBody
    public DataTablesOutput<WarkatJurnal> getTransaksiDataTable(
            @Valid @RequestBody DataTablesInput input
    ) {
        log.info("{}", input);
        return this.service.findForDataTable(input);
    }
}

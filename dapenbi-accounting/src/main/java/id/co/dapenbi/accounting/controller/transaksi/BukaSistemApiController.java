package id.co.dapenbi.accounting.controller.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.BukaSistemResponse;
import id.co.dapenbi.accounting.service.impl.transaksi.BukaSistemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/akunting/buka-sistem")
public class BukaSistemApiController {

    @Autowired
    private BukaSistemService service;

    @GetMapping("/tgl-buka-buku")
    public ResponseEntity<BukaSistemResponse> getTglBukaBuku() {
        try {
            return ResponseEntity.ok(
                    new BukaSistemResponse(
                            true,
                            "Success",
                            service.getTglBuka()
                    )
            );
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok(this.responseFail(e.getLocalizedMessage()));
        }
    }

    @GetMapping("/tgl-buka-buku-sebelum")
    public ResponseEntity<BukaSistemResponse> getTglBukaBukuSebelum() {
        try {
            return ResponseEntity.ok(
                    new BukaSistemResponse(
                            true,
                            "Success",
                            service.getTglBukaMin1()
                    )
            );
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok(this.responseFail(e.getLocalizedMessage()));
        }
    }

    private BukaSistemResponse responseFail(String message) {
        return new BukaSistemResponse(
                false,
                message,
                null
        );
    }
}

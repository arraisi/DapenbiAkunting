package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.TutupHarianDao;
import id.co.dapenbi.accounting.dto.transaksi.PengaturanSistemDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.dto.transaksi.TutupHarianDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.service.impl.parameter.PengaturanSistemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TutupHarianService {

    @Autowired
    private TutupHarianDao dao;

    @Autowired
    private SaldoService saldoService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    @Autowired
    private PengaturanSistemService pengaturanSistemService;

    public TutupHarianDTO findTutupHarian() {
        PengaturanSistemDTO pengaturanSistemDTO = pengaturanSistemService.findByCreatedDate().get();
        return dao.getTutupHarian(pengaturanSistemDTO.getTglTransaksi());
    }

    @Transactional
    public int post(TutupHarianDTO request, Principal principal) throws Exception {
        final int i = dao.deleteByTglSaldo();
        int response = dao.retrieveSaldoFAToSaldo(request, principal.getName());
        if (response != 0) {
            // update acc parameter set status open C where status aktif 1
            return dao.updateACCParameter();
        }
        return 0;
    }
}

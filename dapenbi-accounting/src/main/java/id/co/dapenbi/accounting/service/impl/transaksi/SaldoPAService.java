package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoPA;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.repository.transaksi.SaldoPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SaldoPAService {

    @Autowired
    private SaldoPARepository saldoPARepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateFromApprovalSerap(SerapDetail serapDetail) {
        Optional<SaldoPA> saldoPAOptional = saldoPARepository.findById(serapDetail.getRekening().getIdRekening());
        SaldoPA saldoPA = saldoPAOptional.get();

        BigDecimal serapTambah = saldoPA.getSerapTambah() == null ? new BigDecimal(0) : saldoPA.getSerapTambah();
        BigDecimal serapKurang = saldoPA.getSerapKurang() == null ? new BigDecimal(0) : saldoPA.getSerapKurang();
        BigDecimal saldoAnggaran = saldoPA.getSaldoAnggaran() == null ? new BigDecimal(0) : saldoPA.getSaldoAnggaran();
        saldoPA.setSerapTambah(serapTambah.add(serapDetail.getJumlahPenambah()));
        saldoPA.setSerapKurang(serapKurang.add(serapDetail.getJumlahPengurang()));
        saldoPA.setSaldoAnggaran(saldoAnggaran.add(serapDetail.getJumlahPenambah()).subtract(serapDetail.getJumlahPengurang()));
        saldoPARepository.updateFromApprovalSerap(
                saldoPA.getSerapTambah(),
                saldoPA.getSerapKurang(),
                saldoPA.getSaldoAnggaran(),
                serapDetail.getRekening().getIdRekening()
        );
    }
}

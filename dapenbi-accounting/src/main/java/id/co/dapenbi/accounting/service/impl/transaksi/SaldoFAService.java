package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SaldoFA;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.repository.transaksi.SaldoFARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SaldoFAService {

    @Autowired
    private SaldoFARepository saldoFARepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateFromApprovalSerap(SerapDetail serapDetail) {
        Optional<SaldoFA> saldoFAOptional = saldoFARepository.findById(serapDetail.getRekening().getIdRekening());
        SaldoFA saldoFA = saldoFAOptional.get();

        BigDecimal serapTambah = saldoFA.getSerapTambah() == null ? new BigDecimal(0) : saldoFA.getSerapTambah();
        BigDecimal serapKurang = saldoFA.getSerapKurang() == null ? new BigDecimal(0) : saldoFA.getSerapKurang();
        BigDecimal saldoAnggaran = saldoFA.getSaldoAnggaran() == null ? new BigDecimal(0) : saldoFA.getSaldoAnggaran();
        saldoFA.setSerapTambah(serapTambah.add(serapDetail.getJumlahPenambah()));
        saldoFA.setSerapKurang(serapKurang.add(serapDetail.getJumlahPengurang()));
        saldoFA.setSaldoAnggaran(saldoAnggaran.add(serapDetail.getJumlahPenambah()).subtract(serapDetail.getJumlahPengurang()));
        saldoFARepository.updateFromApprovalSerap(
                saldoFA.getSerapTambah(),
                saldoFA.getSerapKurang(),
                saldoFA.getSaldoAnggaran(),
                serapDetail.getRekening().getIdRekening()
        );
    }
}

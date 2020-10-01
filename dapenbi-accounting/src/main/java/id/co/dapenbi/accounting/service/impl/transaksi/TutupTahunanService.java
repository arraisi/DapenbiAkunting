package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.TutupTahunanDao;
import id.co.dapenbi.accounting.dto.transaksi.TutupTahunanDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TutupTahunanService {

    @Autowired
    private TutupTahunanDao dao;

    @Autowired
    private SaldoService saldoService;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    public TutupTahunanDTO findTutupTahunan(String jenisWarkat) {
        return dao.getTutupTahunan(jenisWarkat);
    }

    @Transactional
    public List<Saldo> post(TutupTahunanDTO request, Principal principal) {
        List<Saldo> saldos = new ArrayList<>();
        List<SaldoCurrent> saldoCurrents = saldoCurrentService.findAllUpdatedThisYear();
        if (!saldoCurrents.isEmpty()) {
            for (SaldoCurrent saldoCurrent : saldoCurrents) {
                Saldo saldo = new Saldo();
                saldo.setIdRekening(saldoCurrent.getIdRekening());
                saldo.setKodeDri("3");
                saldo.setKodeRekening(saldoCurrent.getKodeRekening());
                saldo.setNamaRekening(saldoCurrent.getNamaRekening());
                saldo.setKodeTahunBuku(request.getKodeThnBuku());
                saldo.setKodePeriode(request.getKodePeriode());
                saldo.setTglSaldo(Timestamp.valueOf(LocalDateTime.now()));
                saldo.setSaldoAwal(saldoCurrent.getSaldoAwal());
                saldo.setSaldoDebet(saldoCurrent.getSaldoDebet());
                saldo.setSaldoKredit(saldoCurrent.getSaldoKredit());
                saldo.setSaldoAkhir(saldoCurrent.getSaldoAkhir());
                saldo.setNilaiAnggaran(saldoCurrent.getNilaiAnggaran());
                saldo.setSerapTambah(saldoCurrent.getSerapTambah());
                saldo.setSerapKurang(saldoCurrent.getSerapKurang());
                saldo.setSaldoAnggaran(saldoCurrent.getSaldoAnggaran());
                saldo.setCreatedBy(principal.getName());
                saldo.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                Saldo savedSaldo = saldoService.save(saldo);
                saldos.add(savedSaldo);
            }
        }
        return saldos;
    }

    public Integer checkTransaksiSaldo(String kodeDRI, Timestamp tglTransaksi) {
        return dao.checkTransaksiSaldo(kodeDRI, tglTransaksi);
    }

    public void postTahunan(String tglTransaksi, String kodeTahunBuku, String kodePeriode, String kodeDRI, Principal principal) {
        dao.deleteSaldoByTglTransaksi(tglTransaksi, kodeDRI);
        dao.copySaldoFromSaldoFA(kodeTahunBuku, kodePeriode, principal.getName(), kodeDRI);
    }
}

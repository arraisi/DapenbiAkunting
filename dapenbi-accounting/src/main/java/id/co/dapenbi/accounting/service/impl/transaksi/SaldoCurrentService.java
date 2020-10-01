package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.SaldoCurrentDao;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.repository.transaksi.SaldoCurrentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SaldoCurrentService {

    @Autowired
    private SaldoCurrentDao saldoCurrentDao;

    @Autowired
    private SaldoCurrentRepository saldoCurrentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SaldoCurrent updateSaldoCurrentSerap(SaldoCurrent saldoCurrent) {
        return saldoCurrentDao.updateSaldoCurrentSerap(saldoCurrent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateSaldoCurrentWarkatJurnal(SaldoCurrent saldoCurrent) {
        saldoCurrentRepository.updateSaldoCurrentWarkatJurnal(saldoCurrent.getSaldoDebet(), saldoCurrent.getSaldoKredit(), saldoCurrent.getSaldoAkhir(), saldoCurrent.getIdRekening());
    }

    public Optional<SaldoCurrent> findByIdRekening(Integer id) {
        return saldoCurrentRepository.findById(id);
    }

    public List<SaldoCurrent> findAllSaldoCurrentUpdatedToday() {
        return saldoCurrentDao.findAllSaldoCurrentUpdatedToday();
    }

    public List<SaldoDTO> findAllSaldoFAUpdatedToday() {
        return saldoCurrentDao.findAllSaldoFAUpdatedToday();
    }

    public List<SaldoCurrent> findAllUpdatedThisYear() {
        return saldoCurrentDao.findAllUpdatedThisYear();
    }

    public DataTablesOutput<SaldoCurrent> datatables(DataTablesInput input) {
        return saldoCurrentRepository.findAll(input);
    }
}

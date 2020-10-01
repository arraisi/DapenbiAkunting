package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.JurnalPendapatanDao;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPendapatanDTO;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.repository.transaksi.JurnalPendapatanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JurnalPendapatanService {

    @Autowired
    private JurnalPendapatanRepository jurnalPendapatanRepository;

    @Autowired
    private JurnalPendapatanDao jurnalPendapatanDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat save(Warkat warkat) {
        warkat.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return jurnalPendapatanRepository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat update(Warkat warkat) {
        warkat.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return jurnalPendapatanRepository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStatudData(Warkat warkat) {
        jurnalPendapatanRepository.updateStatusData(warkat.getNoWarkat(), warkat.getStatusData());
    }

    public Optional<Warkat> findById(String id) {
        return jurnalPendapatanDao.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        jurnalPendapatanRepository.deleteWarkatById(id);
    }

    public DataTablesResponse<JurnalPendapatanDTO> datatables(DataTablesRequest<JurnalPendapatanDTO> params, String search) {
        List<JurnalPendapatanDTO> data = jurnalPendapatanDao.datatables(params, search);
        Long rowCount = jurnalPendapatanDao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<WarkatJurnal> findJurnalPendapatanKredits() {
        return jurnalPendapatanDao.findJurnalPendapatanKredits();
    }
}

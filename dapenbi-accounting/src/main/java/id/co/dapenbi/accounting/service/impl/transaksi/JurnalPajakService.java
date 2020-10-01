package id.co.dapenbi.accounting.service.impl.transaksi;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.transaksi.JurnalPajakDao;
import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.repository.transaksi.JurnalPajakRepository;
import id.co.dapenbi.accounting.repository.transaksi.WarkatJurnalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JurnalPajakService {

    @Autowired
    private JurnalPajakRepository jurnalPajakRepository;

    @Autowired
    private JurnalPajakDao jurnalPajakDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat save(Warkat warkat) {
        warkat.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return jurnalPajakRepository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Warkat update(Warkat warkat) {
        warkat.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return jurnalPajakRepository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateStatudData(Warkat warkat) {
        jurnalPajakRepository.updateStatusData(warkat.getNoWarkat(), warkat.getStatusData());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        jurnalPajakRepository.deleteWarkatById(id);
    }

    public DataTablesResponse<JurnalPajakDTO> datatables(DataTablesRequest<JurnalPajakDTO> params, String search) {
        List<JurnalPajakDTO> data = jurnalPajakDao.datatables(params, search);
        Long rowCount = jurnalPajakDao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<TransaksiJurnal> transaksiJurnalDataTables(DataTablesRequest<TransaksiJurnal> params, String search) {
        List<TransaksiJurnal> data = jurnalPajakDao.transaksiJurnalDatatables(params, search);
        Long rowCount = jurnalPajakDao.transaksiJurnalDatatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesOutput<Warkat> datatablesHibernate(DataTablesInput input) {
        DataTablesOutput<Warkat> data = jurnalPajakRepository.findAll(input);
        data.setData(jurnalPajakRepository.datatables());
        return data;
    }

    public Optional<Warkat> findById(String id) {
        return jurnalPajakDao.findById(id);
    }
}

package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import id.co.dapenbi.accounting.repository.transaksi.WarkatLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class WarkatLogService {

    @Autowired
    private WarkatLogRepository repository;

    public Iterable<WarkatLog> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public WarkatLog save(WarkatLog warkat) {
        return repository.save(warkat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    public DataTablesOutput<WarkatLog> findForDataTable(DataTablesInput input) {
        return this.repository.findAll(input);
    }

    public Iterable<WarkatLog> findAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteByNoWarkat(String noWarkat) {
        repository.deleteByNoWarkat(noWarkat);
    }
}

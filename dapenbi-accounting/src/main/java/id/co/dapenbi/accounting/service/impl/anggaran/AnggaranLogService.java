package id.co.dapenbi.accounting.service.impl.anggaran;

import id.co.dapenbi.accounting.entity.anggaran.AnggaranLog;
import id.co.dapenbi.accounting.repository.anggaran.AnggaranLogRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnggaranLogService {

    @Autowired
    private AnggaranLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AnggaranLog save(AnggaranLog anggaranLog) {
        anggaranLog = repository.save(anggaranLog);
        return anggaranLog;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteByNoAnggaran(String noAnggaran) {
        Iterable<AnggaranLog> anggaranLogs = repository.findByNoAnggaran(noAnggaran);
        if (IterableUtils.toList(anggaranLogs).size() > 0)
            repository.deleteByNoAnggaran(noAnggaran);
    }
}
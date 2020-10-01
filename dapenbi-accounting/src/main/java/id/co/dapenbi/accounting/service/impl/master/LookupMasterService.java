package id.co.dapenbi.accounting.service.impl.master;

import id.co.dapenbi.accounting.entity.master.LookupMaster;
import id.co.dapenbi.accounting.repository.master.LookupMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LookupMasterService {

    @Autowired
    private LookupMasterRepository lookupMasterRepository;

    public List<LookupMaster> findByJenisLookup(String jenisLookup) {
        return lookupMasterRepository.findByJenisLookup(jenisLookup);
    }
}

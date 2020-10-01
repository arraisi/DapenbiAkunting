package id.co.dapenbi.accounting.service.impl.parameter;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.repository.parameter.PeriodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PeriodeService {

    @Autowired
    private PeriodeRepository periodeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Periode save(Periode periode) {
        periode.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        periode = periodeRepository.save(periode);

        return periode;
    }

    public Optional<Periode> findById(String id) {
        return periodeRepository.findById(id);
    }

    public Iterable<Periode> getAll() {
        return periodeRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Periode update(Periode periode) {
        periode.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        periodeRepository.update(
                periode.getKodePeriode(),
                periode.getNamaPeriode(),
                periode.getTriwulan(),
                periode.getQuarter(),
                periode.getStatusAktif(),
                periode.getUpdatedBy(),
                periode.getUpdatedDate()
        );

        return periode;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        periodeRepository.deleteById(id);
    }

    public DataTablesOutput<Periode> findForDataTable(DataTablesInput input) {
        return periodeRepository.findAll(input);
    }

    public Optional<Periode> findByNamaPeriode(String namePeriode) {
        return periodeRepository.findByNamaPeriode(namePeriode);
    }

    public Iterable<Periode> findAll() {
        return periodeRepository.findAll();
    }
}

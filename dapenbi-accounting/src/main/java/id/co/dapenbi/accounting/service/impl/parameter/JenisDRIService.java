package id.co.dapenbi.accounting.service.impl.parameter;

import id.co.dapenbi.accounting.entity.parameter.JenisDRI;
import id.co.dapenbi.accounting.repository.parameter.JenisDRIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JenisDRIService {

    @Autowired
    private JenisDRIRepository jenisDRIRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public JenisDRI save(JenisDRI jenisDRI) {
        jenisDRI.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        jenisDRI = jenisDRIRepository.save(jenisDRI);

        return jenisDRI;
    }

    public Optional<JenisDRI> findById(String id) {
        return jenisDRIRepository.findById(id);
    }

    public Iterable<JenisDRI> getAll() {
        return jenisDRIRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public JenisDRI update(JenisDRI jenisDRI) {
        jenisDRI.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        jenisDRIRepository.update(
                jenisDRI.getKodeDRI(),
                jenisDRI.getNamaDRI(),
                jenisDRI.getStatusAktif(),
                jenisDRI.getUpdatedBy(),
                jenisDRI.getUpdatedDate()
        );

        return jenisDRI;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        jenisDRIRepository.deleteById(id);
    }

    public DataTablesOutput<JenisDRI> findForDataTable(DataTablesInput input) {
        return jenisDRIRepository.findAll(input);
    }
}

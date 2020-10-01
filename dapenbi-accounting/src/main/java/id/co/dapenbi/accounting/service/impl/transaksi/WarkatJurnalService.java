package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.dao.transaksi.WarkatJurnalDao;
import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import id.co.dapenbi.accounting.mapper.WarkatJurnalMapper;
import id.co.dapenbi.accounting.repository.transaksi.WarkatJurnalDtoDataTableRepository;
import id.co.dapenbi.accounting.repository.transaksi.WarkatJurnalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class WarkatJurnalService {

    @Autowired
    private WarkatJurnalRepository repository;

    @Autowired
    private WarkatJurnalDao dao;

    @Autowired
    private WarkatJurnalDtoDataTableRepository dataTableRepository;

    public Iterable<WarkatJurnal> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public WarkatJurnal save(WarkatJurnal warkatJurnal) {
        return repository.save(warkatJurnal);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public WarkatJurnal querySave(WarkatJurnal warkatJurnal) {
        return dao.querySave(warkatJurnal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Iterable<WarkatJurnal> saveAll(List<WarkatJurnal> warkatJurnal) {
        return repository.saveAll(warkatJurnal);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Iterable<WarkatJurnal> saveAllDTO(List<WarkatJurnalDTO> warkatJurnalDTOs) {
        List<WarkatJurnal> warkatJurnals = new ArrayList<>();
        for (WarkatJurnalDTO dto : warkatJurnalDTOs) {
            WarkatJurnal _warkatJurnal = WarkatJurnalMapper.INSTANCE.dtoToEntity(dto);
            WarkatJurnal warkatJurnal = save(_warkatJurnal);
            warkatJurnals.add(warkatJurnal);
        }
        return warkatJurnals;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByNoWarkat(String warkatJurnal) {
        return dao.deleteByNoWarkat(warkatJurnal);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    public Optional<WarkatJurnal> findById(Integer id) {
        return this.repository.findById(id);
    }

    public DataTablesOutput<WarkatJurnal> findForDataTable(DataTablesInput input) {
        return this.repository.findAll(input);
    }

    public Iterable<WarkatJurnal> findAll() {
        return repository.findAll();
    }

    public Iterable<WarkatJurnalDTO.datatables> getAllByNoWarkat(String noWarkat) {
        return dataTableRepository.findByNoWarkat(noWarkat);
    }

    public List<WarkatJurnal> findByNoWarkat(String noWarkat) {
        return dao.findByNoWarkat(noWarkat);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByIdWarkat(String id) {
        repository.deleteByIdWarkat(id);
    }

    public List<WarkatJurnal> findByNoWarkatDRI2(String noWarkat) {
        return dao.findByNoWarkatDRI2(noWarkat);
    }

    public List<WarkatJurnalDTO> findByNoWarkatAndSaldoNormal(String noWarkat, String saldoNormal) {
        return dao.findByNoWarkatAndSaldoNormal(noWarkat, saldoNormal);
    }
}

package id.co.dapenbi.accounting.service.impl.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.parameter.TransaksiDao;
import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.mapper.TransaksiJurnalMapper;
import id.co.dapenbi.accounting.mapper.TransaksiMapper;
import id.co.dapenbi.accounting.repository.parameter.TransaksiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TransaksiService {

    @Autowired
    private TransaksiRepository repository;

    @Autowired
    private TransaksiDao dao;

    @Autowired
    private TransaksiJurnalService transaksiJurnalService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Transaksi save(Transaksi request) {
        return repository.save(request);
    }

    public Optional<Transaksi> findById(String id) {
        return Optional.ofNullable(dao.findByKodeTransaksi(id));
    }

    public Iterable<Transaksi> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Transaksi update(Transaksi request) {
        return repository.save(request);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Transaksi transaksi) {
        transaksiJurnalService.deleteByKodeTransaksi(transaksi.getKodeTransaksi());
        repository.deleteById(transaksi.getKodeTransaksi());
    }

    public DataTablesOutput<Transaksi> findForDataTable(DataTablesInput input) {
        return repository.findAll(input);
    }

    public DataTablesResponse<TransaksiDTO> datatables(DataTablesRequest<TransaksiDTO> params, String search) {
        List<TransaksiDTO> data = dao.datatables(params, search);
        Long rowCount = dao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<Transaksi> datatablesForJurnalBiaya(DataTablesRequest<TransaksiDTO> params, String search) {
        List<Transaksi> data = dao.datatablesForJurnalBiaya(params, search);
        Long rowCount = dao.datatablesForJurnalBiaya(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<Transaksi> findAll() {
        return dao.findAll();
    }

    public List<TransaksiDTO> findAllWithJurnals() {
        return dao.findAllWithJurnals();
    }
}

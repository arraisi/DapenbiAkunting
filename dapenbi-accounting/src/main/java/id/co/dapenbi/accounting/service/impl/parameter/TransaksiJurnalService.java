package id.co.dapenbi.accounting.service.impl.parameter;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.parameter.TransaksiJurnalDao;
import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import id.co.dapenbi.accounting.mapper.TransaksiJurnalMapper;
import id.co.dapenbi.accounting.repository.parameter.TransaksiJurnalDTRepository;
import id.co.dapenbi.accounting.repository.parameter.TransaksiJurnalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransaksiJurnalService {

    @Autowired
    private TransaksiJurnalRepository repository;

    @Autowired
    private TransaksiJurnalDTRepository dtRepository;

    @Autowired
    private TransaksiJurnalDao dao;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(TransaksiJurnalDTO request) {
        return dao.save(request);
    }

    public Optional<TransaksiJurnal> findById(Integer id) {
        return dao.findById(id);
    }

    public Iterable<TransaksiJurnal> findAll() {
        return dao.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public TransaksiJurnal update(TransaksiJurnal value) {
        return repository.save(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public List<TransaksiJurnalDTO> findByKodeTransaksi(String kodeTransaksi) {
        return dao.findByKodeTransaksi(kodeTransaksi);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKodeTransaksi(String kodeTransaksi) {
        return dao.deleteByKodeTransaksi(kodeTransaksi);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(TransaksiDTO transaksi) {
        transaksi.getTransaksiJurnals().forEach(transaksiJurnalDTO -> {
            transaksiJurnalDTO.setIdTransaksiJurnal(null);
            transaksiJurnalDTO.setKodeTransaksi(transaksi);
            transaksiJurnalDTO.setCreatedBy(transaksi.getCreatedBy());
            transaksiJurnalDTO.setCreatedDate(transaksi.getCreatedDate());
            transaksiJurnalDTO.setUpdatedBy(transaksi.getUpdatedBy());
            transaksiJurnalDTO.setUpdatedDate(transaksi.getUpdatedDate());
            save(transaksiJurnalDTO);
        });
    }

    public DataTablesOutput<TransaksiJurnalDTO.DataTables> findForDataTable(DataTablesInput input) {
        return dtRepository.findAll(input);
    }

    public DataTablesResponse<TransaksiJurnal> datatables(DataTablesRequest<TransaksiJurnal> params, String search) {
        List<TransaksiJurnal> data = dao.datatables(params, search);
        Long rowCount = dao.datatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public List<TransaksiJurnalDTO> findDataJurnal(String tipeRekening) {
        return dao.findDataJurnal(tipeRekening);
    }

    public BigDecimal findPajakPph(String kodePeriode, String tahunBuku) {
        final List<BigDecimal> pajakPph = dao.findPajakPph(kodePeriode, tahunBuku);
        if (pajakPph.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return pajakPph.get(0);
        }
    }

    public BigDecimal findHutangPph(String kodePeriode, String tahunBuku) {
        final List<BigDecimal> hutangPph = dao.findHutangPph(kodePeriode, tahunBuku);
        if (hutangPph.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return hutangPph.get(0);
        }
    }
}

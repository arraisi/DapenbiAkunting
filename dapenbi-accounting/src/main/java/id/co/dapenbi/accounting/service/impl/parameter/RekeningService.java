package id.co.dapenbi.accounting.service.impl.parameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.parameter.RekeningDao;
import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.SaldoDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.mapper.RekeningMapper;
import id.co.dapenbi.accounting.repository.parameter.RekeningRepository;
import id.co.dapenbi.accounting.service.impl.transaksi.SaldoCurrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RekeningService {

    @Autowired
    private RekeningRepository repository;

    @Autowired
    private RekeningDao dao;

    @Autowired
    private SaldoCurrentService saldoCurrentService;

    public Iterable<Rekening> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Rekening save(Rekening rekening) {
        updateStatusAktifByIdParent(rekening.getIdRekening(), rekening.getStatusAktif());
        return repository.save(rekening);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Integer updateParentByParent(String key, String parent) {
        return dao.updateParentByParent(key, parent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int deleteByParent(Integer idParent) {
        return this.repository.deleteByIdParent(idParent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int updateStatusAktifByIdParent(Integer idParent, String status) {
        Long childLenght = countByIdParent(idParent);
        if (childLenght == 0) return 0;
        this.repository.updateStatusAktifByIdParent(idParent, status);

        Iterable<Rekening> rekenings = findByParent(idParent);
        for (Rekening _rekening : rekenings) {
            updateStatusAktifByIdParent(_rekening.getIdRekening(), status);
        }
        return 1;
    }

    public Optional<Rekening> findById(Integer id) {
        return this.repository.findById(id);
    }

    public Optional<Rekening> findByIdParent(Integer idParent) {
        return this.repository.findByIdParent(idParent);
    }

    public DataTablesOutput<Rekening> findForDataTable(DataTablesInput input) {
        return this.repository.findAll(input);
    }

    public Iterable<Rekening> findAll() {
        return this.dao.findAll();
    }

    public Iterable<Rekening> findAllRekenings() {
        return this.dao.findAllRekening();
    }

    public Iterable<Rekening> getAllParent() {
        return this.repository.findAllParent();
    }

    public Iterable<Rekening> findByParent(Integer parent) {
        return this.repository.findByParent(parent);
    }

    public Long countByIdRekening(Integer idRekening) {
        return this.repository.countByIdRekening(idRekening);
    }

    public Long countByIdParent(Integer idParent) {
        return this.repository.countByIdParent(idParent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Integer deleteByKodeRekening(String kodeRekening) {
        return this.dao.deleteByKodeRekening(kodeRekening);
    }

    public List<String> getAllIdRekening() {
        return this.dao.getAllIdRekening();
    }

    @Transactional
    public int update(Rekening request, Principal principal) {
        try {

            request.setUpdatedBy(principal.getName());
            request.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
            // update parent
            Optional<Rekening> currentRekening = findById(request.getIdRekening());
            // status active children update
            if (!request.getStatusAktif().equals(currentRekening.get().getStatusAktif()))
                updateStatusAktifByIdParent(request.getIdRekening(), request.getStatusAktif());
            // status active children update
            updateParentByParent(request.getKodeRekening(), currentRekening.get().getKodeRekening());
            save(request);
        } catch (Exception e) {
            log.info("{}", e);
        }
        return 1;
    }

    public DataTablesResponse<RekeningDTO> datatables(DataTablesRequest<RekeningDTO> params, String search) {
        List<RekeningDTO> data = dao.findForDataTableRekening(params, search);
        Long rowCount = dao.findForDataTableRekening(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public Iterable<RekeningDTO> findAllDTO() {
        return this.dao.findAllDTO();
    }

    public List<Rekening> findAllByOrder() {
        return repository.findAllByOrder();
    }

    public RekeningDTO findByIdDTO(Integer id) {
        Optional<Rekening> rekening = repository.findById(id);
        Optional<SaldoCurrent> saldoCurrent = saldoCurrentService.findByIdRekening(id);
        RekeningDTO rekeningDTO = RekeningMapper.INSTANCE.rekeningToRekeningDTO(rekening.get());
        rekeningDTO.setSaldoCurrent(saldoCurrent.isPresent() ? saldoCurrent.get() : new SaldoCurrent());
        return rekeningDTO;
    }

    public List<Rekening> findByTipeRekening(String tipeRekening) {
        return repository.findByTipeRekening(tipeRekening);
    }

    @Transactional
    public int updateEdited(Integer idRekening, Integer status) {
        return repository.updateEdited(idRekening, status);
    }

    public List<Rekening> listByLevel(Integer level) {
        return repository.listByLevel(level);
    }

    public List<Rekening> findByListTipeRekening(String tipeRekenings) {
        try {
            return dao.findByListTipeRekening(tipeRekenings);
        } catch (IncorrectResultSizeDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public SaldoDTO findSaldoAwalAkhir(String kodeRekening, String startDate, String endDate) {
        try {
            return dao.findSaldoAwalAkhir(kodeRekening, startDate, endDate);
        } catch (EmptyResultDataAccessException e) {
            SaldoDTO saldo = new SaldoDTO();
            saldo.setSaldoAkhir(BigDecimal.ZERO);
            saldo.setSaldoAwal(BigDecimal.ZERO);
            return saldo;
        }
    }
}

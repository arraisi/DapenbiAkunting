package id.co.dapenbi.accounting.service.impl.laporan;

import id.co.dapenbi.accounting.entity.laporan.InvestasiDetail;
import id.co.dapenbi.accounting.entity.laporan.InvestasiHeader;
import id.co.dapenbi.accounting.repository.laporan.InvestasiDetailRepository;
import id.co.dapenbi.accounting.repository.laporan.InvestasiHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MasterSPIService {

    @Autowired
    private InvestasiHeaderRepository headerRepository;

    @Autowired
    private InvestasiDetailRepository detailRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiHeader save(InvestasiHeader investasiHeader) {
        return headerRepository.save(investasiHeader);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiHeader update(InvestasiHeader investasiHeader) {
        Optional<InvestasiHeader> optional = headerRepository.findById(investasiHeader.getIdInvestasi());
        if (optional.isPresent()) {
            investasiHeader.setStatusData(optional.get().getStatusData());
            investasiHeader.setCreatedBy(optional.get().getCreatedBy());
            investasiHeader.setCreatedDate(optional.get().getCreatedDate());
        }
        return headerRepository.save(investasiHeader);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiHeader updateStatusInv(String status, String idInvestasi, String username) {
        Optional<InvestasiHeader> optional = headerRepository.findById(idInvestasi);
        if (optional.isPresent()) {
            InvestasiHeader investasiHeader = optional.get();
            investasiHeader.setStatusData(status);
            investasiHeader.setUpdatedBy(username);
            investasiHeader.setUpdatedDate(new Date());
            return headerRepository.save(investasiHeader);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteMasterInv(String idInvestasi) {
        detailRepository.deleteByIdInvestasi(idInvestasi);
        headerRepository.deleteById(idInvestasi);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiDetail save(InvestasiDetail investasiDetail) {
        return detailRepository.save(investasiDetail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiDetail update(InvestasiDetail investasiDetail) {
        Optional<InvestasiDetail> optional = detailRepository.findById(investasiDetail.getIdInvestasiDtl());
        if (optional.isPresent()) {
            investasiDetail.setStatusData(optional.get().getStatusData());
            investasiDetail.setCreatedBy(optional.get().getCreatedBy());
            investasiDetail.setCreatedDate(optional.get().getCreatedDate());
        }
        return detailRepository.save(investasiDetail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public InvestasiDetail updateStatusSpi(String status, Integer idInvDtl, String username) {
        Optional<InvestasiDetail> optional = detailRepository.findById(idInvDtl);
        if (optional.isPresent()) {
            InvestasiDetail investasiDetail = optional.get();
            investasiDetail.setStatusData(status);
            investasiDetail.setUpdatedBy(username);
            investasiDetail.setUpdatedDate(new Date());
            return detailRepository.save(investasiDetail);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteSpi(Integer idInvestasiDtl) {
        detailRepository.deleteById(idInvestasiDtl);
    }

    public List<InvestasiDetail> findAllInvestasiDetails(String idInvestasi) {
        Specification<InvestasiDetail> specification = this.byIdInvestasi(idInvestasi);
        return detailRepository.findAll(specification);
    }

    public DataTablesOutput<InvestasiHeader> getInvestasiHeaderDataTables(DataTablesInput input) {
        return headerRepository.findAll(input);
    }

    public DataTablesOutput<InvestasiDetail> getInvestasiDetailDataTables(DataTablesInput input, String idInvestasi) {
        Specification<InvestasiDetail> specification = this.byIdInvestasi(idInvestasi);
        return detailRepository.findAll(input, specification);
    }

    private Specification<InvestasiDetail> byIdInvestasi(String idInvestasi) {
        return (model, cq, cb) -> cb.equal(model.get("idInvestasi"), idInvestasi);
    }
}

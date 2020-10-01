package id.co.dapenbi.accounting.service.impl.laporan;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;
import id.co.dapenbi.accounting.dao.laporan.TransaksiSPIDao;
import id.co.dapenbi.accounting.dto.laporan.SPIDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIHeaderDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.laporan.InvestasiDetail;
import id.co.dapenbi.accounting.entity.laporan.InvestasiHeader;
import id.co.dapenbi.accounting.entity.laporan.SPIDetail;
import id.co.dapenbi.accounting.entity.laporan.SPIHeader;
import id.co.dapenbi.accounting.mapper.TransaksiSPIMapper;
import id.co.dapenbi.accounting.repository.laporan.InvestasiDetailRepository;
import id.co.dapenbi.accounting.repository.laporan.InvestasiHeaderRepository;
import id.co.dapenbi.accounting.repository.laporan.SPIDetailRepository;
import id.co.dapenbi.accounting.repository.laporan.SPIHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransaksiSPIService {

    @Autowired
    private SPIHeaderRepository headerRepository;

    @Autowired
    private SPIDetailRepository detailRepository;

    @Autowired
    private TransaksiSPIDao dao;

    public DataTablesResponse<SPIHeaderDTO> datatables(DataTablesRequest<SPIHeaderDTO> params, String search) {
        List<SPIHeaderDTO> data = dao.datatable(params, search);
        Long rowCount = dao.datatable(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<SPIDetailDTO> SPIDetaildatatables(DataTablesRequest<SPIDetailDTO> params, String search) {
        List<SPIDetailDTO> data = dao.SPIDetaildatatables(params, search);
        Long rowCount = dao.SPIDetaildatatables(params.getValue(), search);
        return new DataTablesResponse<>(data, params.getDraw(), rowCount, rowCount);
    }

    public void delete(Integer idSPIHdr) {
        dao.deleteHdr(idSPIHdr);
        dao.deleteDetailByIdHdr(idSPIHdr);
    }

    public List<SPIDetailDTO> findDetailByIdSPIHdr(Integer id) {
        return dao.findDetailByIdSPIHdr(id);
    }

    public SPIHeader save(SPIHeaderDTO request, Principal principal) {
        if (request.getCreatedBy() == null) {
            request.setCreatedBy(principal.getName());
            request.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        } else {
            request.setUpdatedBy(principal.getName());
            request.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        final SPIHeader spiHeader = TransaksiSPIMapper.INSTANCE.dtoToSPIHeader(request);
        final SPIHeader save = headerRepository.save(spiHeader);

        if (request.getSpiDetails().size() > 0) {
            request.getSpiDetails().forEach(spiDetailDTO -> {
                spiDetailDTO.setIdSPIHdr(spiHeader.getIdSPIHdr());
                if (spiDetailDTO.getIdSPIDtl() == null) {
                    final SPIDetail spiDetail = TransaksiSPIMapper.INSTANCE.dtoToSPIDetail(spiDetailDTO);
                    detailRepository.save(spiDetail);
                } else {
                    dao.mergeSPIDetail(spiDetailDTO, principal);
                }
            });
        }
        return save;
    }

    public void deleteDetil(Integer idSPIDtl) {
        detailRepository.deleteById(idSPIDtl);
    }
}

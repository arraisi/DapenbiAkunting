package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.laporan.SPIDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.SPIHeaderDTO;
import id.co.dapenbi.accounting.entity.laporan.SPIHeader;
import id.co.dapenbi.accounting.entity.laporan.SPIDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface TransaksiSPIMapper {
    TransaksiSPIMapper INSTANCE = Mappers.getMapper(TransaksiSPIMapper.class);

    SPIHeader dtoToSPIHeader(SPIHeaderDTO dto);

    SPIDetail dtoToSPIDetail(SPIDetailDTO dto);
}

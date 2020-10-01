package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.JurnalPajakDTO;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JurnalPajakMapper {
    JurnalPajakMapper INSTANCE = Mappers.getMapper(JurnalPajakMapper.class);

    Warkat warkatDTOToWarkat(JurnalPajakDTO warkatDTO);
}

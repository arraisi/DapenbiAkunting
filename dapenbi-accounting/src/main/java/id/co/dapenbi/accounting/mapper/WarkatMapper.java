package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface WarkatMapper {
    WarkatMapper INSTANCE = Mappers.getMapper(WarkatMapper.class);

    @Mappings(value = {
            @Mapping(target = "warkatJurnals", ignore = true)})
    Warkat warkatDtoToWarkat(WarkatDTO dto);
}

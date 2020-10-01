package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import id.co.dapenbi.accounting.entity.transaksi.WarkatJurnal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WarkatJurnalMapper {
    WarkatJurnalMapper INSTANCE = Mappers.getMapper(WarkatJurnalMapper.class);

    @Mappings(value = {
            @Mapping(target = "noWarkat.noWarkat", source = "dto.noWarkat")})
    WarkatJurnal dtoToEntity(WarkatJurnalDTO dto);
}

package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.dto.parameter.TransaksiJurnalDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import id.co.dapenbi.accounting.entity.parameter.TransaksiJurnal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransaksiJurnalMapper {
    TransaksiJurnalMapper INSTANCE = Mappers.getMapper(TransaksiJurnalMapper.class);

    TransaksiJurnal dtoToEntity(TransaksiJurnalDTO dto);
}

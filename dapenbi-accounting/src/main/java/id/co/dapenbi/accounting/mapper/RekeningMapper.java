package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.parameter.RekeningDTO;
import id.co.dapenbi.accounting.dto.transaksi.SerapDTO;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RekeningMapper {
    RekeningMapper INSTANCE = Mappers.getMapper(RekeningMapper.class);

    RekeningDTO rekeningToRekeningDTO(Rekening rekening);

    Rekening rekeningDTOToRekening(SerapDTO.RekeningDTO rekeningDTO);
}

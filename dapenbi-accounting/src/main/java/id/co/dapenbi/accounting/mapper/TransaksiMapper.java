package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.parameter.TransaksiDTO;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransaksiMapper {
    TransaksiMapper INSTANCE = Mappers.getMapper(TransaksiMapper.class);

    TransaksiDTO transaksiDtoToTransaksi(Transaksi dto);
    Transaksi dtoToTransaksi(TransaksiDTO dto);
}

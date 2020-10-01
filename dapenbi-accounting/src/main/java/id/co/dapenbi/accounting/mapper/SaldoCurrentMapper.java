package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.InformasiSaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaldoCurrentMapper {
    SaldoCurrentMapper INSTANCE = Mappers.getMapper(SaldoCurrentMapper.class);

    SaldoCurrent InformasiSaldoDTOToSaldoCurrent(InformasiSaldoDTO.SaldoCurrent dto);
}

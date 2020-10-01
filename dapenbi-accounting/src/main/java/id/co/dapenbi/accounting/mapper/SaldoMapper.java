package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.InformasiSaldoDTO;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaldoMapper {
    SaldoMapper INSTANCE = Mappers.getMapper(SaldoMapper.class);

    Saldo InformasiSaldoDTOToSaldo(InformasiSaldoDTO.Saldo dto);
}

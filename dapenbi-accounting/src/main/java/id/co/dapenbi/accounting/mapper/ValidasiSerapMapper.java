package id.co.dapenbi.accounting.mapper;

import commons.mappers.ObjectMapper;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ValidasiSerapMapper extends ObjectMapper<ValidasiSerap, Serap> {

    ValidasiSerapMapper instance = Mappers.getMapper(ValidasiSerapMapper.class);

    @Mappings({
            @Mapping(target = "noSerap", source = "serap.noSerap"),
            @Mapping(target = "keterangan", source = "serap.keteranganDebet"),
            @Mapping(target = "totalTransaksi", source = "serap.totalTransaksi"),
            @Mapping(target = "statusData", source = "serap.statusData")
    })
    ValidasiSerap serapToValidasiSerap(Serap serap);
}

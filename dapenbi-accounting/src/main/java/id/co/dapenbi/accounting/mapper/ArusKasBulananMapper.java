package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.entity.laporan.ArusKasBulanan;
import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.parameter.ArusKasRincian;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArusKasBulananMapper {
    ArusKasBulananMapper INSTANCE = Mappers.getMapper(ArusKasBulananMapper.class);

    @Mappings({
            @Mapping(target = "kodeArusKas", source = "arusKasRincian.kodeArusKas"),
            @Mapping(target = "idArusKas", source = "arusKasRincian.kodeRincian"),
            @Mapping(target = "idRekening", source = "arusKasRincian.idRekening"),
            @Mapping(target = "flagGroup", source = "arusKasRincian.flagGroup"),
            @Mapping(target = "saldoAwalTahun", source = "arusKasRincian.saldoAwalTahun")
    })
    ArusKasBulanan arusKasRincianToArusKasBulanan(ArusKasRincian arusKasRincian);
}

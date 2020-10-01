package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.laporan.LaporanRencanaBisnis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LaporanRencanaBisnisMapper {

    LaporanRencanaBisnisMapper INSTANCE = Mappers.getMapper(LaporanRencanaBisnisMapper.class);

    @Mappings({
            @Mapping(target = "idLaporanHdr", source = "laporanDetail.laporanHeader"),
            @Mapping(target = "idLaporanDtl", source = "laporanDetail.idLaporanDtl"),
            @Mapping(target = "kodeRumus", source = "laporanDetail.kodeRumus"),
            @Mapping(target = "levelAkun", source = "laporanDetail.levelAkun"),
            @Mapping(target = "cetakTebal", source = "laporanDetail.cetakTebal"),
            @Mapping(target = "cetakMiring", source = "laporanDetail.cetakMiring"),
            @Mapping(target = "cetakGaris", source = "laporanDetail.cetakGaris"),
            @Mapping(target = "uraian", source = "laporanDetail.judul")
    })
    LaporanRencanaBisnis laporanDetailToLaporanRencanaBisnis(LaporanDetail laporanDetail);
}

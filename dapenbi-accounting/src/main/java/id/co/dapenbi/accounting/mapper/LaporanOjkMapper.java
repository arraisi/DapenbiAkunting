package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDTO;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDetailDTO;
import id.co.dapenbi.accounting.dto.laporan.LaporanOjkDTO;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkuntingDetail;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPst;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface LaporanOjkMapper {
    LaporanOjkMapper INSTANCE = Mappers.getMapper(LaporanOjkMapper.class);

    @Mappings(value = {
            @Mapping(target = "urutan", source = "urutan"),
            @Mapping(target = "kodePeriode", source = "kodePeriode"),
            @Mapping(target = "kodeTahunBuku", source = "kodeTahunBuku")})
    OjkPst dtoToEntity(LaporanOjkDTO.Manual dto);
}

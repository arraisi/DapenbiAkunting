package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDTO;
import id.co.dapenbi.accounting.dto.anggaran.PenyusunanAnggaranAkuntingDetailDTO;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkunting;
import id.co.dapenbi.accounting.entity.anggaran.PenyusunanAnggaranAkuntingDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface PenyusunanAnggaranAkuntingMapper {
    PenyusunanAnggaranAkuntingMapper INSTANCE = Mappers.getMapper(PenyusunanAnggaranAkuntingMapper.class);

    @Mappings(value = {
            @Mapping(target = "kodeThnBuku.kodeTahunBuku", source = "dto.kodeThnBuku.kodeTahunBuku"),
            @Mapping(target = "kodePeriode.kodePeriode", source = "dto.kodePeriode.kodePeriode")})
    PenyusunanAnggaranAkunting dtoToEntity(PenyusunanAnggaranAkuntingDTO dto);

    @Mappings(value = {
            @Mapping(target = "noAnggaran", source = "dto.noAnggaran.noAnggaran"),
            @Mapping(target = "idRekening", source = "dto.idRekening.idRekening")})
    PenyusunanAnggaranAkuntingDetail dtoToEntityDetail(PenyusunanAnggaranAkuntingDetailDTO dto);
}

package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.entity.laporan.LaporanDetail;
import id.co.dapenbi.accounting.entity.laporan.LaporanKeuangan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LaporanKeuanganMapper {
    LaporanKeuanganMapper INSTANCE = Mappers.getMapper(LaporanKeuanganMapper.class);

    @Mappings({
            @Mapping(target = "idLaporanHdr", source = "laporanDetail.laporanHeader"),
            @Mapping(target = "idLaporanDtl", source = "laporanDetail.idLaporanDtl"),
            @Mapping(target = "kodeRumus", source = "laporanDetail.kodeRumus"),
            @Mapping(target = "idRekening", source = "laporanDetail.idRekening"),
            @Mapping(target = "kodeRekening", source = "laporanDetail.kodeRekening"),
            @Mapping(target = "namaRekening", source = "laporanDetail.namaRekening"),
            @Mapping(target = "levelAkun", source = "laporanDetail.levelAkun"),
            @Mapping(target = "cetakTebal", source = "laporanDetail.cetakTebal"),
            @Mapping(target = "cetakMiring", source = "laporanDetail.cetakMiring"),
            @Mapping(target = "cetakGaris", source = "laporanDetail.cetakGaris"),
            @Mapping(target = "uraian", source = "laporanDetail.judul"),
            @Mapping(target = "cetakJudul", source = "laporanDetail.cetakJudul"),
            @Mapping(target = "saldoSebelum", source = "laporanDetail.saldoSebelum")
    })
    LaporanKeuangan laporanDetailToLaporanKeuangan(LaporanDetail laporanDetail);
}

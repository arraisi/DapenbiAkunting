package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.SerapDTO;
import id.co.dapenbi.accounting.entity.transaksi.Serap;
import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SerapMapper {
    SerapMapper INSTANCE = Mappers.getMapper(SerapMapper.class);

    SerapDTO.Serap SerapToSerapDTO(Serap serap);
    Serap SerapDTOToSerap(SerapDTO.Serap serapDTO);

    List<SerapDTO.SerapExport> mapToSerapExport(List<SerapDetail> serapDetails);

    default SerapDTO.SerapExport mapToSerapExport(SerapDetail serapDetail) {
        SerapDTO.SerapExport serapExport = new SerapDTO.SerapExport();
        serapExport.setKodeRekening(serapDetail.getRekening().getKodeRekening());
        serapExport.setJumlahPenambah(serapDetail.getJumlahPenambah());
        serapExport.setJumlahPengurang(serapDetail.getJumlahPengurang());
        return serapExport;
    }
}

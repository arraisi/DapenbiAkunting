package id.co.dapenbi.accounting.mapper;

import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDTO;
import id.co.dapenbi.accounting.dto.transaksi.BudgetReviewDetailDTO;
import id.co.dapenbi.accounting.dto.transaksi.WarkatDTO;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import id.co.dapenbi.accounting.entity.transaksi.Warkat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel="spring")
public interface BudgetReviewMapper {
    BudgetReviewMapper INSTANCE = Mappers.getMapper(BudgetReviewMapper.class);

    @Mappings(value = {
            @Mapping(target = "kodeThnBuku", source = "dto.kodeThnBuku.kodeTahunBuku"),
            @Mapping(target = "kodePeriode", source = "dto.kodePeriode.kodePeriode")})
    BudgetReview dtoToBudgetReview(BudgetReviewDTO dto);

    @Mappings(value = {
            @Mapping(target = "noBudgetReview", source = "dto.noBudgetReview.noBudgetReview"),
            @Mapping(target = "idRekening", source = "dto.idRekening.idRekening")})
    BudgetReviewDetail dtoToBudgetReviewDetail(BudgetReviewDetailDTO dto);
}

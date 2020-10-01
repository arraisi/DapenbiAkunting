package id.co.dapenbi.accounting.dto.transaksi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

public class CheckAndBalanceDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {
        private Date tglTransaksi;
        private String thnBukuPeriode;
        private String userInput;
        private String satuanKerja;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rekening {
        private Long nomorRekening;
        private String namaRekening;
        private BigDecimal saldoWarkat;
        private BigDecimal saldoPreApproval;
        private BigDecimal saldoFinalApproval;
        private String status;
    }
}

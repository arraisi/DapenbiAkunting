package id.co.dapenbi.accounting.dto.transaksi;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PengaturanSistemDTO {
    private Long idParameter;
    private TahunBuku kodeTahunBuku;
    private Periode kodePeriode;
    private String dirut;
    private String div;
    private String kdiv;
    private String ks;
    private String jamTutup;
    private String noPengantarWarkat;
    private Timestamp tglTransaksi;
    private String statusAktif;
    private String statusOpen;
    private String createdBy;
    private Timestamp createdDate;
    private String updatedBy;
    private Timestamp updatedDate;
}

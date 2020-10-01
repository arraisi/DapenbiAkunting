package id.co.dapenbi.accounting.entity.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACC_PARAMETER")
public class PengaturanSistem {

    @Id
    @Column(name = "ID_PARAMETER")
    private Long idParameter;

    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;

    @Column(name = "DIRUT")
    private String dirut;

    @Column(name = "DIV")
    private String div;

    @Column(name = "KDIV")
    private String kdiv;

    @Column(name = "KS")
    private String ks;

    @Column(name = "JAM_TUTUP")
    private String jamTutup;

    @Column(name = "NO_PENGANTAR_WARKAT")
    private String noPengantarWarkat;

    @Column(name = "TGL_TRANSAKSI")
    private Timestamp tglTransaksi;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "STATUS_OPEN")
    private String statusOpen;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

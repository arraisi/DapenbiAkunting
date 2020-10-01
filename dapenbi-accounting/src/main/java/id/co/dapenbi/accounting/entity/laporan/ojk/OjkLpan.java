package id.co.dapenbi.accounting.entity.laporan.ojk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OJK_LPAN")
public class OjkLpan {

    @Id
    @SequenceGenerator(sequenceName = "OJK_LPAN_SEQ", allocationSize = 1, name = "ojkLpanGenerator")
    @GeneratedValue(generator = "ojkLpanGenerator")
    @Column(name = "ID_LAPORAN")
    private Integer idLaporan;

    @Column(name = "TGL_LAPORAN")
    private Date tglLaporan;

    @Column(name = "URAIAN")
    private String uraian;

    @Column(name = "ID_REKENING")
    private Long idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "LEVEL_AKUN")
    private Integer levelAkun;

    @Column(name = "PROGRAM_PENSIUN")
    private BigDecimal programPensiun;

    @Column(name = "MANFAAT_LAIN")
    private BigDecimal manfaatLain;

    @Column(name = "GABUNGAN")
    private BigDecimal gabungan;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "KODE_THNBUKU")
    private String kodeTahunBuku;

    @Column(name = "KODE_PERIODE")
    private String kodePeriode;
}

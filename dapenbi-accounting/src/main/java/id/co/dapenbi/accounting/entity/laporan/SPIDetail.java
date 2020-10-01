package id.co.dapenbi.accounting.entity.laporan;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_SPI_DTL")
public class SPIDetail {

    @Id
    @SequenceGenerator(sequenceName = "ACC_SPI_DTL_SEQ", allocationSize = 1, name = "spiDtlGenerator")
    @GeneratedValue(generator = "spiDtlGenerator")
    @Column(name = "ID_SPI_DTL")
    private Integer idSPIDtl;

    @Column(name = "ID_SPI_HDR")
    private Integer idSPIHdr;

    @OneToOne
    @JoinColumn(name = "KODE_THNBUKU", nullable = false)
    private TahunBuku kodeTahunBuku;

    @OneToOne
    @JoinColumn(name = "KODE_PERIODE", nullable = false)
    private Periode kodePeriode;

    @Column(name = "TGL_SPI")
    private Timestamp tglSPI;

    @Column(name = "ID_INVESTASI")
    private String idInvestasi;

    @Column(name = "ID_SPI")
    private String idSPI;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "NILAI_PEROLEHAN")
    private BigDecimal nilaiPerolehan;

    @Column(name = "NILAI_WAJAR")
    private BigDecimal nilaiWajar;

    @Column(name = "NILAI_SPI")
    private BigDecimal nilaiSPI;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

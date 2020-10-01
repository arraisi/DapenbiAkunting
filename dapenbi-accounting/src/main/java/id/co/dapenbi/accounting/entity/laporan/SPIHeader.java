package id.co.dapenbi.accounting.entity.laporan;

import id.co.dapenbi.accounting.entity.parameter.Periode;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.entity.parameter.Transaksi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_SPI_HDR")
public class SPIHeader {

    @Id
    @SequenceGenerator(sequenceName = "ACC_SPI_HDR_SEQ", allocationSize = 1, name = "spiHdrGenerator")
    @GeneratedValue(generator = "spiHdrGenerator")
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

    @Column(name = "TGL_VALIDASI")
    private Timestamp tglValidasi;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "USER_VALIDASI")
    private String userValidasi;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

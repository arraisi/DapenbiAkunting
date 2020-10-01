package id.co.dapenbi.accounting.entity.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_INVESTASI_DTL")
public class InvestasiDetail {

    @Id
    @SequenceGenerator(sequenceName = "ACC_INVESTASI_DTL_SEQ", allocationSize = 1, name = "investasiDtlGenerator")
    @GeneratedValue(generator = "investasiDtlGenerator")
    @Column(name = "ID_INVESTASI_DTL")
    private Integer idInvestasiDtl;

    @Column(name = "ID_INVESTASI")
    private String idInvestasi;

    @Column(name = "ID_SPI")
    private String idSpi;

    @Column(name = "KETERANGAN_SPI")
    private String keteranganSpi;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}

package id.co.dapenbi.accounting.entity.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACC_INVESTASI_HDR")
public class InvestasiHeader {

    @Id
    @Column(name = "ID_INVESTASI")
    private String idInvestasi;

    @Column(name = "NAMA_INVESTASI")
    private String namaInvestasi;

    @Column(name = "ID_LAPORAN_HDR")
    private Integer idLaporanHeader;

    @Column(name = "ID_LAPORAN_DTL")
    private Integer idLaporanDetail;

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

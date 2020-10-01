package id.co.dapenbi.accounting.entity.master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MST_LOOKUP")
public class LookupMaster {

    @Id
    @Column(name = "KODE_LOOKUP")
    private String kodeLookup;

    @Column(name = "JENIS_LOOKUP")
    private String jenisLookup;

    @Column(name = "NAMA_LOOKUP")
    private String namaLookup;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "STATUS_DATA")
    private String statusData;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;

    public LookupMaster(String kodeLookup) {
        this.setKodeLookup(kodeLookup);
    }
}

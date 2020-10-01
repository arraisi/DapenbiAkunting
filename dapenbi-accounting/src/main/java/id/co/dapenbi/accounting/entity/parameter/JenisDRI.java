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
@Table(name = "ACC_DRI")
public class JenisDRI {

    @Id
    @Column(name = "KODE_DRI")
    private String kodeDRI;

    @Column(name = "NAMA_DRI")
    private String namaDRI;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

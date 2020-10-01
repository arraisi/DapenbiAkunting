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
@Table(name = "ACC_ARUSKAS")
public class ArusKas {

    @Id
    @Column(name = "KODE_ARUSKAS")
    private String kodeArusKas;

    @Column(name = "KETERANGAN")
    private String keterangan;

    @Column(name = "STATUS_AKTIF")
    private String statusAktif;

    @Column(name = "ARUSKAS_AKTIVITAS")
    private String arusKasAktivitas;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private Timestamp createdDate;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_DATE")
    private Timestamp updatedDate;
}

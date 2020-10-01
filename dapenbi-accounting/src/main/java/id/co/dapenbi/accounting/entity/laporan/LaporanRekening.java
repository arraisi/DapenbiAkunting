package id.co.dapenbi.accounting.entity.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ACC_LAPORAN_REK")
public class LaporanRekening {
    @Id
    @SequenceGenerator(sequenceName = "ACC_LAPORAN_REK_SEQ", allocationSize = 1, name = "laporanRekGenerator")
    @GeneratedValue(generator = "laporanRekGenerator")
    @Column(name = "ID_LAPORAN_REK")
    private Integer idLaporanRek;

    @Column(name = "ID_LAPORAN_DTL")
    private Integer idLaporanDtl;

    @Column(name = "ID_REKENING")
    private Integer idRekening;

    @Column(name = "KODE_REKENING")
    private String kodeRekening;

    @Column(name = "NAMA_REKENING")
    private String namaRekening;

    @Column(name = "RUMUS_URUTAN")
    private Integer rumusUrutan;

    @Column(name = "RUMUS_OPERATOR")
    private String rumusOperator;

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
}

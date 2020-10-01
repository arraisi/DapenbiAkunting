package id.co.dapenbi.accounting.dto.laporan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableColumn {
    public TableColumn(String title, String data) {
        this.title = title;
        this.data = data;
    }

    private String title;
    private String data;
    private Boolean visible = true;
}

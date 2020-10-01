package commons.utils.oracle;

import commons.utils.PageableLimitOffset;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.validation.constraints.NotNull;

public class Oracle11LimitOffset implements PageableLimitOffset {

    private String query;
    private String rowNumber;
    private final MapSqlParameterSource parameterSource;

    public Oracle11LimitOffset(@NotNull MapSqlParameterSource parameterSource) {
        this.parameterSource = parameterSource;
    }

    @Override
    public String query(@NotNull String baseQuery, @NotNull String nameOfRowNumber, @NotNull Long length) {
        this.query = baseQuery;
        this.rowNumber = nameOfRowNumber;
        if(length == -1) {
            return String.format("select * from (%s)", query);
        } else {
            return String.format("select * from (%s) where %s between (:start + 1) and (:page * :limit)", query, rowNumber);
        }
    }

    @Override
    public MapSqlParameterSource parameter(Long start, Long limit) {
        if(start > 0) {
            parameterSource.addValue("page", (start + limit) / limit);
            parameterSource.addValue("start", start);
            parameterSource.addValue("limit", limit);
        } else if(limit == -1) {
//            do nothing
        } else {
            parameterSource.addValue("page", 1);
            parameterSource.addValue("start", start);
            parameterSource.addValue("limit", limit);
        }

        return parameterSource;
    }
}

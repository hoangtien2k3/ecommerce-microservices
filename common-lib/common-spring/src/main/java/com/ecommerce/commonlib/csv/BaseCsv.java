package com.ecommerce.commonlib.csv;

import com.ecommerce.commonlib.csv.annotation.CsvColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Optional convenience base for CSV view-models. Inheriting is not required — any class
 * whose fields are annotated with {@link CsvColumn} can be exported via
 * {@code CsvExporter}. Pick this base when every row should carry an {@code id} column.
 */
@SuperBuilder
@Getter
@Setter
public class BaseCsv {

    @CsvColumn(columnName = "Id")
    private Long id;
}

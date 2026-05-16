package com.ecommerce.commonlib.csv;

import com.ecommerce.commonlib.csv.annotation.CsvColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class BaseCsv {
    @CsvColumn(columnName = "Id")
    private Long id;
}

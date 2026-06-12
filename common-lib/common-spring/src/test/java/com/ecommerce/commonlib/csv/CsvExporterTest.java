package com.ecommerce.commonlib.csv;

import com.ecommerce.commonlib.csv.annotation.CsvColumn;
import com.ecommerce.commonlib.csv.annotation.CsvName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvExporterTest {

    @SuperBuilder
    @CsvName(fileName = "TestFile")
    @Getter
    @Setter
    static class TestData extends BaseCsv {

        @CsvColumn(columnName = "Name")
        private String name;

        @CsvColumn(columnName = "Tags")
        private List<String> tags;
    }

    @Test
    void exportProducesHeaderPlusRows() throws IOException {
        List<TestData> rows = List.of(
                TestData.builder().id(1L).name("Alice").tags(List.of("tag1", "tag2")).build(),
                TestData.builder().id(2L).name("Bob").tags(List.of("tag3", "tag4")).build()
        );

        String csv = new String(CsvExporter.exportToCsv(rows, TestData.class));

        assertThat(csv).isEqualTo("""
                Id,Name,Tags
                1,Alice,[tag1|tag2]
                2,Bob,[tag3|tag4]
                """);
    }

    @Test
    void exportEmptyListReturnsHeaderOnly() throws IOException {
        String csv = new String(CsvExporter.exportToCsv(List.<TestData>of(), TestData.class));
        assertThat(csv).isEqualTo("Id,Name,Tags\n");
    }

    @Test
    void filenameUsesCsvNameAnnotation() {
        String filename = CsvExporter.createFileName(TestData.class);
        assertThat(filename).startsWith("TestFile_").endsWith(".csv");
    }
}

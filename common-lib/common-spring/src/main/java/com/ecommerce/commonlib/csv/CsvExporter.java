package com.ecommerce.commonlib.csv;

import com.ecommerce.commonlib.csv.annotation.CsvColumn;
import com.ecommerce.commonlib.csv.annotation.CsvName;
import com.ecommerce.commonlib.util.DateTimeUtils;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Reflection-driven CSV exporter.
 *
 * <h3>Performance</h3>
 * The expensive part of reflection is looking up {@link Field}/{@link Method} objects — not
 * invoking them. We compile each class into an immutable {@code Schema} of accessible
 * getter {@link Method}s on first encounter and cache it forever. After the first export,
 * a row write is just {@code N} method invocations.
 *
 * <h3>Thread-safety</h3>
 * Schemas are immutable; the {@link ConcurrentHashMap} cache is safe under concurrent use.
 */
public final class CsvExporter {

    private static final Logger log = LoggerFactory.getLogger(CsvExporter.class);

    private CsvExporter() {
    }

    private static final ConcurrentMap<Class<?>, Schema> SCHEMAS = new ConcurrentHashMap<>();

    public static <T> byte[] exportToCsv(List<? extends T> rows, Class<T> type) throws IOException {
        Schema schema = SCHEMAS.computeIfAbsent(type, CsvExporter::compileSchema);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVWriter csv = new CSVWriter(writer,
                     ICSVWriter.DEFAULT_SEPARATOR,
                     ICSVWriter.NO_QUOTE_CHARACTER,
                     ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     ICSVWriter.DEFAULT_LINE_END)) {

            csv.writeNext(schema.headers);
            for (T row : rows) {
                csv.writeNext(schema.read(row));
            }
            csv.flush();
            return out.toByteArray();
        }
    }

    public static <T> String createFileName(Class<T> type) {
        CsvName ann = type.getAnnotation(CsvName.class);
        if (ann == null) {
            throw new IllegalArgumentException(type.getName() + " is missing @CsvName");
        }
        return String.format("%s_%s.csv", ann.fileName(), DateTimeUtils.format(LocalDateTime.now()));
    }

    // ------------------------------------------------------------------
    // Schema compilation — runs once per class
    // ------------------------------------------------------------------

    private static Schema compileSchema(Class<?> type) {
        List<Column> columns = new ArrayList<>();
        for (Field field : allDeclaredFields(type)) {
            CsvColumn ann = field.getAnnotation(CsvColumn.class);
            if (ann == null) {
                continue;
            }
            Method getter = findGetter(field);
            if (getter == null) {
                log.warn("Skipping CSV column {}#{} — no usable getter",
                        type.getSimpleName(), field.getName());
                continue;
            }
            getter.setAccessible(true);
            columns.add(new Column(ann.columnName(), getter));
        }
        return new Schema(columns);
    }

    private static Method findGetter(Field field) {
        String suffix = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
        for (String prefix : field.getType() == boolean.class ? new String[]{"is", "get"} : new String[]{"get"}) {
            try {
                return field.getDeclaringClass().getMethod(prefix + suffix);
            } catch (NoSuchMethodException ignored) {
                // try next prefix or fall through
            }
            try {
                return field.getDeclaringClass().getDeclaredMethod(prefix + suffix);
            } catch (NoSuchMethodException ignored) {
                // continue
            }
        }
        return null;
    }

    /** Walks the class hierarchy parent-first so inherited columns appear before declared ones. */
    private static List<Field> allDeclaredFields(Class<?> type) {
        ArrayDeque<Class<?>> hierarchy = new ArrayDeque<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            hierarchy.push(c);
        }
        List<Field> fields = new ArrayList<>();
        for (Class<?> c : hierarchy) {
            fields.addAll(List.of(c.getDeclaredFields()));
        }
        return fields;
    }

    // ------------------------------------------------------------------
    // Schema + Column — immutable, cache-friendly
    // ------------------------------------------------------------------

    private static final class Schema {
        final String[] headers;
        final List<Column> columns;

        Schema(List<Column> columns) {
            this.columns = List.copyOf(columns);
            this.headers = this.columns.stream().map(c -> c.header).toArray(String[]::new);
        }

        String[] read(Object row) {
            String[] cells = new String[columns.size()];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = columns.get(i).read(row);
            }
            return cells;
        }
    }

    private record Column(String header, Method getter) {
        String read(Object row) {
            try {
                Object value = getter.invoke(row);
                if (value == null) {
                    return "";
                }
                if (value instanceof Collection<?> col) {
                    StringBuilder sb = new StringBuilder("[");
                    boolean first = true;
                    for (Object item : col) {
                        if (!first) sb.append('|');
                        sb.append(item);
                        first = false;
                    }
                    return sb.append(']').toString();
                }
                return value.toString();
            } catch (ReflectiveOperationException t) {
                log.warn("CSV cell read failed for column {}: {}", header, t.getMessage());
                return "";
            }
        }
    }
}

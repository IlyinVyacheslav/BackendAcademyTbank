package backend.academy.statistics.writer;

import backend.academy.statistics.Statistics;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarkdownStatisticsWriter implements StatisticsWriter {

    public static final int MIN_COLUMN_SIZE = 10;

    @Override
    public String writeStatistics(Statistics stats) {
        StringBuilder result = new StringBuilder();

        result.append(
            baseInfoTable(
                stats.requestsNumber(),
                stats.averageServerResponseSize(),
                stats.p95ServerResponseSize(),
                stats.errorRate()
            )
        );

        result.append(
            frequencyMapTable(stats.resourceFrequencyMap(), Statistics.RESOURCE_FREQUENCY_MAP_DESCRIPTION, "Ресурс"));
        result.append(
            frequencyMapTable(stats.responseCodesFrequencyMap(), Statistics.RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION,
                "Код"));
        result.append(
            frequencyMapTable(stats.remoteAddressFrequencyMap(), Statistics.REMOTE_ADDRESS_FREQUENCY_MAP_DESCRIPTION,
                "Адрес"));

        return result.toString();
    }

    private String baseInfoTable(
        int requestsNumber,
        double averageServerResponseSize,
        double p95ServerResponseSize,
        double errorRate
    ) {
        StringBuilder basePart = new StringBuilder();
        basePart.append("#### Общая информация\n\n");

        int maxLength = Math.max(
            MIN_COLUMN_SIZE,
            maxEntryLength(
                List.of(
                    Statistics.REQUEST_NUMBER_DESCRIPTION,
                    Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION,
                    Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION,
                    Statistics.ERROR_RATE_DESCRIPTION
                ).toArray()
            )
        );

        basePart.append(tableInfoLine(maxLength, "Метрика", "Значение"));
        basePart.append(tableSeparatingLine(maxLength));
        basePart.append(tableInfoLine(maxLength, requestsNumber, Statistics.REQUEST_NUMBER_DESCRIPTION, ""));
        basePart.append(
            tableInfoLine(maxLength, averageServerResponseSize, Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION,
                "b"));
        basePart.append(
            tableInfoLine(maxLength, p95ServerResponseSize, Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION, "b"));
        basePart.append(
            tableInfoLine(maxLength, errorRate, Statistics.ERROR_RATE_DESCRIPTION, "%"));

        return basePart.toString();
    }

    private <T> String frequencyMapTable(Map<T, Integer> map, String description, String columnName) {
        if (map.isEmpty()) {
            return "";
        }

        StringBuilder freqTable = new StringBuilder();
        freqTable.append(String.format("%n#### %s%n%n", description));

        int maxLength = Math.max(
            columnName.length(),
            maxEntryLength(map.keySet().toArray())
        );

        freqTable.append(tableInfoLine(maxLength, columnName, "Количество"));
        freqTable.append(tableSeparatingLine(maxLength));
        map.entrySet().stream()
            .sorted(Map.Entry.<T, Integer>comparingByValue().reversed())
            .forEach(
                entry -> freqTable.append(
                    tableInfoLine(maxLength, entry.getKey().toString(), entry.getValue().toString())));

        return freqTable.toString();
    }

    private String tableSeparatingLine(int maxLength) {
        return String.format("|:%s:|-%s:|%n", "-".repeat(maxLength), "-".repeat(MIN_COLUMN_SIZE));
    }

    private <T> String tableInfoLine(int length, T entry, String description, String dimension) {
        return tableInfoLine(length, description, entry, dimension);
    }

    private <T> String tableInfoLine(int length, String d, T v) {
        return tableInfoLine(length, d, v, "");
    }

    private <T> String tableInfoLine(int length, String d, T v, String dimension) {
        String format = "| %-" + length + "s | %";
        int columnSize = MIN_COLUMN_SIZE - dimension.length();
        if (v instanceof Double) {
            return String.format(Locale.US, format + columnSize + ".2f%s |\n", d, v, dimension);
        }
        return String.format(format + columnSize + "s%s |\n", d, v, dimension);
    }

    private int maxEntryLength(Object[] entries) {
        return Arrays.stream(entries)
            .map(Object::toString)
            .mapToInt(String::length)
            .max()
            .orElse(0);
    }
}

package backend.academy.statistics.writer;

import backend.academy.statistics.Statistics;
import java.util.Map;

public class AdocStatisticsWriter implements StatisticsWriter {
    @Override
    public String writeStatistics(Statistics statistics) {
        StringBuilder result = new StringBuilder();
        result.append(writeLine(statistics.requestsNumber(), Statistics.REQUEST_NUMBER_DESCRIPTION, ""));
        result.append(
            writeLine(statistics.averageServerResponseSize(), Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION,
                "b"));
        result.append(
            writeLine(statistics.p95ServerResponseSize(), Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION, "b"));
        result.append(
            writeLine(statistics.errorRate(), Statistics.ERROR_RATE_DESCRIPTION, "%")
        );
        result.append(writeMap(statistics.resourceFrequencyMap(), Statistics.RESOURCE_FREQUENCY_MAP_DESCRIPTION));
        result.append(
            writeMap(statistics.responseCodesFrequencyMap(), Statistics.RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION));
        result.append(
            writeMap(statistics.remoteAddressFrequencyMap(), Statistics.REMOTE_ADDRESS_FREQUENCY_MAP_DESCRIPTION));
        return result.toString();
    }

    private String writeMap(Map<?, ?> map, String description) {
        return map.isEmpty() ? "" : writeLine(map, description, "");
    }

    private <T> String writeLine(T entry, String description, String dimension) {
        return String.format("%s: %s%s.%n", description, entry, dimension);
    }
}

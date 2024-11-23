package backend.academy.statistics.writer;

import backend.academy.statistics.Statistics;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AdocStatisticsWriterTest extends AbstractStatisticsWriterTest{
    public static final StatisticsWriter ADOC_STATISTICS_WRITER = new AdocStatisticsWriter();

    private <T> String formLine(String description, T value, String dimension) {
        return String.format("%s: %s%s.%n", description, value, dimension);
    }

    @Test
    void testWritingEmptyStatistics() {
        String expectedReport = formLine(Statistics.REQUEST_NUMBER_DESCRIPTION, 0, "") +
                                formLine(Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION, 0.0, "b") +
                                formLine(Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION, 0.0, "b") +
                                formLine(Statistics.ERROR_RATE_DESCRIPTION, 0.0,"%");

        String actualReport = ADOC_STATISTICS_WRITER.writeStatistics(EMPTY_STATISTICS);

        assertThat(actualReport).isEqualTo(expectedReport);
    }

    @Test
    void testWritingFullStatistics() {
        String expectedReport = formLine(Statistics.REQUEST_NUMBER_DESCRIPTION, FULL_STATISTICS.requestsNumber(), "") +
                                formLine(Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION, FULL_STATISTICS.averageServerResponseSize(), "b") +
                                formLine(Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION, FULL_STATISTICS.p95ServerResponseSize(), "b") +
                                formLine(Statistics.ERROR_RATE_DESCRIPTION, FULL_STATISTICS.errorRate(), "%") +
                                formLine(Statistics.RESOURCE_FREQUENCY_MAP_DESCRIPTION, FULL_STATISTICS.resourceFrequencyMap(), "") +
                                formLine(Statistics.RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION, FULL_STATISTICS.responseCodesFrequencyMap(), "") +
                                formLine(Statistics.REMOTE_ADDRESS_FREQUENCY_MAP_DESCRIPTION, FULL_STATISTICS.remoteAddressFrequencyMap(), "");

        String actualReport = ADOC_STATISTICS_WRITER.writeStatistics(FULL_STATISTICS);

        assertThat(actualReport).isEqualTo(expectedReport);
    }
}

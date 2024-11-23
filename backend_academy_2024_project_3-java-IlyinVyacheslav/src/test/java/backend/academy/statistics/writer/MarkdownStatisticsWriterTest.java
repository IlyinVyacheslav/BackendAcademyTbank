package backend.academy.statistics.writer;

import backend.academy.statistics.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MarkdownStatisticsWriterTest extends AbstractStatisticsWriterTest {
    public static final StatisticsWriter MARKDOWN_STATISTICS_WRITER = new MarkdownStatisticsWriter();
    public static final String MAIN_HEADER = "Общая информация";
    private static int maxDescriptionLength;

    @BeforeAll static void setUp() {
        maxDescriptionLength = Math.max(
            Math.max(
                Statistics.REQUEST_NUMBER_DESCRIPTION.length(),
                Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION.length()
            ),
            Math.max(
                Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION.length(),
                Statistics.ERROR_RATE_DESCRIPTION.length()
            )
        );
    }

    private static String buildHeader(String header) {
        return "#### " + header;
    }

    private String buildDescription(String s) {
        return String.format("| %s |", s + " ".repeat(maxDescriptionLength - s.length()));
    }

    @Test
    void testWritingEmptyStatistics() {
        String mdReport = MARKDOWN_STATISTICS_WRITER.writeStatistics(EMPTY_STATISTICS);

        assertThat(mdReport)
            .startsWith(buildHeader(MAIN_HEADER))
            .contains(buildDescription(Statistics.REQUEST_NUMBER_DESCRIPTION) + "          0 |")
            .contains(buildDescription(Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION) + "      0.00b |")
            .contains(buildDescription(Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION) + "      0.00b |")
            .contains(buildDescription(Statistics.ERROR_RATE_DESCRIPTION) + "      0.00% |")
            .doesNotContain(Statistics.RESOURCE_FREQUENCY_MAP_DESCRIPTION)
            .doesNotContain(Statistics.RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION);
    }

    @Test
    void testWritingFullStatistics() {
        String mdReport = MARKDOWN_STATISTICS_WRITER.writeStatistics(FULL_STATISTICS);

        assertThat(mdReport)
            .startsWith(buildHeader(MAIN_HEADER))
            .contains(buildDescription(Statistics.REQUEST_NUMBER_DESCRIPTION) + "         10 |")
            .contains(buildDescription(Statistics.AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION) + "     52.00b |")
            .contains(buildDescription(Statistics.P_95_SERVER_RESPONSE_SIZE_DESCRIPTION) + "     90.00b |")
            .contains(buildDescription(Statistics.ERROR_RATE_DESCRIPTION) + "     80.00% |")
            .contains(buildHeader(Statistics.RESOURCE_FREQUENCY_MAP_DESCRIPTION))
            .contains(buildHeader(Statistics.RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION))
            .contains("| Ресурс | Количество |")
            .contains("| /a     |         12 |")
            .contains("| Код | Количество |")
            .contains("| 504 |         10 |")
            .contains("| 200 |          2 |")
            .contains("| Ресурс | Количество |")
            .contains("| Адрес           | Количество |")
            .contains("| 135.90.137.4    |          7 |")
            .contains("| 114.152.188.135 |          5 |");
    }

}

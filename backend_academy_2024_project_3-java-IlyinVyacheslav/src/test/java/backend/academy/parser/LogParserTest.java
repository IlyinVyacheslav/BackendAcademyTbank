package backend.academy.parser;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogParserTest {
    public static final LogParser LOG_PARSER = new LogParser();

    @Nested
    class IncorrectLogTests {
        private static void assertThrowsIncorrectLogException(String log, String condition) {
            assertThatThrownBy(() -> LOG_PARSER.parseLog(log))
                .as(String.format("If %s IncorrectLogException should be thrown", condition))
                .isInstanceOf(IncorrectLogException.class);
        }

        @Test
        void testNullLog() {
            LogRecord parsedLog = LOG_PARSER.parseLog(null);

            assertThat(parsedLog).isNull();
        }

        @Test
        void testEmptyLog() {
            LogRecord parsedLog = LOG_PARSER.parseLog("");

            assertThat(parsedLog).isNull();
        }

        @Test
        void testLogWithoutTimeStamp() {
            String logWithoutTimeStamp =
                "93.180.71.3 - - \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithoutTimeStamp, "log misses local time");
        }

        @Test
        void testLogWithoutRemoteAddress() {
            String logWithoutRemoteAddress =
                " - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithoutRemoteAddress, "log misses remote address");
        }

        @Test
        void testLogWithoutRequest() {
            String logWithoutRequest =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithoutRequest, "log misses request");
        }

        @Test
        void testLogWithoutStatusCode() {
            String logWithoutStatusCode =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithoutStatusCode, "log misses status code");
        }

        @Test
        void testLogWithoutBodyBytesSent() {
            String logWithoutBodyBytesSent =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200  \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithoutBodyBytesSent, "log misses body bytes sent");
        }

        @Test
        void testLogWithNegativeBodyBytesSent() {
            String logWithNegativeBodyBytesSent =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 -10 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

            assertThrowsIncorrectLogException(logWithNegativeBodyBytesSent, "body bytes sent can not be negative");
        }

        @Test
        void testLogWithoutUserAgent() {
            String logWithoutUserAgent =
                "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 200 0 \"-\" \"\"";

            assertThrowsIncorrectLogException(logWithoutUserAgent, "log misses user agent");
        }

        @Nested
        class IncorrectStatusCodeTests {
            private final String condition = "status code should contain 3 digits";

            @Test
            void testLogWithNonDigitStatusCode() {
                String logWithShortStatusCode =
                    "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" cod 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

                assertThrowsIncorrectLogException(logWithShortStatusCode, condition);
            }

            @Test
            void testLogWithShortStatusCode() {
                String logWithShortStatusCode =
                    "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 30 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

                assertThrowsIncorrectLogException(logWithShortStatusCode, condition);
            }

            @Test
            void testLogWithLongStatusCode() {
                String logWithLongStatusCode =
                    "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 5120 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

                assertThrowsIncorrectLogException(logWithLongStatusCode, condition);
            }
        }
    }

    @Nested
    class CorrectLogTests {
        private static final String DEFAULT_VALUE = "-";
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MMM/yyyy:HH:mm:ss Z", java.util.Locale.US);

        private static String defaultValue(String value) {
            if (value == null || value.isEmpty()) {
                return DEFAULT_VALUE;
            }
            return value;
        }

        private static String generateLog(
            String remoteAddress,
            String remoteUser,
            String localTime,
            String requestType,
            String recourse,
            String protocol,
            int status,
            int bodyBytesSent,
            String httpReferer,
            String httpUserAgent
        ) {

            return String.format("%s - %s [%s] \"%s %s %s\" %d %d \"%s\" \"%s\"",
                remoteAddress,
                defaultValue(remoteUser),
                localTime,
                requestType,
                recourse,
                protocol,
                status,
                bodyBytesSent,
                defaultValue(httpReferer),
                httpUserAgent);
        }

        public static Stream<Arguments> correctLogs() {
            return Stream.of(
                Arguments.of("170.163.195.91", "", "05/Nov/2024:10:55:44 +0000",
                    "GET", "/system-worthy-Cloned-hierarchy-De-engineered%20structure.svg",
                    "HTTP/1.1", 500, 62, "",
                    "Mozilla/5.0 (Windows 98; Win 9x 4.90) AppleWebKit/5330 (KHTML, like Gecko) Chrome/37.0.802.0 Mobile Safari/5330"
                ),
                Arguments.of(
                    "177.36.119.79", "", "05/Nov/2024:10:55:45 +0000",
                    "PUT", "/synergy_bandwidth-monitored/tangible.jpg",
                    "HTTP/1.1", 200, 2838, "",
                    "Mozilla/5.0 (Macintosh; PPC Mac OS X 10_6_1) AppleWebKit/5341 (KHTML, like Gecko) Chrome/37.0.892.0 Mobile Safari/5341"
                ),
                Arguments.of(
                    "205.51.218.106", "", "05/Nov/2024:10:55:46 +0000",
                    "GET", "/emulation/contingency/Assimilated/software/Reverse-engineered.htm",
                    "HTTP/1.1", 200, 2041, "",
                    "Opera/10.79 (Macintosh; U; PPC Mac OS X 10_6_2; en-US) Presto/2.11.270 Version/11.00"
                ),
                Arguments.of(
                    "80.91.33.133", "", "17/May/2015:08:05:04 +0000",
                    "GET", "/downloads/product_1",
                    "HTTP/1.1", 304, 0, "",
                    "Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)"
                ),
                Arguments.of(
                    "153.200.158.235", "", "05/Nov/2024:10:57:16 +0000",
                    "GET", "/conglomeration/Ergonomic.svg",
                    "HTTP/1.1", 200, 1763, "",
                    "Opera/9.48 (Windows 98; Win 9x 4.90; en-US) Presto/2.10.223 Version/11.00"
                )

            );
        }

        @ParameterizedTest
        @MethodSource("correctLogs")
        void testCorrectLogs(
            String remoteAddress, String remoteUser, String localTime,
            String requestType, String recourse, String protocol, int status,
            int bodyBytesSent, String httpReferer, String httpUserAgent
        ) {
            String log = generateLog(remoteAddress, remoteUser, localTime, requestType, recourse, protocol, status,
                bodyBytesSent, httpReferer, httpUserAgent);

            LogRecord parsedLog = LOG_PARSER.parseLog(log);

            assertThat(parsedLog.remoteAddress()).isEqualTo(remoteAddress);
            assertThat(parsedLog.remoteUser()).isEqualTo(remoteUser);
            assertThat(parsedLog.localTime().format(DATE_TIME_FORMATTER)).isEqualTo(localTime);
            assertThat(parsedLog.request().requestType()).isEqualTo(requestType);
            assertThat(parsedLog.request().resource()).isEqualTo(recourse);
            assertThat(parsedLog.request().protocol()).isEqualTo(protocol);
            assertThat(parsedLog.status()).isEqualTo(status);
            assertThat(parsedLog.bodyBytesSent()).isEqualTo(bodyBytesSent);
            assertThat(parsedLog.httpReferer()).isEqualTo(httpReferer);
            assertThat(parsedLog.httpUserAgent()).isEqualTo(httpUserAgent);
        }
    }
}

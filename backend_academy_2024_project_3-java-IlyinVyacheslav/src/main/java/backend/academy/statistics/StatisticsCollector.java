package backend.academy.statistics;

import backend.academy.parser.LogRecord;
import com.google.common.math.Quantiles;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class StatisticsCollector {
    public static final String REQUEST_NUMBER_DESCRIPTION = "Количество запросов";
    public static final String AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION = "Средний размер ответа";
    public static final String P_95_SERVER_RESPONSE_SIZE_DESCRIPTION = "95p размера ответа";
    public static final String RESOURCE_FREQUENCY_MAP_DESCRIPTION = "Запрашиваемые ресурсы";
    public static final String RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION = "Коды ответа";

    private static final int P_95_INDEX = 95;
    private static final int MINIMAL_ERROR_STATUS = 400;
    private static final int PERCENT_NORMALIZATION = 100;

    public Statistics getStatistics(Stream<LogRecord> logRecordStream) {
        AtomicInteger requestsNumber = new AtomicInteger();
        Map<String, Integer> resourceFrequencyMap = new LinkedHashMap<>();
        Map<Integer, Integer> responseCodesFrequencyMap = new LinkedHashMap<>();
        Map<String, Integer> remoteAddressFrequencyMap = new LinkedHashMap<>();
        AtomicDouble totalResponseSize = new AtomicDouble(0.0);
        List<Integer> responseSizes = new ArrayList<>();
        AtomicInteger errorCodesCount = new AtomicInteger();

        logRecordStream.forEach(logRecord -> {
            requestsNumber.incrementAndGet();
            resourceFrequencyMap.merge(logRecord.request().resource(), 1, Integer::sum);
            responseCodesFrequencyMap.merge(logRecord.status(), 1, Integer::sum);
            remoteAddressFrequencyMap.merge(logRecord.remoteAddress(), 1, Integer::sum);
            totalResponseSize.addAndGet(logRecord.bodyBytesSent());
            responseSizes.add(logRecord.bodyBytesSent());
            if (logRecord.status() >= MINIMAL_ERROR_STATUS) {
                errorCodesCount.incrementAndGet();
            }
        });

        double averageResponseSize = requestsNumber.get() == 0
            ? 0
            : totalResponseSize.get() / requestsNumber.get();

        double percentile95 =
            responseSizes.isEmpty() ? 0 : Quantiles.percentiles().index(P_95_INDEX).compute(responseSizes);

        double errorRate = requestsNumber.get() == 0 ? 0 : (double) errorCodesCount.get() / requestsNumber.get()
                                                           * PERCENT_NORMALIZATION;

        return new Statistics(
            requestsNumber.get(),
            resourceFrequencyMap,
            responseCodesFrequencyMap,
            remoteAddressFrequencyMap,
            averageResponseSize,
            percentile95,
            errorRate
        );
    }
}

package backend.academy.statistics;

import java.util.Map;

public record Statistics(
    int requestsNumber,
    Map<String, Integer> resourceFrequencyMap,
    Map<Integer, Integer> responseCodesFrequencyMap,
    Map<String, Integer> remoteAddressFrequencyMap,
    double averageServerResponseSize,
    double p95ServerResponseSize,
    double errorRate
) {
    public static final String REQUEST_NUMBER_DESCRIPTION = "Количество запросов";
    public static final String AVERAGE_SERVER_RESPONSE_SIZE_DESCRIPTION = "Средний размер ответа";
    public static final String P_95_SERVER_RESPONSE_SIZE_DESCRIPTION = "95p размера ответа";
    public static final String RESOURCE_FREQUENCY_MAP_DESCRIPTION = "Запрашиваемые ресурсы";
    public static final String RESPONSE_CODES_FREQUENCY_MAP_DESCRIPTION = "Коды ответа";
    public static final String REMOTE_ADDRESS_FREQUENCY_MAP_DESCRIPTION = "Адреса пользователей";
    public static final String ERROR_RATE_DESCRIPTION = "Процент ошибок";
}

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hsbc.hase.p2g.tp.utils.http.request.RestRequestBean;
import com.hsbc.hase.p2g.tp.utils.http.RestTemplateHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 44032090 on 2017/7/19.
 */
public class RestUtilsForHttp {


    private static final String EQUALSTRING = "=";

    private static final String PATHPARAMPREFFIX = "#";

    private static final String PATHPARAMVALUEDLIMITER = ";";

    private RestUtilsForHttp() {}

    /**
     *
     * getJsonStringFromSystemApi
     *
     * @param url
     * @param param
     * @return String
     */
    public static String getJsonStringFromSystemApi(final String url, final Map<String, String> header, final Object param) {
        try {
            final RestTemplate restTemplate = new RestTemplate();
            final HttpHeaders headers = new HttpHeaders();
            // add headers
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAll(header);
            final HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtils.OBJECTMAPER.writeValueAsString(param), headers);
            // get response
            return restTemplate.postForObject(url, httpEntity, String.class);
        } catch (final Exception e) {
            LoggingUtils.error("Exception: " + e);
            // ------------dummy--delete--------------
            return "{\"responseDetails\":{\"responseCode\":8},\"reasonCode\":[{\"reasonCode\":\"URS0076\"}],\"portfolioOrderCreateInstructionResponse\":[{\"investmentAccount\":{\"accountCurrencyCode\":\"HKD\",\"accountNumber\":\"068827534388\",\"accountProductTypeCode\":\"SS\",\"accountTypeCode\":\"SEM\",\"countryAccountCode\":\"HK\",\"groupMemberAccountCode\":\"HSBC\"},\"orderCreateResponseInfo\":{\"cashHoldAmount\":42743.2,\"currencyCashHoldCode\":\"HKD\",\"currencyProductTradeCode\":\"HKD\",\"executeAllProductIndicator\":false,\"holdQuantityCount\":0,\"orderProcessDate\":\"2017-06-27\",\"orderQuantityCount\":0,\"orderSettlementDayCount\":0,\"portfolioOrderTypeCode\":\"M\",\"recordCaptureDateTime\":\"2017-06-27T14:33:25.782+08:00\",\"shortSellIndicator\":false,\"suspenseSettlementAccountIndicator\":false},\"portfolioOrderId\":[{\"portfolioOrderReferenceNumber\":301291,\"portfolioOrderReferenceTypeCode\":\"P\"}],\"productId\":{\"countryProductTradableCode\":\"HK\",\"productAlternativeClassificationCode\":\"M\",\"productAlternativeNumber\":\"00005\",\"assetClassCode\":\"SEC\"},\"productMultilingualInfo\":[{\"localeProductTextCode\":\"en\",\"productName\":\"HSBCHOLDINGSPLC\"},{\"localeProductTextCode\":\"zh_HK\",\"productName\":\"匯豐控股有限公司\"},{\"localeProductTextCode\":\"zh\",\"productName\":\"汇丰控股有限公司\"}]}],\"coreReserveArea\":[{\"coreReserveArea\":\"examplecorereservearea\"}],\"localFieldsArea\":[{\"localFieldsArea\":\"examplelocalfieldarea\"}]}";
            // ------------dummy--delete--------------
        }
    }

    /**
     *
     * @param restRequestBean
     * @return
     * @throws Throwable
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object callApi(final RestRequestBean restRequestBean) throws RestClientException, JsonProcessingException{
        Object responseEntity = "{}";
        try {
            if (HttpMethod.GET.equals(restRequestBean.getMethod())) {
                restRequestBean.setUrl(buildPathParameters(restRequestBean.getUrl(), restRequestBean.getPathParameters()));
                restRequestBean.setUrl(buildQueryParameters(restRequestBean.getUrl(), restRequestBean.getParameters()));
                responseEntity = RestTemplateHelper.RESTTEMPLATE.exchange(restRequestBean.getUrl(), restRequestBean.getMethod(),
                        new HttpEntity<>(buildHeader(restRequestBean.getCustomerHeader())), restRequestBean.getResponseType())
                        .getBody();
            } else {
                restRequestBean.setUrl(buildPathParameters(restRequestBean.getUrl(), restRequestBean.getPathParameters()));
                responseEntity = RestTemplateHelper.RESTTEMPLATE.exchange(restRequestBean.getUrl(), restRequestBean.getMethod(),
                        new HttpEntity<>(JSONUtils.OBJECTMAPER.writeValueAsString(restRequestBean.getParameters()), buildHeader(restRequestBean
                                .getCustomerHeader())), restRequestBean.getResponseType()).getBody();
            }

        } catch (final RestClientException rse) {
            LoggingUtils.error("Get Json From System Api Error.", rse);
            throw rse;
        } catch (final JsonProcessingException jpe) {
            LoggingUtils.error("Data Format Transform Error.", jpe);
            throw  jpe;
        }
        return responseEntity;
    }

    public static HttpHeaders buildHeader(final Map<String, String> customerHeader){
        if (customerHeader == null) {
            throw new IllegalArgumentException("Http Header Is Not Empty Or Null !");
        }
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAll(customerHeader);
        return httpHeaders;
    }


    public static <T> String buildPathParameters(final String url, final Map<String, T> pathParameters)
            throws JsonProcessingException {
        if (url.indexOf(PATHPARAMPREFFIX) == -1 || pathParameters == null) {
            return url;
        }
        String replaceString = url;
        final Iterator<Map.Entry<String, T>> entryIterator = pathParameters.entrySet().iterator();
        for (; entryIterator.hasNext();) {
            final Map.Entry<String, T> entry = entryIterator.next();
            final String paramName = entry.getKey();
            final Object paramValue = entry.getValue();
            if (paramValue instanceof String) {
                // optimization
                replaceString = replaceString.replace(PATHPARAMPREFFIX + paramName, String.valueOf(paramValue));
            } else if (paramValue instanceof Map) {
                // optimization
                replaceString = replaceString.replace(PATHPARAMPREFFIX + paramName, buildStringByDelimiter(paramValue,
                        PATHPARAMVALUEDLIMITER, paramName));
            }

        }

        LoggingUtils.error("URL ->" + replaceString);
        return replaceString;
    }


    @SuppressWarnings("rawtypes")
    public static String buildStringByDelimiter(final Object pathParamValue, final String delimiter, final String defaultValue)
            throws JsonProcessingException {


        if (pathParamValue instanceof Map) {
            final StringBuffer sb = new StringBuffer();

            final Iterator iterator = ((Map) pathParamValue).keySet().iterator();

            for (; iterator.hasNext();) {
                final String key = String.valueOf(iterator.next());
                final Object value = ((Map) pathParamValue).get(key);
                sb.append(key);
                sb.append(EQUALSTRING);
                sb.append((value instanceof String) ? value : JSONUtils.OBJECTMAPER.writeValueAsString(value));
                sb.append(delimiter);
            }

            return sb.toString();

        }
        return defaultValue;
    }

    public static String buildQueryParameters(final String url, final Map<String, Object> parameters)
            throws JsonProcessingException {
        if (parameters == null) {
            return url;
        }
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        final Iterator<Map.Entry<String, Object>> entrySet = parameters.entrySet().iterator();

        for (; entrySet.hasNext();) {
            final Map.Entry<String, Object> entry = entrySet.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof String) {
                builder.queryParam(key, value);
            } else {
                builder.queryParam(key, JSONUtils.OBJECTMAPER.writeValueAsString(value));
            }
        }

        LoggingUtils.error("Get Method  Url -> " + builder.build().encode().toUriString());
        return builder.build().encode().toUriString();
    }
}

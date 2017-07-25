import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Created by 44032090 on 2017/7/7.
 */
public class RestRequestBean<T> extends AbstractRequestBean {
    private String url;
    private HttpMethod method;
    private Class responseType;
    private Map<String, String> customerHeader;
    private Map<String, Object> parameters;
    private Map<String, T> pathParameters;



    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    /**
     *
     * @param method
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Class getResponseType() {
        return responseType;
    }

    /**
     *
     * @param responseType
     */
    public void setResponseType(Class responseType) {
        this.responseType = responseType;
    }

    public Map<String, String> getCustomerHeader() {
        return customerHeader;
    }

    /**
     *
     * @param customerHeader
     */
    public void setCustomerHeader(Map<String, String> customerHeader) {
        this.customerHeader = customerHeader;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     *
     * @param parameters
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }


    /**
     *
     * @param pathParameters
     */
    public void setPathParameterses(Map<String, T> pathParameters) {
        this.pathParameters = pathParameters;
    }

    public Map<String, T> getPathParameters() {
        return pathParameters;
    }
}

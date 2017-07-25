
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 44032090 on 2017/7/17.
 */
public final class RestTemplateHelper {
    private RestTemplateHelper(){};
    public static final RestTemplate RESTTEMPLATE;
    static {
        RESTTEMPLATE = new RestTemplate();
        final ClientHttpRequestInterceptor ri = new RestRequestInterceptorForLog();
        final List<ClientHttpRequestInterceptor> ris = new ArrayList<>();
        ris.add(ri);
        RESTTEMPLATE.setInterceptors(ris);
        RESTTEMPLATE.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }
}

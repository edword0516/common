import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by 44032090 on 2017/7/19.
 */
public class JSONUtils {

    private JSONUtils(){};

    public static final ObjectMapper OBJECTMAPER = new ObjectMapper();

    static {
        OBJECTMAPER.setVisibility(OBJECTMAPER.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }
}

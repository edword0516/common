import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 44032090 on 2017/7/19.
 */
public final class LoggingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

    private static final String HEADER = " [HASE PIB] : ";
    private static final String MSGPREFIX = "{Method Name: ";
    private static final String MSGSUBFFIX = "}";
    private static final int CALLSITEINDEX = 3;
    private static final String MSG = "Message: ";
    private static final String DELIMIT = ", ";
    private static final String METHOD_NAME = "{ Method Name : info }";

    private LoggingUtils() {}

    /**
     * print error
     *
     * @param message
     */
    public static void info(final String message) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isInfoEnabled()) {
                logger.info(buildMessage(stackTraceElement.getMethodName(), message));
            }
        } catch (ClassNotFoundException e) {

            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     * @param throwable
     */
    public static void info(final String message, final Throwable throwable) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isInfoEnabled()) {
                logger.info(buildMessage(stackTraceElement.getMethodName(), message), throwable);
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     */
    public static void warn(final String message) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isWarnEnabled()) {
                logger.warn(buildMessage(stackTraceElement.getMethodName(), message));
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     * @param throwable
     */
    public static void warn(final String message, final Throwable throwable) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isWarnEnabled()) {
                logger.warn(buildMessage(stackTraceElement.getMethodName(), message), throwable);
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     */
    public static void debug(final String message) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isDebugEnabled()) {
                logger.error(buildMessage(stackTraceElement.getMethodName(), message));
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     * @param throwable
     */
    public static void debug(final String message, final Throwable throwable) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isDebugEnabled()) {
                logger.error(buildMessage(stackTraceElement.getMethodName(), message), throwable);
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     */
    public static void error(final String message) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isErrorEnabled()) {
                logger.error(buildMessage(stackTraceElement.getMethodName(), message));
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }

    /**
     * print error
     *
     * @param message
     * @param throwable
     */
    public static void error(final String message, final Throwable throwable) {
        try {
            final StackTraceElement stackTraceElement = getCurrentCallSite();
            final Logger logger = getCallSiteLogger(Class.forName(stackTraceElement.getClassName()));
            if (logger.isErrorEnabled()) {
                logger.error(buildMessage(stackTraceElement.getMethodName(), message), throwable);
            }
        } catch (ClassNotFoundException e) {
            LoggingUtils.LOGGER.error(LoggingUtils.HEADER + METHOD_NAME, e);
        }
    }


    private static String buildMessage(final String methodName, final String message) {
        final StringBuilder sb = new StringBuilder();
        sb.append(LoggingUtils.HEADER);
        sb.append(LoggingUtils.MSGPREFIX);
        sb.append(methodName);
        sb.append(LoggingUtils.DELIMIT);
        sb.append(LoggingUtils.MSG);
        sb.append(message);
        sb.append(LoggingUtils.MSGSUBFFIX);
        return sb.toString();
    }


    @SuppressWarnings("rawtypes")
    private static Logger getCallSiteLogger(final Class callSiteClass) {
        return LoggerFactory.getLogger(callSiteClass);
    }

    private static StackTraceElement getCurrentCallSite() {
        final StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        return traceElements[LoggingUtils.CALLSITEINDEX];
    }
}

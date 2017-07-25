import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 44032090 on 2017/7/19.
 */
public final class DateUtils {

    private DateUtils(){};

    private static final String DEFAULTLOCALE = "en";

    private static final String DEFAULTDATEFORMAT = "yyyy-MM-dd";

    private static final String DEFAULTDATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss a";

    private static final String DEFAULTTIMEZONE= "Asia/Shanghai";

    private static final String DEFAULTTIMEFORMAT = "HH:mm:ss";

    private static final String TESTDATEKEYNAME = "p2g.system.testdate";

    private static final String TESTDATEFORMATVALUENAME = "p2g.system.testdate.format";

    private static final String OFFSETPREFIXREGEX = "^[+,-]\\d*";

    private static Map<String, String> defaultDateAndTimeSetup = Collections.emptyMap();
    static {
        try{
            Properties p = new Properties();
            p.load(ClassUtils.getDefaultClassLoader().getResourceAsStream("systemdate.properties"));
            defaultDateAndTimeSetup = (Map) p;
        } catch (Exception e) {
            LoggingUtils.error("systemdate.properties", e);
        }
    }

    /**
     *
     * @return, ""
     */
    public static String getCurrentTime() {
        return getCurrentTime(DEFAULTTIMEFORMAT, DEFAULTLOCALE , DEFAULTTIMEZONE);
    }

    /**
     *
     * @param timeFormat
     * @return
     */
    public static String getCurrentTime(final String timeFormat) {
        if(StringUtils.isBlank(timeFormat)){
            throw new IllegalArgumentException("timeFormat Zone Is Not Null Or Empty!");
        }
        return getCurrentTime(timeFormat, DEFAULTLOCALE , DEFAULTTIMEZONE);
    }

    /**
     *
     * @param zoneOffset
     * @return
     */
    public static String getCurrentDateTimeByZoneOffset(String zoneOffset, String dateTimeFormat){
        if(StringUtils.isBlank(zoneOffset) || StringUtils.isBlank(dateTimeFormat)){
            throw new IllegalArgumentException("zoneOffset, dateTimeFormat Is Not Null Or Empty!");
        }
        return OffsetDateTime.of(LocalDate.now(), LocalTime.now(), getZoneByOffset(zoneOffset)).format(DateTimeFormatter.ofPattern(dateTimeFormat, new Locale("en")));
    }

    /**
     *
     * @param timeFormat
     * @param locale
     * @return
     */
    public static String getCurrentTime(final String timeFormat, final String locale){
        if(StringUtils.isBlank(timeFormat) || StringUtils.isBlank(locale)){
            throw new IllegalArgumentException("timeFormat, locale  Is Not Null Or Empty!");
        }
        return getCurrentTime(timeFormat, locale , DEFAULTTIMEZONE);
    }

    /**
     *
     * @param timeFormat
     * @param locale
     * @param timeZone
     * @return
     */
    public static String getCurrentTime(final String timeFormat, final String locale, final String timeZone) {
        LoggingUtils.error("timeFormat : " + timeFormat + ", timeZone : " + timeZone + ", locale : " + locale);
        if(StringUtils.isBlank(timeFormat) || StringUtils.isBlank(locale) || StringUtils.isBlank(timeZone)){
            throw new IllegalArgumentException("timeFormat, locale, timeZone,  Is Not Null Or Empty!");
        }
        return LocalTime.now(getZoneId(timeZone)).format(DateTimeFormatter.ofPattern(timeFormat, new Locale(locale)));
    }

    /**
     *
     * @param specificTime
     * @param offsetTime, +000000,HHmmss,-000000,HHmmss
     * @param specificTimeFormat
     * @param delimiter, .,:
     * @return
     */
    public static boolean currentTimeIsAfterSpecificTime(final String specificTime,
                                                         final String offsetTime,
                                                         final String delimiter,
                                                         final String specificTimeFormat){
        return currentTimeIsAfterSpecificTime(specificTime, offsetTime, delimiter, specificTimeFormat, null);
    }


    /**
     *
     * @param specificTime
     * @param offsetTime
     * @param delimiter
     * @param specificTimeFormat
     * @param offsetZoneId
     * @return
     */
    public static boolean currentTimeIsAfterSpecificTime(final String specificTime,
                                                         final String offsetTime,
                                                         final String delimiter,
                                                         final String specificTimeFormat,
                                                         final String offsetZoneId){
        LoggingUtils.error("specificTime : " + specificTime + ", offsetTime : " + offsetTime + ", delimiter : " + delimiter + ", specificTimeFormat" + specificTimeFormat);
        boolean result = false;
        if(StringUtils.isBlank(specificTime) || StringUtils.isBlank(specificTimeFormat)){
            throw new IllegalArgumentException("specificTime, offsetTime, delimiter or specificTimeFormat  invalidate !");
        }
        String offsetTimeString = offsetTime;
        if(StringUtils.isNotBlank(delimiter)){
            offsetTimeString = offsetTimeString.replaceAll("\\" + delimiter, "");
        }
        String offSetPrefix = "";
        if(offsetTimeString.matches(OFFSETPREFIXREGEX)){
            offSetPrefix = offsetTimeString.substring(0, 1);
        } else {
            offSetPrefix = "+";
            offsetTimeString = offSetPrefix + offsetTimeString;
        }
        if(StringUtils.isNotBlank(offsetZoneId) && offsetZoneId.matches(OFFSETPREFIXREGEX)){
            OffsetDateTime offsetDateTimeNow = LocalDate.now().atTime(LocalTime.now().atOffset(getZoneByOffset(offsetZoneId)));
            OffsetDateTime offsetDateTimeBySpecific = LocalDateTime.of(LocalDate.now(), LocalTime.parse(specificTime, DateTimeFormatter.ofPattern(specificTimeFormat, new Locale(DEFAULTLOCALE)))).atOffset(getZoneByOffset(offsetZoneId));
            offsetDateTimeBySpecific = offsetDateTimeBySpecific.plusHours(Integer.parseInt(offsetTimeString.substring(0,3)));
            offsetDateTimeBySpecific = offsetDateTimeBySpecific.plusMinutes(Integer.parseInt(offSetPrefix + offsetTimeString.substring(3,5)));
            offsetDateTimeBySpecific = offsetDateTimeBySpecific.plusSeconds(Integer.parseInt(offSetPrefix + offsetTimeString.substring(5,7)));
            result = offsetDateTimeNow.isAfter(offsetDateTimeBySpecific);
        } else {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(specificTime, DateTimeFormatter.ofPattern(specificTimeFormat, new Locale(DEFAULTLOCALE))));
            localDateTime = localDateTime.plusHours(Integer.parseInt(offsetTimeString.substring(0,3)));
            localDateTime = localDateTime.plusMinutes(Integer.parseInt(offSetPrefix + offsetTimeString.substring(3,5)));
            localDateTime = localDateTime.plusSeconds(Integer.parseInt(offSetPrefix + offsetTimeString.substring(5,7)));
            result = LocalDateTime.now().isAfter(localDateTime);
        }

        return result;
    }

    /**
     *
     * @param startTime
     * @param startTimeOffset
     * @param endTime
     * @param endTimeOffset
     * @param timeFormat
     * @param delimiter
     * @return
     */
    public static boolean currentTimeBetweenStartTimeAndEndTime(final String startTime,
                                                                final String startTimeOffset,
                                                                final String endTime,
                                                                final String endTimeOffset,
                                                                final String delimiter,
                                                                final String timeFormat){
        return currentTimeIsAfterSpecificTime(startTime, startTimeOffset, delimiter,timeFormat) &&
                !currentTimeIsAfterSpecificTime(endTime, endTimeOffset, delimiter,timeFormat);
    }

    /**
     *
     * @param specificDate
     * @param dateFormat
     * @return
     */
    public static String getNextDateBySpecificDate(String specificDate, String dateFormat){
        LoggingUtils.error("specificDate : " + specificDate + ", dateFormat : " + dateFormat);
        if(StringUtils.isBlank(specificDate) || StringUtils.isBlank(dateFormat)){
            throw new IllegalArgumentException("specificDate, specificTimeFormat invalidate !");
        }
        LocalDate localDate = LocalDate.parse(specificDate, DateTimeFormatter.ofPattern(dateFormat, new Locale(DEFAULTLOCALE))).plusDays(1);
        return localDate.format(DateTimeFormatter.ofPattern(dateFormat, new Locale(DEFAULTLOCALE)));
    }






    /**
     *
     * @return
     */

    public static String getCurrentSystemDate() {
        return getCurrentSystemDate(DEFAULTDATEFORMAT, DEFAULTTIMEZONE, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateFormat
     * @return
     */

    public static String getCurrentSystemDate(final String dateFormat){
        LoggingUtils.error("dateFormat : " + dateFormat);
        if(StringUtils.isBlank(dateFormat)){
            throw new IllegalArgumentException("dateFormat Zone Is Not Null Or Empty!");
        }
        return getCurrentSystemDate(dateFormat, DEFAULTTIMEZONE, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateFormat date of string format ,egg : yyyy-MM-dd, dd-MM-yyyy, yyyy-MM-dd HH:mm:ss,
     * @param timeZone
     * @return
     */

    public static String getCurrentSystemDate(final String dateFormat, final String timeZone){
        LoggingUtils.error("dateFormat : " + dateFormat + ", timeZone : " + timeZone);
        if(StringUtils.isBlank(dateFormat) || StringUtils.isBlank(timeZone)){
            throw new IllegalArgumentException("dateFormat and timeZone Is Not Null Or Empty!");
        }
        return getCurrentSystemDate(dateFormat, timeZone, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateFormat
     * @param timeZone
     * @param locale
     * @return
     */

    public static String getCurrentSystemDate(final String dateFormat, final String timeZone, final String locale){
        LoggingUtils.error("dateFormat : " + dateFormat + ", timeZone : " + timeZone + ", locale : " + locale);
        if(StringUtils.isBlank(dateFormat) || StringUtils.isBlank(timeZone) || StringUtils.isBlank(locale)){
            throw new IllegalArgumentException("dateFormat or  Zone or locale Is Not Null Or Empty!");
        }
        LocalDate localDate;
        if(checkTestDate()){

            localDate = LocalDate.now(getZoneId(timeZone));

        } else {
            LoggingUtils.error("test date key : " + defaultDateAndTimeSetup.get(TESTDATEKEYNAME) + ", test date value : " + defaultDateAndTimeSetup.get(TESTDATEFORMATVALUENAME));
            localDate = LocalDate.parse(defaultDateAndTimeSetup.get(TESTDATEKEYNAME), DateTimeFormatter.ofPattern(defaultDateAndTimeSetup.get(TESTDATEFORMATVALUENAME), new Locale(locale)));
        }
        return localDate.format(DateTimeFormatter.ofPattern(dateFormat, new Locale(locale)));
    }


    /**
     *
     * @return
     */

    public static String getCurrentSystemDateTime() {
        return getCurrentSystemDateTime(DEFAULTDATETIMEFORMAT, DEFAULTTIMEZONE, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateTimeFormat
     * @return
     */

    public static String getCurrentSystemDateTime(final String dateTimeFormat){
        LoggingUtils.error("dateTimeFormat : " + dateTimeFormat);
        if(StringUtils.isBlank(dateTimeFormat)){
            throw new IllegalArgumentException("dateFormat Zone Is Not Null Or Empty!");
        }
        return getCurrentSystemDateTime(dateTimeFormat, DEFAULTTIMEZONE, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateTimeFormat date of string format ,egg : yyyy-MM-dd, dd-MM-yyyy, yyyy-MM-dd HH:mm:ss,
     * @param timeZone
     * @return
     */

    public static String getCurrentSystemDateTime(final String dateTimeFormat, final String timeZone){
        LoggingUtils.error("dateTimeFormat : " + dateTimeFormat + ", timeZone : " + timeZone);
        if(StringUtils.isBlank(dateTimeFormat) || StringUtils.isBlank(timeZone)){
            throw new IllegalArgumentException("dateTimeFormat and timeZone Is Not Null Or Empty!");
        }
        return getCurrentSystemDateTime(dateTimeFormat, timeZone, DEFAULTLOCALE);
    }

    /**
     *
     * @param dateTimeFormat
     * @param timeZone
     * @param locale
     * @return
     */

    public static String getCurrentSystemDateTime(final String dateTimeFormat, final String timeZone, final String locale){
        LoggingUtils.error("dateTimeFormat : " + dateTimeFormat + ", timeZone : " + timeZone + ", locale" + locale);
        if(StringUtils.isBlank(dateTimeFormat) || StringUtils.isBlank(timeZone) || StringUtils.isBlank(locale)){
            throw new IllegalArgumentException("dateTimeFormat or  Zone or locale Is Not Null Or Empty!");
        }
        LocalDateTime localDateTime;
        if(checkTestDate()){
            localDateTime = LocalDateTime.now(getZoneId(timeZone));
        } else {
            LoggingUtils.error("test date key : " + defaultDateAndTimeSetup.get(TESTDATEKEYNAME) + ", test date value : " + defaultDateAndTimeSetup.get(TESTDATEFORMATVALUENAME));
            LocalDate localDate = LocalDate.parse(defaultDateAndTimeSetup.get(TESTDATEKEYNAME), DateTimeFormatter.ofPattern(defaultDateAndTimeSetup.get(TESTDATEFORMATVALUENAME), new Locale(locale)));
            localDateTime = localDate.atTime(LocalTime.now(getZoneId(timeZone)));
        }

        return localDateTime.format(DateTimeFormatter.ofPattern(dateTimeFormat, new Locale(locale)));
    }

    public static String transformDateFormat(String dateString, String sourceFormat, String targetFormat){
        if(StringUtils.isBlank(dateString) || StringUtils.isBlank(sourceFormat) || StringUtils.isBlank(targetFormat)){
            throw new IllegalArgumentException("dateString, sourceFormat, targetFormat or  Zone or locale Is Not Null Or Empty!");
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(sourceFormat, new Locale("en")))
                .format(DateTimeFormatter.ofPattern(targetFormat));
    }


    private static ZoneId getZoneId(String zoneId){
        if(StringUtils.isBlank(zoneId)){
            throw new IllegalArgumentException("zoneId Zone Is Not Null Or Empty!");
        }
        LoggingUtils.error("zoneId :" + zoneId);
        return ZoneId.of(zoneId);
    }


    /**
     *
     * @param offSetHourAndMinute, egg : +0800
     * @return
     * @throws IllegalArgumentException
     */

    private static ZoneOffset getZoneByOffset(final String offSetHourAndMinute) throws IllegalArgumentException{
        if(StringUtils.isBlank(offSetHourAndMinute)){
            throw new IllegalArgumentException("offSetHourAndMinute Zone Is Not Null Or Empty!");
        }
        String offSetPrefix = "";
        if(offSetHourAndMinute.matches(OFFSETPREFIXREGEX)){
            offSetPrefix = offSetHourAndMinute.substring(0, 1);
        }
        LoggingUtils.error("zoneId :" + offSetHourAndMinute);
        return ZoneOffset.ofHoursMinutes(Integer.parseInt(offSetHourAndMinute.substring(0,3)), Integer.parseInt(offSetPrefix+offSetHourAndMinute.substring(3,5)));
    }

    /**
     *
     * @return
     */
    private static boolean checkTestDate(){
        return defaultDateAndTimeSetup == null || StringUtils.isBlank(defaultDateAndTimeSetup.get(TESTDATEKEYNAME));
    }

}

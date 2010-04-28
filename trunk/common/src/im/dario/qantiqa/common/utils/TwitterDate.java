package im.dario.qantiqa.common.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Helper to centralize how dates are handled by Qantiqa, based on Twitter
 * standard.
 * 
 * @author Dario
 * 
 */
public class TwitterDate {
    private final static DateTimeFormatter twitter = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText().appendLiteral(' ')
            .appendMonthOfYearShortText().appendLiteral(' ')
            .appendDayOfMonth(2).appendLiteral(' ').appendHourOfDay(2)
            .appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':')
            .appendSecondOfMinute(2).appendLiteral(" +0000 ").appendYear(4, 4)
            .toFormatter();

    private DateTime dt;

    public TwitterDate() {
        dt = new DateTime(DateTimeZone.UTC);
    }

    public String toString() {
        return twitter.print(dt);
    }
}

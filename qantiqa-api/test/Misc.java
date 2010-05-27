import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Assert;
import org.junit.Test;

public class Misc {

	/**
	 * Testing out the maximum value for Long ids.
	 * 
	 * Just for checking the docs.
	 */
	@Test
	public void testUniqueIdGenerator() {
		System.out.println(Long.MAX_VALUE);
	}

	/**
	 * Testing Joda Time formatter for supporting Twitter date format.
	 */
	@Test
	public void testCreatedAtString() {
		String example = "Sat Aug 15 13:37:28 +0000 2009";

		// Building the formatter...
		DateTime dt = new DateTime(2009, 8, 15, 13, 37, 28, 0, DateTimeZone.UTC);
		DateTimeFormatterBuilder twitter = new DateTimeFormatterBuilder()
				.appendDayOfWeekShortText().appendLiteral(' ')
				.appendMonthOfYearShortText().appendLiteral(' ')
				.appendDayOfMonth(2).appendLiteral(' ').appendHourOfDay(2)
				.appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':')
				.appendSecondOfMinute(2).appendLiteral(" +0000 ").appendYear(4,
						4);

		// Showing out...
		System.out.println(twitter.toFormatter());
		String date = dt.toString(twitter.toFormatter());

		System.out.println(date);
		Assert.assertEquals(example, date);
	}
}

package net.azureaaron.mod.utils;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.ibm.icu.text.DateTimePatternGenerator;

import ca.weblite.objc.Client;
import net.azureaaron.mod.debug.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

/**
 * Provides useful constants for formatting numbers and dates. If you need to make slight tweaks to a formatter
 * then {@link NumberFormat#clone()} the object and modify it as needed.
 */
public class Formatters {
	/**
	 * Formats numbers as integers with commas.
	 * 
	 * Example: 100,000,000
	 */
	public static final NumberFormat INTEGER_NUMBERS = NumberFormat.getIntegerInstance(Locale.CANADA);
	/**
	 * Formats numbers as floats with up to two digits of precision.
	 * 
	 * Example: 100,000.15
	 */
	public static final NumberFormat DOUBLE_NUMBERS = Util.make(NumberFormat.getInstance(Locale.CANADA), nf -> nf.setMaximumFractionDigits(2));
	/**
	 * Formats numbers as floats with up to one digit of precision.
	 * 
	 * Example: 100,000.1
	 */
	public static final NumberFormat FLOAT_NUMBERS = Util.make(NumberFormat.getInstance(Locale.CANADA), nf -> nf.setMaximumFractionDigits(1));
	/**
	 * Formats integer numbers in a short format.
	 * 
	 * Examples: 10B, 1M, and 5K.
	 */
	public static final NumberFormat SHORT_INTEGER_NUMBERS = NumberFormat.getCompactNumberInstance(Locale.CANADA, NumberFormat.Style.SHORT);
	/**
	 * Formats float numbers in a short format.
	 * 
	 * Examples: 17.3B, 1.5M, and 10.8K.
	 */
	public static final NumberFormat SHORT_FLOAT_NUMBERS = Util.make(NumberFormat.getCompactNumberInstance(Locale.CANADA, NumberFormat.Style.SHORT), nf -> nf.setMinimumFractionDigits(1));
	/**
	 * Formats dates to a standard format.
	 * 
	 * Examples: Thu Jan 30 2025 2:00:10 PM, Thu Jan 30 2025 14:00:10
	 */
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E MMM d yyyy " + getTimeFormat(), Locale.US).withZone(getTimeZone());

	/**
	 * Returns the formatting for the time, always returns 12 hour in text environments.
	 */
	private static String getTimeFormat() {
		return is12HourClock() || Debug.isTestEnvironment() ? "h:mm:ss a" : "HH:mm:ss";
	}

	/**
	 * Returns the time zone to be used for date formatting, always returns UTC in test environments.
	 */
	private static ZoneId getTimeZone() {
		return Debug.isTestEnvironment() ? ZoneId.of("UTC") : ZoneId.systemDefault();
	}

	/**
	 * Determines whether to use the 12 or 24 hour clock for formatting time.<br><br>
	 * 
	 * On macOS this reads the preference for the system clock's time format which accounts for whether a user
	 * chooses 12 or 24 hour time in the System Settings.<br><br>
	 * 
	 * On other platforms, the time format follows the default for the user's current locale.
	 * 
	 * @see <a href="https://developer.apple.com/documentation/foundation/nsdateformatter/1408112-dateformatfromtemplate?language=objc">NSDateFormatter</a>
	 * @see <a href="https://www.unicode.org/reports/tr35/tr35-31/tr35-dates.html#Date_Field_Symbol_Table">Unicode Locale Data Markup Language (LDML)</a>
	 */
	private static boolean is12HourClock() {
		//The j formatting template returns the preferred formatting for the time
		//If the format contains a (am/pm pattern) then the preference is to use the 12 hour clock, otherwise its the 24 hour clock
		if (MinecraftClient.IS_SYSTEM_MAC) {
			Object locale = Client.getInstance().send("NSLocale", "currentLocale");
			String timeFormat = (String) Client.getInstance().send("NSDateFormatter", "dateFormatFromTemplate:options:locale:", "j", 0, locale);

			return timeFormat.contains("a");
		} else {
			DateTimePatternGenerator generator = DateTimePatternGenerator.getInstance(Locale.getDefault());
			String timeFormat = generator.getBestPattern("j");

			return timeFormat.contains("a");
		}
	}
}

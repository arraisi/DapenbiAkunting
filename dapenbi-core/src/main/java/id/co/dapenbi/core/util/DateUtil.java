package id.co.dapenbi.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	public static final String DEFAULT_FORMAT = "MMM dd yyyy";

	private static final SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);

	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	public static final Map<String, SimpleDateFormat> cache = new ConcurrentHashMap<String, SimpleDateFormat>(5);

	public static Date stringToDate(String value) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);

		return date;
	}

	public static Date stringToDate(String format, String value) throws ParseException {
		Date date = new SimpleDateFormat(format).parse(value);

		return date;
	}

	public static String format(Date date) {
		String result = "";
		try {
			if (MAX_DATE.equals(date))
				result = "N/A";
			else
				result = sdf.format(date);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return result;
	}

	public static String format(Date date, String pattern) {
		String result = "";
		try {
			SimpleDateFormat formatter = cache.get(pattern);
			if (null == formatter) {
				formatter = new SimpleDateFormat(pattern);
				cache.put(pattern, formatter);
			}
			if (date != null) {
				result = formatter.format(date);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return result;
	}

	public static Date MAX_DATE;
	static {
		try {
			Calendar cal = GregorianCalendar.getInstance();
			cal.clear();
			cal.set(4712, 11, 31);
			MAX_DATE = cal.getTime();
		} catch (Exception ex) {
		}
	}

	public static Date truncate(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	public static Calendar truncate(Calendar cal) {
		return DateUtils.truncate(cal, Calendar.DATE);
	}

	public static Date add(Date date, int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return cal.getTime();
	}

	public static boolean before(Date date, Date when) {
		try {
			return sdf.parse(sdf.format(date)).before(sdf.parse(sdf.format(when)));
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	public static boolean compareDate(Date dateFrom, Date dateTo) {
		boolean status = false;
		if (dateTo == null) {
			dateTo = MAX_DATE;
		}

		if (dateFrom != null) {
			if (dateTo.before(dateFrom)) {
				status = true;
			} else {
				status = false;
			}
		}
		return status;
	}

	public static boolean isBetween(Date toCompare, Date minDate, Date maxDate) {
		if (toCompare.compareTo(minDate) >= 0 && toCompare.compareTo(maxDate) <= 0)
			return true;
		return false;
	}

	public static boolean isBetweenExclusive(Date toCompare, Date minDate, Date maxDate) {
		if (toCompare.compareTo(minDate) > 0 && toCompare.compareTo(maxDate) < 0)
			return true;
		return false;
	}

	public static boolean isBetweenOrEquals(Date toCompare, Date minDate, Date maxDate) {
		if (toCompare.after(minDate) && toCompare.before(maxDate) || toCompare.equals(minDate)
				|| toCompare.equals(maxDate))
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public static Date changeDateToOrigin(Long time) {
		Date afterChangeDate = new Date(time);
		afterChangeDate.setDate(01);
		afterChangeDate.setMonth(Calendar.JANUARY);
		afterChangeDate.setYear(70);
		return new Date(afterChangeDate.getTime());
	}

	public static long rangeTwoDate(Date startDate, Date endDate) {
		return ((DateUtils.truncate(endDate, Calendar.DATE).getTime()
				- DateUtils.truncate(startDate, Calendar.DATE).getTime()) / (24 * 60 * 60 * 1000));
	}

	public static Period getPeriods(Date birthDate) {
		Calendar today = Calendar.getInstance();
		Calendar date = Calendar.getInstance();
		date.setTime(birthDate);
		Period period = new Period(date.getTimeInMillis(), today.getTimeInMillis(), PeriodType.yearMonthDay());
		return period;
	}

	public static String getProgressTime(Date receivedDate, Date actionDate) {
		String progressTime = "";
		int days = (int) ((actionDate.getTime() - receivedDate.getTime()) / (1000 * 60 * 60 * 24));
		int difhours = (int) ((actionDate.getTime() - receivedDate.getTime()) % (1000 * 60 * 60 * 24));
		int hours = (int) ((difhours) / (1000 * 60 * 60));
		progressTime = days + " day(s), " + hours + " hour(s)";
		return progressTime;
	}

	public static Date getStartOfYear(int year) {
		DateTime dt = new DateTime();
		return truncate(dt.withYear(year).withMonthOfYear(1).withDayOfMonth(1).toDate());
	}

	public static Date getEndOfYear(int year) {
		DateTime dt = new DateTime();
		return truncate(dt.withYear(year).withMonthOfYear(12).withDayOfMonth(31).toDate());
	}

	public static Date getStartOfMonth(int year, int month) {
		DateTime dt = new DateTime();
		return truncate(dt.withYear(year).withMonthOfYear(month).withDayOfMonth(1).toDate());
	}

	public static Date getEndOfMonth(int year, int month) {
		DateTime dt = new DateTime();
		return truncate(dt.withYear(year).withMonthOfYear(month).dayOfMonth().withMaximumValue().toDate());
	}

	public static Date getToday() {
		return new Date();
	}

	public static Date getNthDayOfMonth(int n, int day, int month, int year) {
		LocalDate ld = new LocalDate(year, month, 1).withDayOfWeek(day);
		if (n > 1) {
			LocalDate ldNthDay = ld.plusWeeks(n - 1);
			LocalDate lastDayInMonth = ld.dayOfMonth().withMaximumValue();
			if (ldNthDay.isAfter(lastDayInMonth)) {
				throw new IllegalArgumentException(
						"There is no " + n + " " + ld.dayOfWeek().getAsText() + " in this month.");
			}
			return DateUtils.truncate(ldNthDay.toDate(), Calendar.DATE);
		}
		return DateUtils.truncate(ld.toDate(), Calendar.DATE);
	}

	public static int getDateElement(Date dateValue, String element) {
		if (dateValue == null)
			return 0;

		String stringFormat = "";
		switch (element) {
		case "DAY":
			stringFormat = "d";
			break;
		case "MONTH":
			stringFormat = "M";
			break;
		case "YEAR":
			stringFormat = "yyyy";
			break;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(stringFormat);
		return Integer.parseInt(dateFormat.format(dateValue));
	}

	public static Date getDateFromElement(int day, int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);

		return (Date) calendar.getTime();
	}

	public static LocalDateTime toLocalDateTime(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		TimeZone tz = calendar.getTimeZone();
		ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
		return LocalDateTime.ofInstant(calendar.toInstant(), zid);
	}

	public static int differenceOfMonth(Date startDate, Date endDate) {
		Calendar startDateCal = Calendar.getInstance();
		startDateCal.setTime(startDate);

		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTime(endDate);

		java.time.LocalDate startDateLocal = toLocalDateTime(startDateCal).toLocalDate();
		java.time.LocalDate endDateLocal = toLocalDateTime(endDateCal).toLocalDate();

		java.time.Period diff = java.time.Period.between(startDateLocal.withDayOfMonth(1),
				endDateLocal.withDayOfMonth(1));

		return diff.getMonths();
	}
}

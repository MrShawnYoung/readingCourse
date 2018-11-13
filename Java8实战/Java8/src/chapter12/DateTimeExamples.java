package chapter12;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeExamples {
	private static final ThreadLocal<DateFormat> formatters = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("dd-MMM-yyyy");
		}
	};

	public static void main(String[] args) {
		// useOldDate();
		// useLocalDate();
		// useTemporalAdjuster();
		// useDateFormatter();
		// useZoneId();
		// useCalendar();
	}

	private static void useOldDate() {
		Date date = new Date(114, 2, 18);
		System.out.println(date);
		System.out.println(formatters.get().format(date));
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.FEBRUARY, 18);
		System.out.println(calendar);
	}

	private static void useLocalDate() {
		/* 创建一个LocalDate对象并读取其值 */
		LocalDate date = LocalDate.of(2014, 3, 18);
		int year = date.getYear(); // 2014
		Month month = date.getMonth(); // MARCH
		int day = date.getDayOfMonth(); // 18
		DayOfWeek dow = date.getDayOfWeek(); // TUESDAY
		int len = date.lengthOfMonth(); // 31 (days in March)
		boolean leap = date.isLeapYear(); // false (not a leap year)
		System.out.println(date);

		/* 使用TemporalField读取LocalDate的值 */
		int y = date.get(ChronoField.YEAR);
		int m = date.get(ChronoField.MONTH_OF_YEAR);
		int d = date.get(ChronoField.DAY_OF_MONTH);

		/* 创建LocalTime并读取其值 */
		LocalTime time = LocalTime.of(13, 45, 20); // 13:45:20
		int hour = time.getHour(); // 13
		int minute = time.getMinute(); // 45
		int second = time.getSecond(); // 20
		System.out.println(time);

		/* 直接创建LocalDateTime对象，或者通过合并日期和时间的方式创建 */
		LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);// 2014-03-18T13:45
		LocalDateTime dt2 = LocalDateTime.of(date, time);
		LocalDateTime dt3 = date.atTime(13, 45, 20);
		LocalDateTime dt4 = date.atTime(time);
		LocalDateTime dt5 = time.atDate(date);
		System.out.println(dt1);

		LocalDate date1 = dt1.toLocalDate();// 2014-03-18
		System.out.println(date1);
		LocalTime time1 = dt1.toLocalTime();// 13:45:20
		System.out.println(time1);

		Instant instant = Instant.ofEpochSecond(44 * 365 * 86400);
		Instant now = Instant.now();

		Duration d1 = Duration.between(LocalTime.of(13, 45, 10), time);
		Duration d2 = Duration.between(instant, now);
		System.out.println(d1.getSeconds());
		System.out.println(d2.getSeconds());

		/* 创建Duration和Period对象 */
		// Duration threeMinutes = Duration.of(3);
		Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
		System.out.println(threeMinutes);
		Period tenDays = Period.ofDays(10);
		Period threeWeeks = Period.ofWeeks(3);
		Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);

		/* 以比较直观的方式操纵LocalDate的属性 */
		LocalDate withDate = LocalDate.of(2014, 3, 18);// 2014-03-18
		LocalDate date2 = date1.withYear(2011);// 2011-03-18
		LocalDate date3 = date2.withDayOfMonth(25);// 2011-03-25
		LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);// 2011-09-25

		/* 以相对方式修改LocalDate对象的属性 */
		LocalDate date5 = LocalDate.of(2014, 3, 18);// 2014-03-18
		LocalDate date6 = date1.plusWeeks(1);// 2014-03-25
		LocalDate date7 = date2.minusYears(3);// 2011-03-25
		LocalDate date8 = date3.plus(6, ChronoUnit.MONTHS);// 2011-09-25

		JapaneseDate japaneseDate = JapaneseDate.from(date);
		System.out.println(japaneseDate);
	}

	private static void useTemporalAdjuster() {
		LocalDate date = LocalDate.of(2014, 3, 18);// 2014-03-18
		date = date.with(nextOrSame(DayOfWeek.SUNDAY));// 2014-03-23
		System.out.println(date);
		date = date.with(lastDayOfMonth());// 2014-03-31
		System.out.println(date);

		date = date.with(new NextWorkingDay());
		System.out.println(date);
		date = date.with(nextOrSame(DayOfWeek.FRIDAY));
		System.out.println(date);
		date = date.with(new NextWorkingDay());
		System.out.println(date);

		date = date.with(nextOrSame(DayOfWeek.FRIDAY));
		System.out.println(date);
		date = date.with(temporal -> {
			DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
			int dayToAdd = 1;
			if (dow == DayOfWeek.FRIDAY)
				dayToAdd = 3;
			if (dow == DayOfWeek.SATURDAY)
				dayToAdd = 2;
			return temporal.plus(dayToAdd, ChronoUnit.DAYS);
		});
		System.out.println(date);
	}

	private static class NextWorkingDay implements TemporalAdjuster {
		@Override
		public Temporal adjustInto(Temporal temporal) {
			/* 读取当前日期 */
			DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
			/* 正常情况，增加1天 */
			int dayToAdd = 1;
			/* 如果当天是周五，增加3天 */
			if (dow == DayOfWeek.FRIDAY) {
				dayToAdd = 3;
			}
			/* 如果当天是周六，增加2天 */
			else if (dow == DayOfWeek.SATURDAY) {
				dayToAdd = 2;
			}
			/* 增加恰当的天数后，返回修改的日期 */
			return temporal.plus(dayToAdd, ChronoUnit.DAYS);
		}
	}

	private static void useDateFormatter() {
		/* 按照某个模式创建DateTimeFormatter */
		LocalDate date = LocalDate.of(2014, 3, 18);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);

		System.out.println(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
		System.out.println(date.format(formatter));
		System.out.println(date.format(italianFormatter)); // 18. marzo 2014

		/* 构造一个DateTimeFormatter */
		DateTimeFormatter complexFormatter = new DateTimeFormatterBuilder().appendText(ChronoField.DAY_OF_MONTH)
				.appendLiteral(". ").appendText(ChronoField.MONTH_OF_YEAR).appendLiteral(" ")
				.appendText(ChronoField.YEAR).parseCaseInsensitive().toFormatter(Locale.ITALIAN);
		System.out.println(date.format(complexFormatter));
	}

	private static void useZoneId() {
		ZoneId romeZone = ZoneId.of("Europe/Rome");
		/* 为时间点添加时区信息 */
		LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
		ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

		LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
		ZonedDateTime zdt2 = dateTime.atZone(romeZone);

		Instant instant = Instant.now();
		ZonedDateTime zdt3 = instant.atZone(romeZone);
	}

	private static void useCalendar() {
		Chronology japaneseChronology = Chronology.ofLocale(Locale.JAPAN);
		ChronoLocalDate now = japaneseChronology.dateNow();

		/* 在ISO日历中计算当前伊斯兰年中斋月的起始和终止日期 */
		/* 取得当前的Hijrah日期，紧接着对其进行修正，得到斋月的第一天，即第9个月 */
		HijrahDate ramadanDate = HijrahDate.now().with(ChronoField.DAY_OF_MONTH, 1).with(ChronoField.MONTH_OF_YEAR, 9);
		System.out.println("Ramadan starts on " +
		/* IsoChronology.INSTANCE是IsoChronology类的一个静态实例 */
				IsoChronology.INSTANCE.date(ramadanDate) + " and ends on " +
				/* 斋月始于2014-06-28，止于2014-07-27 */
				IsoChronology.INSTANCE.date(ramadanDate.with(TemporalAdjusters.lastDayOfMonth())));
	}
}
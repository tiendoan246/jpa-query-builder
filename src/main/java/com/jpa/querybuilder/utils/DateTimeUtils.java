package com.jpa.querybuilder.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateTimeUtils {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static Timestamp toTimestamp(Long milliseconds){
		if (Objects.isNull(milliseconds)) {
			return null;
		}

		return new Timestamp(milliseconds);
	}

	public static Timestamp toUTCDateTime(Long milliseconds) {
		ZonedDateTime utc = Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.UTC);
		return Timestamp.from(utc.toInstant());
	}

	public static String toUTCDateTimeString(Long milliseconds) {
		ZonedDateTime utc = Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.UTC);
		return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(utc);
	}

	public static Timestamp getCurrentUTCTime() {
		return new Timestamp(OffsetDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli());
	}

	public static Date startOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startOfDay = calendar.getTime();
		return startOfDay;
	}

	public static Date endOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date endOfDay = calendar.getTime();
		return endOfDay;
	}

	public static Date startOfDaySql(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date endOfDaySql(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
}

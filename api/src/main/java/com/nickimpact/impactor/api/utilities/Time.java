package com.nickimpact.impactor.api.utilities;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {

	private long time;

	private static final int secondsPerMinute = 60;
	private static final int secondsPerHour = secondsPerMinute * 60;
	private static final int secondsPerDay = secondsPerHour * 24;
	private static final int secondsPerWeek = secondsPerDay * 7;

	/** Returns a {@link Time} object representing how long the given milliseconds is in weeks, days, hours, minutes and seconds */
	public Time(long seconds)
	{
		this.time = seconds;
	}

	/** Returns a {@link Time} object given the string representation e.g. 3w5d9h3m1s*/
	public Time(String formattedTime) throws IllegalArgumentException
	{
		final Pattern minorTimeString = Pattern.compile("^\\d+$");
		final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?$");

		if(minorTimeString.matcher(formattedTime).matches()) {
			this.time += Long.parseUnsignedLong(formattedTime);
			return;
		}

		Matcher m = timeString.matcher(formattedTime);
		if(m.matches()) {
			this.time =  amount(m.group(2), secondsPerWeek);
			this.time += amount(m.group(4), secondsPerDay);
			this.time += amount(m.group(6), secondsPerHour);
			this.time += amount(m.group(8), secondsPerMinute);
			this.time += amount(m.group(10), 1);
		}
	}

	private long amount(String g, int multiplier) {
		if(g != null && g.length() > 0) {
			return multiplier * Long.parseUnsignedLong(g);
		}

		return 0;
	}

	public long getTime() {
		return this.time;
	}

	public String asShort() {
		if(time < 60) {
			return time + "s";
		} else if(time < 3600){
			return TimeUnit.SECONDS.toMinutes(this.time) + "m";
		} else {
			return TimeUnit.SECONDS.toHours(this.time) + "h";
		}
	}

	@Override
	public String toString()
	{
		if(time <= 0)
			return "Expired";

		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.SECONDS.toHours(this.time),
				TimeUnit.SECONDS.toMinutes(this.time) % 60,
				this.time % 60
		);
	}

}

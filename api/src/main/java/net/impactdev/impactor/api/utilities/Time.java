/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.utilities;

import java.util.StringJoiner;
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

	public String asPatternized() {
		if(time <= 0) {
			return "Expired";
		}

		StringJoiner joiner = new StringJoiner(" ");

		long weeks = TimeUnit.SECONDS.toDays(this.time) / 7;
		long days = TimeUnit.SECONDS.toDays(this.time) % 7;
		long hours = TimeUnit.SECONDS.toHours(this.time) % 24;
		long minutes = TimeUnit.SECONDS.toMinutes(this.time) % 60;
		long seconds = this.time % 60;

		if(weeks > 0) {
			joiner.add(weeks + "w");
		}

		if(days > 0) {
			joiner.add(days + "d");
		}

		if(hours > 0) {
			joiner.add(hours + "h");
		}

		if(minutes > 0) {
			joiner.add(minutes + "m");
		}

		if(seconds > 0) {
			joiner.add(seconds + "s");
		}

		return joiner.toString();
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

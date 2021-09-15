package net.impactdev.impactor.api.services.mojang;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.utilities.Time;

import java.util.List;

public class Report {

	private List<ReportMapping> reports = Lists.newArrayList();

	public void addReport(Status status, Time time) {
		reports.add(new ReportMapping(status, time));
	}

	public List<ReportMapping> getReports() {
		return this.reports;
	}

	public static class ReportMapping {
		private final Status status;
		private final Time downtime;

		public ReportMapping(Status status, Time downtime) {
			this.status = status;
			this.downtime = downtime;
		}

		public Status getStatus() {
			return this.status;
		}

		public Time getDowntime() {
			return this.downtime;
		}
	}
}

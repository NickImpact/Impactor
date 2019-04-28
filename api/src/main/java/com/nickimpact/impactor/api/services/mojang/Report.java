package com.nickimpact.impactor.api.services.mojang;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.utilities.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class Report {

	private List<ReportMapping> reports = Lists.newArrayList();

	public void addReport(Status status, Time time) {
		reports.add(new ReportMapping(status, time));
	}

	public List<ReportMapping> getReports() {
		return this.reports;
	}

	@Getter
	@AllArgsConstructor
	public static class ReportMapping {
		private Status status;
		private Time downtime;
	}
}

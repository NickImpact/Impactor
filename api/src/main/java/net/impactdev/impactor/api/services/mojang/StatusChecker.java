package net.impactdev.impactor.api.services.mojang;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.impactdev.impactor.api.utilities.Time;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class StatusChecker {

	private static final Gson gson = new GsonBuilder().create();

	private Map<ServiceType, Status> statuses = new EnumMap<>(ServiceType.class);
	private Date lastCheck = Date.from(Instant.now());

	public Optional<Report> getReport() {
		Report report = new Report();
		for(ServiceType st : statuses.keySet()) {
			Status status = statuses.get(st);
			if(status.getStatus() != ServiceStatus.GREEN) {
				if(status.getStatus() == ServiceStatus.RED) {
					Date now = Date.from(Instant.now());
					Time time = new Time(now.toInstant().minusSeconds(status.getDownInstant().toInstant().getEpochSecond()).getEpochSecond());
					report.addReport(status, time);
				}
			}
		}

		return Optional.ofNullable(report.getReports().size() > 0 ? report : null);
	}

	public Report getAllReports() {
		Report report = new Report();
		for(ServiceType st : statuses.keySet()) {
			Status status = statuses.get(st);
			Date now = Date.from(Instant.now());
			if(status.getDownInstant() == null) {
				report.addReport(status, null);
			} else {
				Time time = new Time(now.toInstant().minusSeconds(status.getDownInstant().toInstant().getEpochSecond()).getEpochSecond());
				report.addReport(status, time);
			}
		}

		return report;
	}

	public void fetch() throws Exception {
		this.lastCheck = Date.from(Instant.now());
		List<Map<String, String>> mapping = gson.fromJson(readURL("https://status.mojang.com/check"), new TypeToken<List<Map<String, String>>>() {}.getType());
		mapping.forEach(m -> {
			m.forEach((k, v) -> {
				ServiceType service = ServiceType.from(k);
				statuses.put(service, Status.from(service, ServiceStatus.from(v), lastCheck));
			});
		});
	}

	public boolean isAllGood() {
		return statuses.values().stream().allMatch(status -> status.getStatus() == ServiceStatus.GREEN);
	}

	private String readURL(String urlString) throws Exception {
		URL url = new URL(urlString);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
			StringBuilder data = new StringBuilder();

			String line;
			while((line = reader.readLine()) != null) {
				data.append(line);
			}

			return data.toString();
		}
	}
}

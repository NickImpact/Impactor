package com.nickimpact.impactor.api.services.plan.implementations.buycraft;

import com.djrapitops.plan.api.PlanAPI;
import com.djrapitops.plan.api.exceptions.connection.ForbiddenException;
import com.djrapitops.plan.data.element.AnalysisContainer;
import com.djrapitops.plan.data.element.InspectContainer;
import com.djrapitops.plan.data.element.TableContainer;
import com.djrapitops.plan.data.plugin.ContainerSize;
import com.djrapitops.plan.data.plugin.PluginData;
import com.djrapitops.plan.system.settings.Settings;
import com.djrapitops.plan.utilities.FormatUtils;
import com.djrapitops.plan.utilities.html.Html;
import com.djrapitops.plan.utilities.html.icon.Color;
import com.djrapitops.plan.utilities.html.icon.Family;
import com.djrapitops.plan.utilities.html.icon.Icon;
import com.djrapitops.pluginbridge.plan.buycraft.ListPaymentRequest;
import com.djrapitops.pluginbridge.plan.buycraft.MoneyStackGraph;
import com.djrapitops.pluginbridge.plan.buycraft.Payment;

import java.util.*;

public class Buycraft extends PluginData {

	private final String secret = Settings.PLUGIN_BUYCRAFT_SECRET.toString();

	public Buycraft() {
		super(ContainerSize.TAB, "Buycraft");
	}

	@Override
	public InspectContainer getPlayerData(UUID uuid, InspectContainer inspectContainer) throws Exception {
		return inspectContainer;
	}

	@Override
	public AnalysisContainer getServerData(Collection<UUID> uuids, AnalysisContainer fillThis) throws Exception {
		if(secret.equalsIgnoreCase("-") || secret.isEmpty()) {
			fillThis.addHtml("error", "<div style=\"color: red; text-align: center\">Configuration Error, unable to load data...</div>");
			return fillThis;
		}

		try {
			List<Payment> payments = new ListPaymentRequest(secret).makeRequest();
			Collections.sort(payments);

			addPaymentTotals(fillThis, payments);
			addPlayerTable(fillThis, payments);
		} catch (ForbiddenException e) {
			fillThis.addHtml("error", "<div style=\"color: red; text-align: center\">Invalid Server Secret, unable to show data...</div>");
		}

		return fillThis;
	}

	private void addPlayerTable(AnalysisContainer analysisContainer, List<Payment> payments) {
		TableContainer payTable = new TableContainer(
				true,
				getWithIcon("Date", Icon.called("calendar").of(Family.REGULAR)),
				getWithIcon("Amount", Icon.called("money-bill-wave")),
				getWithIcon("Packages", Icon.called("cube"))
		);
		payTable.setColor("blue");
		for (Payment payment : payments) {
			String name = payment.getPlayerName();
			payTable.addRow(
					Html.LINK.parse(PlanAPI.getInstance().getPlayerInspectPageLink(name), name),
					FormatUtils.formatTimeStampYear(payment.getDate()),
					FormatUtils.cutDecimals(payment.getAmount()) + " " + payment.getCurrency(),
					payment.getPackages()
			);
		}
		analysisContainer.addTable("payTable", payTable);

		MoneyStackGraph moneyStackGraph = MoneyStackGraph.create(payments);
		String graphHtml = Html.PANEL_BODY.parse("<div id=\"buycraftChart\" class=\"dashboard-flot-chart\"></div>") +
				"<script>$(function () {setTimeout(function() {" +
				"stackChart('buycraftChart', "
				+ moneyStackGraph.toHighChartsLabels() + ", "
				+ moneyStackGraph.toHighChartsSeries() + ", '');}, 1000)});</script>";

		analysisContainer.addHtml("moneygraph", graphHtml);
	}

	private void addPaymentTotals(AnalysisContainer analysisContainer, List<Payment> payments) {
		Map<String, Double> paymentTotals = new HashMap<>();
		for (Payment payment : payments) {
			String currency = payment.getCurrency();
			double amount = payment.getAmount();
			paymentTotals.put(currency, paymentTotals.getOrDefault(currency, 0.0) + amount);
		}
		for (Map.Entry<String, Double> entry : paymentTotals.entrySet()) {
			analysisContainer.addValue(
					getWithIcon("Total " + entry.getKey(), Icon.called("money-bill-wave").of(Color.BLUE)),
					FormatUtils.cutDecimals(entry.getValue())
			);
		}
	}
}

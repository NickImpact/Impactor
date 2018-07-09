package com.nickimpact.impactor.mojang;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.time.Time;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatusCheck {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEEEE MM/dd/yy HH:mm:ss z");
	private static final Gson gson = new Gson();

	private static Map<ServiceType, Status> statuses = Maps.newHashMap();
	private static Date lastCheck = Date.from(Instant.now());

	public static void report() {
		List<Text> report = Lists.newArrayList();
		for(ServiceType st : statuses.keySet()) {
			Status status = statuses.get(st);
			Text out = Text.of(TextColors.DARK_AQUA, st.display, TextColors.GRAY, " \u00bb ");
			out = Text.of(out, status.getStatus().getColor(), status.getStatus().getRep());
			if(!status.getStatus().equals(ServiceStatus.GREEN)) {
				if(status.getStatus().equals(ServiceStatus.RED)) {
					Date now = Date.from(Instant.now());
					Time time = new Time(now.toInstant().minusSeconds(status.down.toInstant().getEpochSecond()).getEpochSecond());
					out = Text.of(out, " Down for \uc2b1", time.asShort());
				}
			}

			report.add(out);
		}

		PaginationList pagination = PaginationList.builder()
				.title(Text.of(TextColors.DARK_AQUA, "Mojang Service Status"))
				.header(Text.of(TextColors.YELLOW, "Time of Check: ", sdf.format(lastCheck)))
				.contents(report)
				.build();

		ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.WARN, Lists.newArrayList(
				Text.of("+=========================================+"),
				Text.of("| ", StringUtils.center("Mojang Service Status", 39), " |"),
				Text.of("+=========================================+"),
				Text.of(TextColors.YELLOW, "Time of Check: ", sdf.format(lastCheck)),
				Text.EMPTY
		));
		ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.WARN, report);
		Sponge.getServer().getOnlinePlayers().stream()
				.filter(pl -> pl.hasPermission("impactorcore.admin.mojang"))
				.forEach(pagination::sendTo);
	}

	public static boolean allGreen() {
		for(Status status : statuses.values()) {
			if(!status.status.equals(ServiceStatus.GREEN)) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static void fetch() throws Exception {
		lastCheck = Date.from(Instant.now());
		List<Map<String, String>> mapping = gson.fromJson(readURL("https://status.mojang.com/check"), new TypeToken<List<Map<String, String>>>() {}.getType());
		mapping.forEach(m -> {
			m.forEach((k, v) -> {
				ServiceType service = ServiceType.from(k);
				Status status = Status.from(service, ServiceStatus.from(v), lastCheck);
				Status prior = statuses.get(service);

				// Handle logic for a special change in status
				if(prior != null && !status.equals(prior)) {
					if(prior.getStatus().equals(ServiceStatus.GREEN)) {
						if(status.getStatus().equals(ServiceStatus.RED)) {
							status.setStatus(ServiceStatus.DOWN);
							status.down = Date.from(Instant.now());
						}
					} else if(prior.getStatus().equals(ServiceStatus.RED)) {
						if(status.getStatus().equals(ServiceStatus.GREEN)) {
							status.setStatus(ServiceStatus.BACK);
							status.down = null;
						} else if(status.getStatus().equals(ServiceStatus.YELLOW)) {
							status.down = null;
						}
					} else if(prior.getStatus().equals(ServiceStatus.YELLOW)) {
						if(status.getStatus().equals(ServiceStatus.RED)) {
							status.down = Date.from(Instant.now());
						}
					} else if(prior.getStatus().equals(ServiceStatus.DOWN)) {
						if(status.getStatus().equals(ServiceStatus.RED)) {
							status.down = prior.getDown();
						}
					} else if(prior.getStatus().equals(ServiceStatus.BACK)) {
						if (status.getStatus().equals(ServiceStatus.GREEN)) {
							status.down = null;
						}
					}
				}
				statuses.put(service, status);
			});
		});
	}

	private static String readURL(String urlString) throws Exception {
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

	/**
	 * This enum represents the possible Mojang API servers availability statuses.
	 */
	public enum ServiceStatus {
		RED("\u2639", TextColors.RED),
		YELLOW("\u26a0", TextColors.YELLOW),
		GREEN("\u263a", TextColors.GREEN),
		UNKNOWN("\u00AF\\\u005F\u0028\u30C4\u0029\u005F\u002F\u00AF", TextColors.GRAY),
		DOWN("(┛ಠ_ಠ)┛彡┻━┻", TextColors.RED),
		BACK("┬─┬ノ( º _ ºノ)", TextColors.GREEN);

		@Getter private String rep;
		@Getter private TextColor color;

		ServiceStatus(String rep, TextColor color){
			Charset.forName("UTF-8").encode(rep);
			this.rep = rep;
			this.color = color;
		}

		public static ServiceStatus from(String status) {
			for(ServiceStatus ss : values()) {
				if(ss.name().equalsIgnoreCase(status)) {
					return ss;
				}
			}

			return UNKNOWN;
		}
	}

	/**
	 * This enum represents the various portions of the Mojang API.
	 */
	public enum ServiceType {
		MINECRAFT_NET("minecraft.net", "minecraft.net"),
		SESSION_MINECRAFT_NET("session.minecraft.net", "Sessions"),
		ACCOUNT_MOJANG_COM("account.mojang.com", "Account Services"),
		AUTHSERVER_MOJANG_COM("authserver.mojang.com", "Auth Server"),
		SESSIONSERVER_MOJANG_COM("sessionserver.mojang.com", "Sessions Server"),
		API_MOJANG_COM("api.mojang.com", "Mojang API"),
		TEXTURES_MINECRAFT_NET("textures.minecraft.net", "Skins"),
		MOJANG_COM("mojang.com", "mojang.com");

		@Getter private String path;
		@Getter private String display;

		ServiceType(String path, String display) {
			this.path = path;
			this.display = display;
		}

		@NonNull
		public static ServiceType from(String name) {
			return Arrays.stream(values()).filter(st -> st.path.equalsIgnoreCase(name)).findAny().get();
		}

		/**
		 * <p>This method overrides {@code java.lang.Object.toString()} and returns the address of the mojang api portion a certain enum constant represents.
		 * <p><strong>Example:</strong>
		 * {@code org.shanerx.mojang.Mojang.ServiceType.MINECRAFT_NET.toString()} will return {@literal minecraft.net}
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return name().toLowerCase().replace("_", ".");
		}
	}

	@Getter
	@Setter
	private static class Status {
		private ServiceType type;
		private ServiceStatus status;
		private Date check;

		/** Marks the time a service went down. When the service revives, this field will be set to null */
		private Date down;

		private Status(ServiceType type, ServiceStatus status, Date check) {
			this.type = type;
			this.status = status;
			this.check = check;
		}

		public static Status from(ServiceType type, ServiceStatus status, Date check) {
			return new Status(type, status, check);
		}
	}
}

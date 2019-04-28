package com.nickimpact.impactor.api.services.mojang;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Status {
	private ServiceType type;
	private ServiceStatus status;
	private Date check;

	/** Marks the time a service went down. When the service revives, this field will be set to null */
	private Date downInstant;

	private Status(ServiceType type, ServiceStatus status, Date check) {
		this.type = type;
		this.status = status;
		this.check = check;
	}

	public static Status from(ServiceType type, ServiceStatus status, Date check) {
		return new Status(type, status, check);
	}
}

package net.impactdev.impactor.api.services.mojang;

import java.util.Date;

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

	public ServiceType getType() {
		return this.type;
	}

	public ServiceStatus getStatus() {
		return this.status;
	}

	public Date getCheck() {
		return this.check;
	}

	public Date getDownInstant() {
		return this.downInstant;
	}

	public void setType(ServiceType type) {
		this.type = type;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public void setCheck(Date check) {
		this.check = check;
	}

	public void setDownInstant(Date downInstant) {
		this.downInstant = downInstant;
	}
}

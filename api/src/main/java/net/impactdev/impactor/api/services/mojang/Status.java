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

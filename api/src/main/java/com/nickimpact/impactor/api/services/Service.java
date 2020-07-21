package com.nickimpact.impactor.api.services;

public interface Service {

	String getServiceName();

	interface RequiresInit extends Service {

		void init();

	}

}

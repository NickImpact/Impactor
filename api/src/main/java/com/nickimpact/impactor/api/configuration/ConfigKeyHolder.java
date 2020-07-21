package com.nickimpact.impactor.api.configuration;

import java.util.Map;

public interface ConfigKeyHolder {

	Map<String, ConfigKey<?>> getKeys();

	int getSize();
}

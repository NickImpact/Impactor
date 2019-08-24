package com.nickimpact.impactor.spigot.utils;

public enum SkullTextures {

	Summer("NWE1YWIwNWVhMjU0YzMyZTNjNDhmM2ZkY2Y5ZmQ5ZDc3ZDNjYmEwNGU2YjVlYzJlNjhiM2NiZGNmYWMzZmQifX19"),
	Winter("ODg0ZTkyNDg3YzY3NDk5OTViNzk3MzdiOGE5ZWI0YzQzOTU0Nzk3YTZkZDZjZDliNGVmY2UxN2NmNDc1ODQ2In19fQ=="),
	Present("ZTgxYTM5OWU0ZDJlOGYxYTgyOGUxYzRiYzRjYTk5ZWZlZDE1MDhmM2Y0MjFkOTg4NzQ3MjlhZTY0ZDgzIn19fQ=="),
	Present_Chest("N2JhM2JlODNlZDQ3NTAyZmIxYTE0MWQzYTc4NWZmOTZmMjg1NmVmMzg2MjIzMzg1NDQwNWRiZjhkYWJlNDI3In19fQ=="),
	Crate("ZjYyNGM5MjdjZmVhMzEzNTU0Mjc5OTNkOGI3OTcxMmU4NmY5NGQ1OTUzNDMzZjg0ODg0OWEzOWE2ODc5In19fQ=="),
	Question("");

	public String value;

	private SkullTextures(String texture) {
		this.value = texture;
	}

}

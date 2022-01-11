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

package net.impactdev.impactor.api.dependencies;

import com.google.common.collect.ImmutableList;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public enum Dependency {

	ASM(
			"org.ow2.asm",
			"asm",
			"7.1",
			"SrL6K20sycyx6qBeoynEB7R7E+0pFfYvjEuMyWJY1N4="
	),
	ASM_COMMONS(
			"org.ow2.asm",
			"asm-commons",
			"7.1",
			"5VkEidjxmE2Fv+q9Oxc3TFnCiuCdSOxKDrvQGVns01g="
	),
	JAR_RELOCATOR(
			"me.lucko",
			"jar-relocator",
			"1.5",
			"0D6eM99gKpEYFNDydgnto3Df0ygZGdRVqy5ahtj0oIs="
	),
	SLF4J_SIMPLE(
			"org.slf4j",
			"slf4j-simple",
			"1.7.30",
			"i5J5y/9rn4hZTvrjzwIDm2mVAw7sAj7UOSh0jEFnD+4="
	),
	SLF4J_API(
			"org.slf4j",
			"slf4j-api",
			"1.7.30",
			"zboHlk0btAoHYUhcax6ML4/Z6x0ZxTkorA1/lRAQXFc="
	),
	H2_DRIVER(
			"com.h2database",
			"h2",
			"1.4.200",
			"OtmsS2qunNnTrBxEdGXh7QYBm4UbiT3WqNdt222FvKY="
			// we don't apply relocations to h2 - it gets loaded via
			// an isolated classloader
	),
	HIKARI(
			"com{}zaxxer",
			"HikariCP",
			"4.0.3",
			"fAJK7/HBBjV210RTUT+d5kR9jmJNF/jifzCi6XaIxsk=",
			Relocation.of("hikari", "com{}zaxxer{}hikari")
	),
	MARIADB_DRIVER(
			"org{}mariadb{}jdbc",
			"mariadb-java-client",
			"2.7.4",
			"8GV3sZ6JszAouWzU8DEkjnYd5+vHEvTvU14mjfdu2yo=",
			Relocation.of("mariadb", "org{}mariadb{}jdbc")
	),
	MYSQL_DRIVER(
			"mysql",
			"mysql-connector-java",
			"8.0.26",
			"Ph3d1f2814pVKo/JFfy4BOT3+DqHM1WXKjyXoiVW9iw=",
			Relocation.of("mysql", "com{}mysql")
	),
	MONGODB_DRIVER(
			"org.mongodb",
			"mongo-java-driver",
			"3.12.2",
			"eMxHcEtasb/ubFCv99kE5rVZMPGmBei674ZTdjYe58w=",
			Relocation.of("mongodb", "com{}mongodb"),
			Relocation.of("bson", "org{}bson")
	),
	CONFIGURATE_YAML(
			"org{}spongepowered",
			"configurate-yaml",
			"3.7",
			"14L0JiDuAfQovxkNySeaf9Kul3Nkl0OaW49Ow4ReV8E=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	SNAKEYAML(
			"org.yaml",
			"snakeyaml",
			"1.23",
			"EwCfte3jzyvlqNDxYCFVrqoM5e9fk2aJK9JY2NPU0rE=",
			Relocation.of("yaml", "org{}yaml{}snakeyaml")
	),
	CONFIGURATE_HOCON(
			"org{}spongepowered",
			"configurate-hocon",
			"3.7",
			"GYdqieCZVgPmoaIFjYN0YHuSVsHO7IsXZrwLAWqCgZM=",
			Relocation.of("configurate", "ninja{}leaping{}configurate"),
			Relocation.of("hocon", "com{}typesafe{}config")
	),
	HOCON_CONFIG(
			"com{}typesafe",
			"config",
			"1.4.0",
			"qtv9WlJFUb7vENP4kdMFuDuyfVRwPZpN56yioS2YR+I=",
			Relocation.of("hocon", "com{}typesafe{}config")
	),
	CONFIGURATE_GSON(
			"org{}spongepowered",
			"configurate-gson",
			"3.7",
			"0JhMGX6mjY8MDCGGc7lrfoHvWbpGiE5R6N3nqJch+SU=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	CONFIGURATE_CORE(
			"org{}spongepowered",
			"configurate-core",
			"3.7",
			"V+M3OFm+O0AHsao557kExxa27lYEX7UYE06G/zC/Kyc=",
			Relocation.of("configurate", "ninja{}leaping{}configurate")
	),
	KYORI_EVENT(
			"net{}kyori",
			"event-api",
			"3.0.0",
			"yjvdTdAyktl3iFEQFLHC3qYwwt7/DbCd7Zc8Q4SlIag=",
			Relocation.of("eventbus", "net{}kyori{}event")
	),
	KYORI_EVENT_METHOD(
			"net{}kyori",
			"event-method",
			"3.0.0",
			"CpQqZvtZv/xqjHoCkL+baUgey33g7ey7czZXfCMtoH0=",
			Relocation.of("eventbus", "net{}kyori{}event")
	),
	KYORI_EVENT_METHOD_ASM(
			"net{}kyori",
			"event-method-asm",
			"3.0.0",
			"GnH7tbzQkrqklOGfK2nKNIoorpYXBHMabUdvoEGH8kk=",
			Relocation.of("eventbus", "net{}kyori{}event")
	),
	BYTEBUDDY(
			"net{}bytebuddy",
			"byte-buddy",
			"1.10.9",
			"B7nKbi+XDLA/SyVlHfHy/OJx1JG0TgQJgniHeG9pLU0=",
			Relocation.of("bytebuddy", "net{}bytebuddy")
	),
	CAFFEINE(
			"com{}github{}ben-manes{}caffeine",
			"caffeine",
			"2.8.4",
			"KV9YN5gQj6b507VJApJpPF5PkCon0DZqAi0T7Ln0lag=",
			Relocation.of("caffeine", "com{}github{}benmanes{}caffeine")
	),
	KYORI_EXAMINATION(
			"net{}kyori",
			"examination-api",
			"1.3.0",
			"ySN//ssFQo9u/4YhYkascM4LR7BMCOp8o1Ag/eV/hJI=",
			Relocation.of("examination", "net{}kyori{}examination")
	),
	KYORI_EXAMINATION_STRING(
			"net{}kyori",
			"examination-string",
			"1.3.0",
			"fQH8JaS7OvDhZiaFRV9FQfv0YmIW6lhG5FXBSR4Va4w=",
			Relocation.of("examination", "net{}kyori{}examination")
	),
	KYORI_TEXT(
			"net{}kyori",
			"adventure-api",
			"4.9.1",
			"E5y9joJQ03VGgVBY9vhJZo7gPzbqMUVD0a+EGQ8IWoY=",
			Relocation.of("adventure", "net{}kyori{}adventure"),
			Relocation.of("examination", "net{}kyori{}examination")
	),
	KYORI_TEXT_SERIALIZER_GSON(
			"net{}kyori",
			"adventure-text-serializer-gson",
			"4.9.1",
			"nHEvnOtSXRzE/JgUV/A01TuG2U0NKzsJ0UbEvltOir0=",
			Relocation.of("adventure", "net{}kyori{}adventure")
	),
	KYORI_TEXT_SERIALIZER_LEGACY(
			"net{}kyori",
			"adventure-text-serializer-legacy",
			"4.9.1",
			"Ko8v50bJEMco8e+zilSJt/PpnAY9+7F+vlUlJPfjSbY=",
			Relocation.of("adventure", "net{}kyori{}adventure")
	),
	CLASSGRAPH(
			"io{}github{}classgraph",
			"classgraph",
			"4.8.90",
			"jDcc94HXvxxvPjhxWgyJyrOH2UDwf41O65DKpmWyrLA=",
			Relocation.of("classgraph", "io{}github{}classgraph")
	),
	FAST_CLASSPATH_SCANNER(
			"io{}github{}lukehutch",
			"fast-classpath-scanner",
			"3.1.15",
			"WF0gAn57pvqRa+nr0N+PamO9L6UU9qjwjpLDYk5IZK8=",
			Relocation.of("fastclasspathscanner", "io{}github{}lukehutch{}fastclasspathscanner")
	),
	FLOW_MATH(
			"com{}flowpowered",
			"flow-math",
			"1.0.3",
			"3qIBAx92YOvMdgQTZdeNdLm/s2+uxw0+oNz8UD09xnQ=",
			Relocation.of("flowmath", "com{}flowpowered{}math")
	),
	OBJECT_WEB(
			"org{}ow2{}asm",
			"asm",
			"8.0.1",
			"yluNEVaeU5IbDjSGRp58Z0Nhx5hF2tPVFPOKtuDIwQo=",
			Relocation.of("ow2", "org{}objectweb")
	),
	MXPARSER(
			"org.mariuszgromada.math",
			"MathParser.org-mXparser",
			"4.4.2",
			"z+nZN08mJQ8UniReVzNorIApq3QhAUws6ZtNrtWR8dA=",
			Relocation.of("mxparser", "org{}mariuszgromada{}math{}mxparser")
	),
	JEDIS(
			"redis.clients",
			"jedis",
			"3.3.0",
			"HuTfz9xW/mi1fwVQ3xgPmd6qwTRMF/3fyMzw2LmOgy4=",
			Relocation.of("jedis", "redis{}clients{}jedis"),
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
	),
	COMMONS_POOL_2(
			"org.apache.commons",
			"commons-pool2",
			"2.8.0",
			"Xvqfu1SlixoSIFpfrFZfaYKr/rD/Rb28MYdI71/To/8=",
			Relocation.of("commonspool2", "org{}apache{}commons{}pool2")
	),
	;

	private final String mavenPath;
	private final String group;
	private final String artifact;
	private final String version;
	private final byte[] checksum;
	private final boolean snapshot;
	private final List<Relocation> relocations;

	private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

	Dependency(String groupID, String artifactID, String version, String checksum, Relocation... relocations) {
		this.group = groupID;
		this.artifact = artifactID;
		this.version = version;
		this.snapshot = this.version.contains("-SNAPSHOT");
		this.checksum = !this.snapshot ? Base64.getDecoder().decode(checksum) : new byte[0];
		this.relocations = ImmutableList.copyOf(relocations);

		this.mavenPath = String.format(MAVEN_FORMAT,
				rewriteEscaping(groupID).replace(".", "/"),
				rewriteEscaping(artifactID),
				version,
				rewriteEscaping(artifactID),
				this.version
		);
	}

	Dependency(String group, String artifact, String version, String timestamp, String checksum, Relocation... relocations) {
		this.group = group;
		this.artifact = artifact;
		this.version = version;
		this.snapshot = this.version.contains("-SNAPSHOT");
		this.checksum = !this.snapshot ? Base64.getDecoder().decode(checksum) : new byte[0];
		this.relocations = ImmutableList.copyOf(relocations);

		this.mavenPath = String.format(MAVEN_FORMAT,
				rewriteEscaping(group).replace(".", "/"),
				rewriteEscaping(artifact),
				version,
				rewriteEscaping(artifact),
				this.version + "-" + timestamp
		);
	}

	public String getGroup() {
		return group.replaceAll("[{][}]", ".");
	}

	public String getArtifact() {
		return artifact;
	}

	private static String rewriteEscaping(String s) {
		return s.replace("{}", ".");
	}

	public String getFileName() {
		return name().toLowerCase().replace("_", "-") + "-" + this.version;
	}

	String getMavenPath() {
		return this.mavenPath;
	}

	public String getVersion() {
		return this.version;
	}

	public boolean isSnapshot() {
		return this.snapshot;
	}

	public byte[] getChecksum() {
		return this.checksum;
	}

	public boolean checksumMatches(byte[] hash) {
		return Arrays.equals(this.checksum, hash);
	}

	public List<Relocation> getRelocations() {
		return this.relocations;
	}

	/**
	 * Creates a {@link MessageDigest} suitable for computing the checksums
	 * of dependencies.
	 *
	 * @return the digest
	 */
	public static MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}

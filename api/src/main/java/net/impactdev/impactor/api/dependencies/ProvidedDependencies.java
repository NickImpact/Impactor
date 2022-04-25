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

import net.impactdev.impactor.api.dependencies.relocation.Relocation;

public final class ProvidedDependencies {

    // ASM
    public static final Dependency ASM = Dependency.builder()
            .name("ASM")
            .group("org{}ow2{}asm")
            .artifact("asm")
            .version("9.2")
            .checksum("udT+TXGTjfOIOfDspCqqpkz4sxPWeNoDbwyzyhmbR/U=")
            .build();
    public static final Dependency ASM_COMMONS = Dependency.builder()
            .name("ASM Commons")
            .group("org{}ow2{}asm")
            .artifact("asm-commons")
            .version("9.2")
            .checksum("vkzlMTiiOLtSLNeBz5Hzulzi9sqT7GLUahYqEnIl4KY=")
            .with(ASM)
            .build();

    // Lucko Jar Relocator - Used for Relocations
    public static final Dependency JAR_RELOCATOR = Dependency.builder()
            .name("Jar Relocator")
            .group("me.lucko")
            .artifact("jar-relocator")
            .version("1.5")
            .checksum("0D6eM99gKpEYFNDydgnto3Df0ygZGdRVqy5ahtj0oIs=")
            .with(ASM, ASM_COMMONS)
            .build();

    // Kyori Products
    public static final Dependency KYORI_EXAMINATION = Dependency.builder()
            .name("Kyori Examination")
            .group("net{}kyori")
            .artifact("examination-api")
            .version("1.3.0")
            .checksum("ySN//ssFQo9u/4YhYkascM4LR7BMCOp8o1Ag/eV/hJI=")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .build();
    public static final Dependency KYORI_EXAMINATION_STRINGS = Dependency.builder()
            .name("Kyori Examination Strings")
            .group("net{}kyori")
            .artifact("examination-string")
            .version("1.3.0")
            .checksum("fQH8JaS7OvDhZiaFRV9FQfv0YmIW6lhG5FXBSR4Va4w=")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(KYORI_EXAMINATION)
            .build();
    public static final Dependency KYORI_KEYS = Dependency.builder()
            .name("Kyori - Keys")
            .group("net{}kyori")
            .artifact("adventure-key")
            .version("4.10.1")
            .checksum("n63jKNnvcuSm4+oNbU0dm5QT5nUHEH1PKLMCPLoKeuI=")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(KYORI_EXAMINATION, KYORI_EXAMINATION_STRINGS)
            .build();
    public static final Dependency ADVENTURE = Dependency.builder()
            .name("Adventure")
            .group("net{}kyori")
            .artifact("adventure-api")
            .version("4.10.1")
            .checksum("N6scpKvvKMGUhKIYojadJLbsnA4zgESeO2+WPGtGjcg=")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(KYORI_EXAMINATION, KYORI_EXAMINATION_STRINGS)
            .build();
    public static final Dependency ADVENTURE_LEGACY_SERIALIZER = Dependency.builder()
            .name("Kyori - Adventure Legacy Serializer")
            .group("net{}kyori")
            .artifact("adventure-text-serializer-legacy")
            .version("4.10.1")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(ADVENTURE)
            .build();
    public static final Dependency ADVENTURE_GSON_SERIALIZER = Dependency.builder()
            .name("Kyori - Adventure GSON Serializer")
            .group("net{}kyori")
            .artifact("adventure-text-serializer-gson")
            .version("4.10.1")
            .checksum("NYm9teyDMtXqoMel3gWMapmiG3+Jhudwj9BlKAb1siI=")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(ADVENTURE)
            .build();
    public static final Dependency ADVENTURE_MINIMESSAGE = Dependency.builder()
            .name("Kyori - Adventure MiniMessage")
            .group("net{}kyori")
            .artifact("adventure-text-minimessage")
            .version("4.10.1")
            .relocation(Relocation.of("net{}kyori", "kyori"))
            .with(ADVENTURE, KYORI_KEYS)
            .build();

    public static final Dependency KYORI_EVENT_API = Dependency.builder()
            .name("Kyori Event API")
            .group("net{}kyori")
            .artifact("event-api")
            .version("3.0.0")
            .checksum("yjvdTdAyktl3iFEQFLHC3qYwwt7/DbCd7Zc8Q4SlIag=")
            .relocation(Relocation.of("net{}kyori{}event", "eventbus"))
            .build();
    public static final Dependency KYORI_EVENT_METHOD = Dependency.builder()
            .name("Kyori Event Method")
            .group("net{}kyori")
            .artifact("event-method")
            .version("3.0.0")
            .checksum("CpQqZvtZv/xqjHoCkL+baUgey33g7ey7czZXfCMtoH0=")
            .relocation(Relocation.of("net{}kyori{}event", "eventbus"))
            .build();
    public static final Dependency KYORI_EVENT_METHOD_ASM = Dependency.builder()
            .name("Kyori Event Method ASM")
            .group("net{}kyori")
            .artifact("event-method-asm")
            .version("3.0.0")
            .checksum("GnH7tbzQkrqklOGfK2nKNIoorpYXBHMabUdvoEGH8kk=")
            .relocation(Relocation.of("net{}kyori{}event", "eventbus"))
            .build();


    // Configurate
    public static final Dependency CONFIGURATE_CORE = Dependency.builder()
            .name("Configurate Core")
            .group("org{}spongepowered")
            .artifact("configurate-core")
            .version("4.1.2")
            .relocation(Relocation.of("org{}spongepowered{}configurate", "configurate"))
            .build();
    public static final Dependency CONFIGURATE_YAML = Dependency.builder()
            .name("Configurate YAML")
            .group("org{}spongepowered")
            .artifact("configurate-yaml")
            .version("4.1.2")
            .relocation(Relocation.of("org.spongepowered.configurate", "configurate"))
            .with(CONFIGURATE_CORE)
            .build();
    public static final Dependency TYPESAFE_CONFIG = Dependency.builder()
            .name("Typesafe Config")
            .group("com.typesafe")
            .artifact("config")
            .version("1.4.0")
            .checksum("qtv9WlJFUb7vENP4kdMFuDuyfVRwPZpN56yioS2YR+I=")
            .relocation(Relocation.of("com.typesafe.config", "hocon"))
            .build();
    public static final Dependency CONFIGURATE_HOCON = Dependency.builder()
            .name("Configurate HOCON")
            .group("org{}spongepowered")
            .artifact("configurate-hocon")
            .version("4.1.2")
            .relocation(Relocation.of("org.spongepowered.configurate", "configurate"))
            .with(CONFIGURATE_CORE, TYPESAFE_CONFIG)
            .build();
    public static final Dependency CONFIGURATE_GSON = Dependency.builder()
            .name("Configurate GSON")
            .group("org{}spongepowered")
            .artifact("configurate-gson")
            .version("4.1.2")
            .relocation(Relocation.of("org.spongepowered.configurate", "configurate"))
            .with(CONFIGURATE_CORE)
            .build();

    // Storage Options
    public static final Dependency HIKARI = Dependency.builder()
            .name("Hikari")
            .group("com{}zaxxer")
            .artifact("HikariCP")
            .version("3.4.5")
            .checksum("i3MvlHBXDUqEHcHvbIJrWGl4sluoMHEv8fpZ3idd+mE=")
            .relocation(Relocation.of("com.zaxxer.hikari", "hikari"))
            .build();
    public static final Dependency H2 = Dependency.builder()
            .name("H2")
            .group("com.h2database")
            .artifact("h2")
            .version("1.4.199")
            .checksum("MSWhZ0O8a0z7thq7p4MgPx+2gjCqD9yXiY95b5ml1C4=")
            .build();
    public static final Dependency MARIADB = Dependency.builder()
            .name("MariaDB")
            .group("org{}mariadb{}jdbc")
            .artifact("mariadb-java-client")
            .version("2.6.0")
            .checksum("fgiCp29Z7X38ULAJNsxZ1wFIVT2u3trSx/VCMxTlA6g=")
            .relocation(Relocation.of("org{}mariadb{}jdbc", "mariadb"))
            .build();
    public static final Dependency MYSQL = Dependency.builder()
            .name("MySQL")
            .group("mysql")
            .artifact("mysql-connector-java")
            .version("5.1.48")
            .checksum("VuJsqqOCH1rkr0T5x09mz4uE6gFRatOAPLsOkEm27Kg=")
            .relocation(Relocation.of("com{}mysql", "mysql"))
            .build();
    public static final Dependency MONGODB = Dependency.builder()
            .name("MongoDB")
            .group("org.mongodb")
            .artifact("mongo-java-driver")
            .version("3.12.2")
            .checksum("eMxHcEtasb/ubFCv99kE5rVZMPGmBei674ZTdjYe58w=")
            .relocation(Relocation.of("com.mongodb", "mongodb"))
            .relocation(Relocation.of("org.bson", "bson"))
            .build();

    // Misc Provisions
    public static final Dependency BYTEBUDDY = Dependency.builder()
            .name("ByteBuddy")
            .group("net{}bytebuddy")
            .artifact("byte-buddy")
            .version("1.10.9")
            .checksum("B7nKbi+XDLA/SyVlHfHy/OJx1JG0TgQJgniHeG9pLU0=")
            .relocation(Relocation.of("net{}bytebuddy", "bytebuddy"))
            .build();
    public static final Dependency FLOW_MATH = Dependency.builder()
            .name("Flowpowered Math")
            .group("com{}flowpowered")
            .artifact("flow-math")
            .version("1.0.3")
            .checksum("3qIBAx92YOvMdgQTZdeNdLm/s2+uxw0+oNz8UD09xnQ=")
            .relocation(Relocation.of("com.flowpowered.math", "flowmath"))
            .build();
    public static final Dependency SPONGE_MATH = Dependency.builder()
            .name("SpongePowered Math")
            .group("org.spongepowered")
            .artifact("math")
            .version("2.0.1")
            .checksum("T5SBxcKJtCF1dzARPWlVQn+1qqqGdYA86EPhMiuhnUM=")
            .build();
    public static final Dependency SLF4J_API = Dependency.builder()
            .name("SLF4J API")
            .group("org.slf4j")
            .artifact("slf4j-api")
            .version("1.7.30")
            .checksum("zboHlk0btAoHYUhcax6ML4/Z6x0ZxTkorA1/lRAQXFc=")
            .build();
    public static final Dependency SLF4J_SIMPLE = Dependency.builder()
            .name("SLF4J Simple")
            .group("org.slf4j")
            .artifact("slf4j-simple")
            .version("1.7.30")
            .checksum("i5J5y/9rn4hZTvrjzwIDm2mVAw7sAj7UOSh0jEFnD+4=")
            .with(SLF4J_API)
            .build();
    public static final Dependency REFLECTIONS = Dependency.builder()
            .name("Reflections")
            .group("org.reflections")
            .artifact("reflections")
            .version("0.10.2")
            .checksum("k4otCP5UBQ12ELlE2N3DoJNVcQ2ea+CqyDjbwE6aKCU=")
            .with(Dependency.builder()
                    .name("Java Assist")
                    .group("org.javassist")
                    .artifact("javassist")
                    .version("3.28.0-GA")
                    .checksum("V9Cp6ShvgvTqqFESUYaZf4Eb784OIGD/ChWnf1qd2ac=")
                    .build()
            )
            .build();
    public static final Dependency CAFFEINE = Dependency.builder()
            .name("Caffeine")
            .group("com{}github{}ben-manes{}caffeine")
            .artifact("caffeine")
            .version("2.8.4")
            .checksum("KV9YN5gQj6b507VJApJpPF5PkCon0DZqAi0T7Ln0lag=")
            .relocation(Relocation.of("com{}github{}benmanes{}caffeine", "caffeine"))
            .build();
}

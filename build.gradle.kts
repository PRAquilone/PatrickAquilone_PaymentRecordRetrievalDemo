plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.payment.pra.coding.challenge"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.5")
	implementation("org.apache.logging.log4j:log4j-core:2.24.1")
	implementation("commons-io:commons-io:2.17.0")
	implementation("org.apache.commons:commons-lang3:3.17.0")
	implementation("com.google.code.gson:gson:2.12.1")

	compileOnly("org.projectlombok:lombok:1.18.34")

	annotationProcessor("org.projectlombok:lombok:1.18.34")

	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
	testImplementation("org.junit.vintage:junit-vintage-engine:5.11.3")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
	testImplementation("io.cucumber:cucumber-core:7.20.1")
	testImplementation("io.cucumber:cucumber-java:7.20.1")
	testImplementation("io.cucumber:cucumber-junit:7.20.1")
	testImplementation("io.cucumber:cucumber-spring:7.20.1")
	testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")

	testCompileOnly("org.projectlombok:lombok:1.18.34")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testAnnotationProcessor("org.projectlombok:lombok:1.18.34")


}

tasks.withType<Test> {
	useJUnitPlatform()
}

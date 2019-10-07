plugins {
    java
    kotlin("jvm") version "1.3.41"
}

repositories { mavenCentral() }

dependencies {
    implementation("org.protelis:protelis:${extra["protelisVersion"].toString()}")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:${extra["paho_version"].toString()}")
    implementation("com.google.code.gson:gson:${extra["gsonVersion"].toString()}")
}
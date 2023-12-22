import org.jetbrains.kotlin.cli.jvm.main

plugins {
    kotlin("jvm") version "1.9.20"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

task<JavaExec>("runExample") {
    mainClass = "Day22Kt"
    classpath = sourceSets.main.get().runtimeClasspath
}

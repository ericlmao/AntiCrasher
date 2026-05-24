import gradle.kotlin.dsl.accessors._983bb327668533c52660ac523168b406.annotationProcessor
import gradle.kotlin.dsl.accessors._983bb327668533c52660ac523168b406.compileOnly
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.SourceSetContainer

plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

configurations {
    create("zipConfig")
}

val libs = the<LibrariesForLibs>()

repositories {
    mavenCentral()
    maven {
        name = "fabricmc"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "codemc-snapshots"
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }
    maven {
        name = "codemc-releases"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
    maven {
        name = "clojars"
        url = uri("https://repo.clojars.org/")
    }
    maven {
        name = "alessiodp-snapshots"
        url = uri("https://repo.alessiodp.com/snapshots/")
    }
    maven {
        name = "Sonatype Snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz/")
    }
}

dependencies {
    compileOnly(libs.packetevents.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
        options.fork()
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}

// god, I hate this, but it's the only way to get Fabric to cooperate w/ dependencies
tasks.jar {
    val commonProject = project.findProject(":common")
    val apiProject = project.findProject(":api")
    val commonSourceSets = commonProject?.extensions?.findByType(SourceSetContainer::class.java)
    val apiSourceSets = apiProject?.extensions?.findByType(SourceSetContainer::class.java)

    if (commonProject != null) {
        dependsOn(":common:jar")
        inputs.files(files(commonProject.layout.buildDirectory.file("libs/${commonProject.name}-${commonProject.version}.jar")).builtBy(":common:jar"))
    }
    if (apiProject != null) {
        dependsOn(":api:jar")
        inputs.files(files(apiProject.layout.buildDirectory.file("libs/${apiProject.name}-${apiProject.version}.jar")).builtBy(":api:jar"))
    }

    if (commonProject != null && apiProject != null && commonSourceSets != null && apiSourceSets != null) {
        dependsOn(":common:classes", ":api:classes")
        from(commonSourceSets["main"].output)
        from(apiSourceSets["main"].output)

        mustRunAfter(":common:jar", ":api:jar")
    }

    from({
        configurations.runtimeClasspath.get().filter { it.name.contains("libby") }.map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }

    from({
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations.getByName("zipConfig").map { if (it.isDirectory) it else zipTree(it) }
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "javax/**", "jakarta/**", "io/**", "com/**", "jetty-dir.css", "org/**")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

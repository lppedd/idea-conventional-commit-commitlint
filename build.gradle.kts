import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.4.17"
  kotlin("jvm") version "1.4-M1"
}

group = "com.github.lppedd"
version = "0.1.0"

repositories {
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  mavenCentral()
}

intellij {
  version = "IU-2019.2"
  downloadSources = true
  pluginName = "idea-conventional-commit-commitlint"
  setPlugins("com.github.lppedd.idea-conventional-commit:0.8.0")
}

tasks {
  val kotlinOptions: KotlinCompile.() -> Unit = {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf(
      "-Xno-param-assertions",
      "-Xjvm-default=enable"
    )
  }

  compileKotlin(kotlinOptions)
  compileTestKotlin(kotlinOptions)

  patchPluginXml {
    version(project.version)
    sinceBuild("192")
    untilBuild("201.*")
    pluginDescription(File("plugin-description.html").readText(Charsets.UTF_8))
    changeNotes(File("change-notes/${version.replace('.', '_')}.html").readText(Charsets.UTF_8))
  }
}

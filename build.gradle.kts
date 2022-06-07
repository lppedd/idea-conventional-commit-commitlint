@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "1.6.0"
  kotlin("jvm") version "1.7.0-RC2"
}

group = "com.github.lppedd"
version = "0.1.2"

repositories {
  mavenCentral()
}

intellij {
  type.set("IU")
  version.set("2019.2")
  downloadSources.set(true)
  pluginName.set("idea-conventional-commit-commitlint")
  plugins.set(listOf("com.github.lppedd.idea-conventional-commit:0.15.3"))
}

dependencies {
  compileOnly(kotlin("stdlib-jdk8", "1.7.0-RC2"))
}

tasks {
  val kotlinOptions: KotlinCompile.() -> Unit = {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.freeCompilerArgs += listOf(
      "-Xno-param-assertions",
      "-Xjvm-default=enable"
    )
  }

  compileKotlin(kotlinOptions)
  compileTestKotlin(kotlinOptions)

  patchPluginXml {
    version.set(project.version.toString())
    sinceBuild.set("192")
    untilBuild.set(null as String?)

    val projectPath = projectDir.path
    pluginDescription.set((File("$projectPath/plugin-description.html").readText(Charsets.UTF_8)))
    changeNotes.set((File("$projectPath/change-notes/${version.get().replace('.', '_')}.html").readText(Charsets.UTF_8)))
  }
}

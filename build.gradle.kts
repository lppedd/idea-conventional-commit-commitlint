@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
  id("org.jetbrains.intellij") version "1.6.0"
  kotlin("jvm") version "1.7.0-RC2"
}

group = "com.github.lppedd"

repositories {
  mavenCentral()
}

intellij {
  type.set(properties("platformType"))
  version.set(properties("platformVersion"))
  downloadSources.set(true)
  pluginName.set("idea-conventional-commit-commitlint")
  plugins.set(listOf("com.github.lppedd.idea-conventional-commit:0.21.0"))
}

dependencies {
  compileOnly(kotlin("stdlib-jdk8", "1.7.0-RC2"))
}

tasks {
  val kotlinOptions: KotlinCompile.() -> Unit = {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.apiVersion = "1.5"
    kotlinOptions.freeCompilerArgs += listOf(
      "-Xno-param-assertions",
      "-Xjvm-default=enable"
    )
  }

  compileKotlin(kotlinOptions)
  compileTestKotlin(kotlinOptions)

  patchPluginXml {
    version.set(project.version.toString())
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    val projectPath = projectDir.path
    pluginDescription.set((File("$projectPath/plugin-description.html").readText(Charsets.UTF_8)))
    changeNotes.set((File("$projectPath/change-notes/${version.get().replace('.', '_')}.html").readText(Charsets.UTF_8)))
  }
}

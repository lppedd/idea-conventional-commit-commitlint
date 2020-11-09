package com.github.lppedd.cc.commitlint

import com.github.lppedd.cc.api.*
import com.github.lppedd.cc.api.CommitTokenElement.CommitTokenRendering
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Edoardo Luppi
 */
private class CommitlintTokensProvider(private val project: Project) :
    CommitTypeProvider,
    CommitScopeProvider {
  override fun getId(): String =
    PROVIDER_ID

  override fun getPresentation(): ProviderPresentation =
    PROVIDER_PRESENTATION

  override fun getCommitTypes(prefix: String?): Collection<CommitType> =
    readRuleValuesFromConfigFile("type-enum")
      .map(::CommitlintCommitType)
      .toList()

  override fun getCommitScopes(commitType: String?): Collection<CommitScope> =
    readRuleValuesFromConfigFile("scope-enum")
      .map(::CommitlintCommitScope)
      .toList()

  private fun readRuleValuesFromConfigFile(ruleName: String): Sequence<String> {
    val configFilePath = getConfigFilePath() ?: return emptySequence()
    return Files.newBufferedReader(Paths.get(configFilePath), UTF_8).use {
      val jsonRoot = noException { JSONObject(JSONTokener(it)) }
      val jsonRule = jsonRoot
        ?.optJSONObject("rules")
        ?.optJSONArray(ruleName)
      getRuleValues(jsonRule ?: return emptySequence())
    }
  }

  private fun getRuleValues(jsonRuleConfig: JSONArray): Sequence<String> {
    // We check if the rule is disabled, or if it's inverted,
    // which means the rule's values are not valid
    return if (jsonRuleConfig.optNumber(0, 0) == 0 ||
               jsonRuleConfig.optString(1, "never") == "never") {
      emptySequence()
    } else {
      val values = jsonRuleConfig.optJSONArray(2) ?: JSONArray()
      values.asSequence().filterIsInstance<String>()
    }
  }

  private fun getConfigFilePath(): String? =
    project.guessProjectDir()
      ?.findChild(CONFIG_FILE_NAME)
      ?.path

  private inline fun <T> noException(block: () -> T): T? =
    try {
      block()
    } catch (ignored: Exception) {
      null
    }
}

private val TOKEN_RENDERING = CommitTokenRendering(icon = ICON_COMMITLINT)

private class CommitlintCommitType(text: String) : CommitType(text, "") {
  override fun getRendering() = TOKEN_RENDERING
}

private class CommitlintCommitScope(text: String) : CommitScope(text, "") {
  override fun getRendering() = TOKEN_RENDERING
}

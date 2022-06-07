package com.github.lppedd.cc.commitlint

import com.github.lppedd.cc.api.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.Icon

/**
 * @author Edoardo Luppi
 */
@Suppress("UnstableApiUsage")
internal class CommitlintTokensProvider(private val project: Project) :
    CommitTypeProvider,
    CommitScopeProvider {
  companion object {
    const val ID: String = "37415b03-9388-4c55-b949-8c4526f6934d"
  }

  override fun getId(): String =
    ID

  override fun getPresentation(): ProviderPresentation =
    CommitlintProviderPresentation

  override fun getCommitTypes(prefix: String): Collection<CommitType> =
    readRuleValuesFromConfigFile("type-enum")
      .map(::CommitlintCommitToken)
      .toList()

  override fun getCommitScopes(commitType: String): Collection<CommitScope> =
    readRuleValuesFromConfigFile("scope-enum")
      .map(::CommitlintCommitToken)
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
      ?.findChild(CommitlintConstants.ConfigFileName)
      ?.path

  private inline fun <T> noException(block: () -> T): T? =
    try {
      block()
    } catch (ignored: Exception) {
      null
    }

  private object CommitlintProviderPresentation : ProviderPresentation {
    override fun getName(): String =
      "Commitlint"

    override fun getIcon(): Icon =
      CommitlintIcons.Logo
  }

  private object CommitlintTokenPresentation : TokenPresentation {
    override fun getIcon(): Icon =
      CommitlintIcons.Logo
  }

  private class CommitlintCommitToken(private val text: String) : CommitType, CommitScope {
    override fun getText(): String =
      text

    override fun getValue(): String =
      getText()

    override fun getDescription(): String =
      ""

    override fun getPresentation(): TokenPresentation =
      CommitlintTokenPresentation
  }
}

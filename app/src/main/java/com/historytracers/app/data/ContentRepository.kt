// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import com.google.gson.Gson
import com.historytracers.common.*

class ContentRepository(private val context: Context) {
    private val gson = Gson()
    private val primarySourceMap = mutableMapOf<String, HTSourceElement>()
    private val refSourceMap = mutableMapOf<String, HTSourceElement>()
    private val holyRefSourceMap = mutableMapOf<String, HTSourceElement>()
    private val smSourceMap = mutableMapOf<String, HTSourceElement>()

    fun loadJson(name: String): String? {
        return try {
            context.assets.open("lang/$name.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    fun loadSourceFile(uuid: String): HTSourceFile? {
        val json = loadJson("sources/$uuid") ?: return null
        return try {
            gson.fromJson(json, HTSourceFile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun loadAndParse(name: String): ContentResult? {
        val json = loadJson(name) ?: return ContentResult.Error("File not found: $name")
        return try {
            parseContent(json)
        } catch (e: Exception) {
            ContentResult.Error("Parse error: ${e.message}")
        }
    }

    private fun parseContent(json: String): ContentResult {
        val obj = gson.fromJson(json, Map::class.java) as Map<*, *>
        val type = ContentType.from(obj["type"] as? String)

        return when (type) {
            ContentType.INDEX -> ContentResult.IndexClass(gson.fromJson(json, ClassIdx::class.java))
            ContentType.CLASS -> ContentResult.ClassContent(gson.fromJson(json, ClassTemplateFile::class.java))
            ContentType.ATLAS -> ContentResult.AtlasContent(gson.fromJson(json, AtlasTemplateFile::class.java))
            ContentType.FAMILY_TREE -> ContentResult.FamilyTree(gson.fromJson(json, Family::class.java))
            ContentType.SM_GAME -> ContentResult.SMGame(gson.fromJson(json, SMGameFile::class.java))
            ContentType.UNKNOWN -> {
                when {
                    json.contains("primary_sources") -> ContentResult.Sources(gson.fromJson(json, HTSourceFile::class.java))
                    json.contains("keywords") -> ContentResult.Keywords(gson.fromJson(json, HTKeywordsFormat::class.java))
                    json.contains("math_keywords") -> ContentResult.MathKeywords(gson.fromJson(json, HTMathKeywordsFormat::class.java))
                    json.contains("families") -> ContentResult.FamilyTree(gson.fromJson(json, Family::class.java))
                    else -> ContentResult.Error("Unknown content type")
                }
            }
        }
    }

    fun loadSources(contentSources: List<String>?) {
        contentSources?.forEach { uuid ->
            val file = loadSourceFile(uuid) ?: return@forEach
            file.primary_sources?.forEach { primarySourceMap[it.id] = it }
            file.reference_sources?.forEach { refSourceMap[it.id] = it }
            file.religious_sources?.forEach { holyRefSourceMap[it.id] = it }
            file.social_media_sources?.forEach { smSourceMap[it.id] = it }
        }
    }

    fun getSource(uuid: String): HTSourceElement? =
        primarySourceMap[uuid] ?: refSourceMap[uuid] ?: holyRefSourceMap[uuid] ?: smSourceMap[uuid]
}

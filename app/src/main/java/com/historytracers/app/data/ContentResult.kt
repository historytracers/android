// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import com.historytracers.common.*

sealed class ContentResult {
    data class IndexClass(val data: ClassIdx) : ContentResult()
    data class ClassContent(val data: ClassTemplateFile) : ContentResult()
    data class AtlasContent(val data: AtlasTemplateFile) : ContentResult()
    data class FamilyTree(val data: Family) : ContentResult()
    data class SMGame(val data: SMGameFile) : ContentResult()
    data class Sources(val data: HTSourceFile) : ContentResult()
    data class Keywords(val data: HTKeywordsFormat) : ContentResult()
    data class MathKeywords(val data: HTMathKeywordsFormat) : ContentResult()
    data class Error(val message: String) : ContentResult()
}

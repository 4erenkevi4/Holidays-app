package com.elinext.holidays.di.engine.models

import com.elinext.holidays.di.engine.models.ArticleDSL
import com.elinext.holidays.di.engine.models.PageBlock

@ArticleDSL
class PageBlockBuilder {

    var content = ""
    var type = "UNDEFINED"
    var innerBlock: PageBlock? = null

    fun build(): PageBlock = PageBlock(content, type, innerBlock)
}
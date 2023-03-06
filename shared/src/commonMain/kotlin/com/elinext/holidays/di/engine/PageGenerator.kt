package com.elinext.holidays.di.engine

import com.elinext.holidays.di.engine.models.*

class PageGenerator {
    private fun generatePage(): Page = page {
        number = 1
        pageBlocks {
            headerBlock("This is article header")
            textBlock("This is article content")
            compositeBlock(imageBlock(url = "imageUrl"))
        }
    }
}

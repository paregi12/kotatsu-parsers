package org.koitharu.kotatsu.parsers

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.SortOrder
import org.koitharu.kotatsu.parsers.model.search.MangaSearchQuery
import kotlin.time.Duration.Companion.minutes

internal class MangaParkReproductionTest {

    private val context = MangaLoaderContextMock
    private val timeout = 2.minutes
    private val source = MangaParserSource.MANGAPARK

    @Test
    fun list() = runTest(timeout = timeout) {
        val parser = context.newParserInstance(source)
        val list = parser.getList(MangaSearchQuery.Builder().build())
        
        assert(list.isNotEmpty()) { "Manga list is empty" }
        assert(list.all { it.source == source })
        
        // Also check if we can get details for one of them
        val manga = list.first()
        println("Checking details for: ${manga.title}")
        val details = parser.getDetails(manga)
        val chapters = details.chapters
        assert(chapters?.isNotEmpty() == true) { "Chapters are empty for ${manga.title}" }

        // Test fetching pages for the first chapter
        val firstChapter = chapters!!.first()
        println("Checking pages for chapter: ${firstChapter.title}")
        val pages = parser.getPages(firstChapter)
        assert(pages.isNotEmpty()) { "Pages are empty for chapter ${firstChapter.title}" }
        
        // Test fetching image URL for the first page
        val firstPage = pages.first()
        val imageUrl = parser.getPageUrl(firstPage)
        assert(imageUrl.isNotEmpty()) { "Image URL is empty for the first page" }
        println("Successfully fetched image URL: $imageUrl")
    }
}

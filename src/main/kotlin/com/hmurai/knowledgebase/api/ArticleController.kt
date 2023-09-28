package com.hmurai.knowledgebase.api

import com.hmurai.knowledgebase.domain.Article
import com.hmurai.knowledgebase.domain.ArticleQueryAttribute
import com.hmurai.knowledgebase.service.ArticleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/article-base", produces = [MediaType.APPLICATION_JSON_VALUE])
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping
    fun getKnowledgeArticles(
        @RequestParam allParams: Map<String, String>,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "20") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "lastUpdatedAt") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): Page<Article> {
        val excludedRequestParams =
            arrayOf(
                "page",
                "size",
                "sortBy",
                "sortDirection"
            )
        val attributes = allParams.filterKeys { key -> key !in excludedRequestParams }
            .mapKeys { ArticleQueryAttribute.getAttributeFromParamName(it.key) }.toMutableMap()

        return articleService.search(attributes, size, page, sortBy, sortDirection)
    }

    @PostMapping
    fun saveKnowledgeArticle(@RequestBody article: Article): Article? {
        return articleService.saveKnowledgeArticle(article)
    }

    @PutMapping("/{articleId}")
    fun updateKnowledgeArticle(
        @PathVariable articleId: String,
        @RequestBody article: Article
    ): Article? {
        return articleService.saveKnowledgeArticle(article)
    }

    @DeleteMapping("/{articleId}")
    fun deleteKnowledgeArticleById(@PathVariable articleId: String) {
        return articleService.deleteKnowledgeArticle(articleId)
    }
}

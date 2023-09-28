package com.hmurai.knowledgebase.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hmurai.knowledgebase.domain.Article
import com.hmurai.knowledgebase.domain.ArticleQueryAttribute
import com.hmurai.knowledgebase.exceptions.BadRequestException
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.Operator
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortOrder
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArticleService(
    private val restHighLevelClient: RestHighLevelClient
) {
    private val indexName = "articles"
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val objectMapper = jacksonObjectMapper()

    fun saveKnowledgeArticle(knowledgeArticle: Article): Article? {
        try {
            if (knowledgeArticle.id.isNullOrBlank()) {
                knowledgeArticle.id = UUID.randomUUID().toString()
            }
            val knowledgeBaseDTOAsString = objectMapper.writeValueAsString(knowledgeArticle)
            IndexRequest(indexName)
            val indexRequest = IndexRequest(indexName)
            indexRequest.id(knowledgeArticle.id)
            indexRequest.source(knowledgeBaseDTOAsString, XContentType.JSON)
            indexRequest.refreshPolicy = WriteRequest.RefreshPolicy.WAIT_UNTIL
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT)
            return knowledgeArticle
        } catch (e: Exception) {
            logger.error("Elasticsearch: failed to article", e)
            throw BadRequestException("Elasticsearch: failed to save article", e)
        }
    }

    fun deleteKnowledgeArticle(knowledgeId: String) {
        try {
            val deleteRequest = DeleteRequest(indexName, knowledgeId)
            deleteRequest.refreshPolicy = WriteRequest.RefreshPolicy.WAIT_UNTIL
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT)
        } catch (e: Exception) {
            logger.error("Elasticsearch: failed to delete knowledge article for id: $knowledgeId", e)
            throw BadRequestException("Elasticsearch: failed to delete knowledge article", e)
        }
    }

    fun search(
        searchParams: Map<ArticleQueryAttribute, String>,
        size: Int,
        page: Int,
        sortBy: String,
        sortDirection: Sort.Direction
    ): Page<Article> {
        val searchRequest = toSearchRequest(searchParams, size, page, sortBy, SortOrder.fromString(sortDirection.name))
        val pageable = PageRequest.of(page, size)
        return try {
            val searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
            val totalSize = searchResponse.hits.totalHits?.value!!
            val listOfArticles = ArrayList<Article>()
            searchResponse.hits.hits.forEach { hit ->
                val article = objectMapper.readValue(hit.sourceAsString, Article::class.java)
                listOfArticles.add(article)
            }
            PageImpl(listOfArticles, pageable, totalSize)
        } catch (e: Exception) {
            logger.warn("Could not query elasticsearch", e)
            return Page.empty(pageable)
        }
    }

    private fun toSearchRequest(
        searchParams: Map<ArticleQueryAttribute, String>,
        size: Int,
        page: Int,
        sortBy: String,
        sortDirection: SortOrder
    ): SearchRequest {
        val searchQuery = QueryBuilders.boolQuery()

        searchParams.forEach {
            when (it.key) {
                ArticleQueryAttribute.Q -> {
                    searchQuery.must(
                        QueryBuilders.simpleQueryStringQuery("\"${searchParams[it.key]}\"")
                            .analyzeWildcard(false).defaultOperator(Operator.AND)
                    )
                }
            }
        }

        val searchRequest = SearchRequest()
        searchRequest.indices(indexName)
        searchRequest.source(
            SearchSourceBuilder()
                .query(searchQuery)
                .from(page * size).size(size)
                .sort(FieldSortBuilder(sortBy).order(sortDirection))
        )
        return searchRequest
    }
}

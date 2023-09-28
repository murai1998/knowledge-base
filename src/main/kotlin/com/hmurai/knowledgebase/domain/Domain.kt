package com.hmurai.knowledgebase.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.hmurai.knowledgebase.domain.QueryAttribute
import java.lang.RuntimeException
import java.util.*


enum class ArticleQueryAttribute(
    private val field: String,
    private val operation: QueryOperation,
    private val param: String,
    private val targetClazz: Class<*> = String::class.java
) : QueryAttribute {
    Q("q", QueryOperation.EQUAL, "q");
    override fun getFieldName(): String {
        return field
    }

    override fun getParamName(): String {
        return param
    }

    override fun getQueryOperation(): QueryOperation {
        return operation
    }

    override fun getTargetClass(): Class<*> {
        return targetClazz
    }

    companion object {
        fun getAttributeFromParamName(paramName: String): ArticleQueryAttribute {
            for (attribute in values()) {
                if (attribute.param == paramName) {
                    return attribute
                }
            }
            throw RuntimeException("No KnowledgeBaseQueryAttribute found with paramName: $paramName")
        }
    }
}



enum class QueryOperation {
    EQUAL,
    LIKE
}

data class Article(
    var id: String?,
    val title: String,
    val articleBody: String,
    val tags: MutableList<String> = ArrayList(),
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    val createdAt: Date,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    val lastUpdatedAt: Date,
    val createdBy: String,
    val lastUpdatedBy: String,
    val likes: Int = 0,
    val dislikes: Int = 0
)

package com.hmurai.knowledgebase.domain


interface QueryAttribute {

    fun getFieldName(): String?

    fun getQueryOperation(): QueryOperation?

    fun getParamName(): String?

    fun getTargetClass(): Class<*>?
}

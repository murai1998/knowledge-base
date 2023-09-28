package com.hmurai.knowledgebase.exceptions

class BadRequestException(errorMessage: String?, err: Throwable?) : RuntimeException(errorMessage, err)

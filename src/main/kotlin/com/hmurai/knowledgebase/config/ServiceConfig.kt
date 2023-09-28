package com.hmurai.knowledgebase.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.URISyntaxException

@Configuration
class ServiceConfig {

//    @Value("\${elasticsearch.host}")
    lateinit var elasticSearchHost: String

//    @Bean
//    @Throws(URISyntaxException::class)
//    fun restHighLevelClient(): RestHighLevelClient? {
//        val esUri = URI(elasticSearchHost)
//        return RestHighLevelClient(
//            RestClient.builder(HttpHost(esUri.host, esUri.port, esUri.scheme))
//        )
//    }

}

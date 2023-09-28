package com.hmurai.knowledgebase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableFeignClients
class KnowledgeBaseApplication

fun main(args: Array<String>) {
	runApplication<KnowledgeBaseApplication>(*args)
}

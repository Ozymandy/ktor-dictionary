package com.katemedia.com.katemedia

import com.katemedia.task.CounterDeserializer
import com.katemedia.task.Dictionary
import com.katemedia.task.DictionaryService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, CounterDeserializer(Json))
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    val dictionaryService = DictionaryService(database)
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

        get("/test") {
            call.respondText("Hello, world!")
        }

        post("/dictionaries") {
            val dictionary = call.receive<Dictionary>()
            val counter = dictionaryService.create(dictionary)
            call.respond(HttpStatusCode.Created, counter)
        }

        get("/dictionaries") {
            val counter = call.request.queryParameters["counter"] ?: throw IllegalArgumentException("Invalid Name")
            val dictionary = dictionaryService.read(counter)
            if (dictionary != null) {
                call.respond(HttpStatusCode.OK, dictionary)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/dictionaries/all") {
            val dictionaries = dictionaryService.getAll()
            if (dictionaries != null) {
                call.respond(HttpStatusCode.OK, dictionaries)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Increment
        put("/dictionaries/{counter}") {
            val name = call.parameters["counter"] ?: throw IllegalArgumentException("Invalid Name")
            val newValue = dictionaryService.increment(name)
            if (newValue != null) {
                call.respond(HttpStatusCode.OK, newValue)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/dictionaries/{counter}") {
            val counter = call.parameters["counter"] ?: throw IllegalArgumentException("Invalid Name")
            dictionaryService.delete(counter)
            call.respond(HttpStatusCode.OK)
        }
    }
}
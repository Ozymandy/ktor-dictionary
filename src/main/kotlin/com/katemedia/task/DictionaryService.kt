package com.katemedia.task

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DictionaryService(private val database: Database) {
    object Dictionaries : Table() {
        val name = varchar("name", length = 50)
        val counter = integer("counter")

        override val primaryKey = PrimaryKey(name)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Dictionaries)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(dictionary: Dictionary): String = dbQuery {
        Dictionaries.insert {
            it[name] = dictionary.name
            it[counter] = dictionary.counter
        }[Dictionaries.name]
    }

    suspend fun read(name: String): Dictionary? {
        return dbQuery {
            Dictionaries.selectAll()
                .where { Dictionaries.name eq name }
                .map { Dictionary(it[Dictionaries.name], it[Dictionaries.counter]) }
                .singleOrNull()
        }
    }
    suspend fun getAll(): List<Dictionary>? {
        return dbQuery {
            Dictionaries.selectAll()
                .map { Dictionary(it[Dictionaries.name], it[Dictionaries.counter]) }
        }
    }

    suspend fun increment(name: String) : Int? {
        dbQuery {
            Dictionaries.update({Dictionaries.name eq name}) {
                it[counter] = counter + 1
            }
        }
        return read(name)?.counter;
    }

    suspend fun delete(name: String) {
        dbQuery {
            Dictionaries.deleteWhere { Dictionaries.name.eq(name) }
        }
    }
}
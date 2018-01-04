package src.utils

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import src.model.DiaryEntries
import src.model.Foods
import java.sql.Connection.TRANSACTION_SERIALIZABLE


private var LOG_TO_CONSOLE: Boolean = false

//fun newTransaction(): Transaction = TransactionManager.currentOrNew(TRANSACTION_SERIALIZABLE).apply { logger.addLogger(StdOutSqlLogger) }
fun newTransaction(): Transaction = TransactionManager.currentOrNew(TRANSACTION_SERIALIZABLE).apply {
    if (LOG_TO_CONSOLE) logger.addLogger(StdOutSqlLogger)
}
// Isolation level options: TRANSACTION_SERIALIZABLE, TRANSACTION_READ_UNCOMMITTED

fun enableConoleLogger() {
    LOG_TO_CONSOLE = true
}

fun createTables() {
    with(newTransaction()) {
        create(Foods, DiaryEntries)
    }
}

fun <T> execute(command: () -> T) : T {
    with (newTransaction()) {
        return command().apply {
            commit()
            close()
        }
    }
}
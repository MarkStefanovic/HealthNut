package src.utils

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import src.model.DiaryEntries
import src.model.Foods
import java.sql.Connection.TRANSACTION_SERIALIZABLE


fun newTransaction(): Transaction = TransactionManager.currentOrNew(TRANSACTION_SERIALIZABLE).apply { logger.addLogger(StdOutSqlLogger) }
// Isolation level options: TRANSACTION_SERIALIZABLE, TRANSACTION_READ_UNCOMMITTED

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
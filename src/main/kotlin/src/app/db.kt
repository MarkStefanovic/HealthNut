package src.app

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update
import src.model.DiaryEntries
import src.model.Foods
import java.sql.Connection.TRANSACTION_SERIALIZABLE


/* Example JDBC connection strings:
    Database.connect("jdbc:sqlite:./test.db", "org.sqlite.JDBC")  // sqlite db in current working db
    Database.connect("jdbc:h2:./test", "org.h2.Driver")  // h2 in current working directory
    Database.connect("jdbc:h2:~/test", "org.h2.Driver")  // home directory
 */


fun newTransaction(): Transaction = TransactionManager.currentOrNew(TRANSACTION_SERIALIZABLE).apply { logger.addLogger(StdOutSqlLogger) }
// Isolation level options: TRANSACTION_SERIALIZABLE, TRANSACTION_READ_UNCOMMITTED

fun createTables() {
    with(newTransaction()) {
        // drop(Foods, DiaryEntries)
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
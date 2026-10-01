package com.soulquote.app.data

import androidx.sqlite.db.SupportSQLiteDatabase
import com.soulquote.app.data.local.SoulQuoteContentDatabase
import com.soulquote.app.data.local.SoulQuoteUserDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

class RoomMigrationTest {

    @Test
    fun testMigration1To2SqlExecution() {
        val migration = SoulQuoteUserDatabase.MIGRATION_1_2
        assertEquals(1, migration.startVersion)
        assertEquals(2, migration.endVersion)

        val executedSqlStatements = mutableListOf<String>()

        // Dynamic proxy simulating SupportSQLiteDatabase
        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSqlStatements.add(args[0].toString())
            }
            null
        } as SupportSQLiteDatabase

        migration.migrate(dbProxy)

        assertEquals(3, executedSqlStatements.size)

        // 1. Check table creation SQL
        val tableSql = executedSqlStatements[0]
        assertTrue("Must create journal_entries table", tableSql.contains("CREATE TABLE IF NOT EXISTS `journal_entries`"))
        assertTrue("Must include id PK", tableSql.contains("`id` TEXT NOT NULL"))
        assertTrue("Must include date", tableSql.contains("`date` TEXT NOT NULL"))
        assertTrue("Must include mood", tableSql.contains("`mood` TEXT NOT NULL"))
        assertTrue("Must include reflectionPrompt", tableSql.contains("`reflectionPrompt` TEXT NOT NULL"))
        assertTrue("Must include content", tableSql.contains("`content` TEXT NOT NULL"))
        assertTrue("Must include quoteId", tableSql.contains("`quoteId` TEXT"))
        assertTrue("Must include createdAt", tableSql.contains("`createdAt` INTEGER NOT NULL"))
        assertTrue("Must include updatedAt", tableSql.contains("`updatedAt` INTEGER NOT NULL"))

        // 2. Check indexes SQL
        val indexDateSql = executedSqlStatements[1]
        assertTrue("Must index date column", indexDateSql.contains("index_journal_entries_date"))

        val indexCreatedSql = executedSqlStatements[2]
        assertTrue("Must index createdAt column", indexCreatedSql.contains("index_journal_entries_createdAt"))
    }

    @Test
    fun testMigration2To3SqlExecution() {
        val migration = SoulQuoteUserDatabase.MIGRATION_2_3
        assertEquals(2, migration.startVersion)
        assertEquals(3, migration.endVersion)

        val executedSqlStatements = mutableListOf<String>()

        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSqlStatements.add(args[0].toString())
            }
            null
        } as SupportSQLiteDatabase

        migration.migrate(dbProxy)

        assertEquals(2, executedSqlStatements.size)

        // 1. Check user_quotes table creation SQL
        val tableSql = executedSqlStatements[0]
        assertTrue("Must create user_quotes table", tableSql.contains("CREATE TABLE IF NOT EXISTS `user_quotes`"))
        assertTrue("Must include id PK", tableSql.contains("`id` TEXT NOT NULL"))
        assertTrue("Must include text", tableSql.contains("`text` TEXT NOT NULL"))
        assertTrue("Must include author", tableSql.contains("`author` TEXT NOT NULL"))
        assertTrue("Must include category", tableSql.contains("`category` TEXT NOT NULL"))
        assertTrue("Must include createdAt", tableSql.contains("`createdAt` INTEGER NOT NULL"))
        assertTrue("Must include updatedAt", tableSql.contains("`updatedAt` INTEGER NOT NULL"))

        // 2. Check index SQL
        val indexSql = executedSqlStatements[1]
        assertTrue("Must index createdAt column", indexSql.contains("index_user_quotes_createdAt"))
    }

    @Test
    fun testContentDbMigration1To2SqlExecution() {
        val migration = SoulQuoteContentDatabase.MIGRATION_1_2
        assertEquals(1, migration.startVersion)
        assertEquals(2, migration.endVersion)

        val executedSqlStatements = mutableListOf<String>()

        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSqlStatements.add(args[0].toString())
            }
            null
        } as SupportSQLiteDatabase

        migration.migrate(dbProxy)

        assertEquals(2, executedSqlStatements.size)
        assertTrue(
            "Must alter table to add meditationStartSeconds",
            executedSqlStatements.any { it.contains("ALTER TABLE `meditations` ADD COLUMN `meditationStartSeconds` INTEGER DEFAULT NULL") }
        )
        assertTrue(
            "Must alter table to add youtubeUrl",
            executedSqlStatements.any { it.contains("ALTER TABLE `meditations` ADD COLUMN `youtubeUrl` TEXT DEFAULT NULL") }
        )
    }
}

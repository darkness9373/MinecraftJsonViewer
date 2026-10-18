package org.darkness.jsonuiview.data.repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.darkness.jsonuiview.data.local.db.AppDatabase
import org.darkness.jsonuiview.data.local.entity.FormEntity
import org.darkness.jsonuiview.data.local.entity.RecentFileEntity
import org.darkness.jsonuiview.data.model.form.ParseResult
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.data.parser.JsonUiParser
import org.darkness.jsonuiview.util.FileUtils
import org.darkness.jsonuiview.util.Logger

class FormRepository(
    private val database: AppDatabase,
    private val parser: JsonUiParser,
) {
    private val logger = Logger("FormRepository")
    private val _parsingProgress = MutableStateFlow<ParsingProgress>(ParsingProgress.Idle)
    val parsingProgress = _parsingProgress

    suspend fun parseAndCacheFile(uri: Uri, contentResolver: ContentResolver): ParseResult {
        _parsingProgress.value = ParsingProgress.Parsing("Reading file...")
        
        val jsonString = withContext(Dispatchers.IO) {
            FileUtils.readUriAsString(uri, contentResolver)
        }
        
        _parsingProgress.value = ParsingProgress.Parsing("Parsing JSON...")
        
        val parseResult = parser.parseFile(jsonString, uri.toString())
        
        _parsingProgress.value = ParsingProgress.Caching("Saving to database...")
        
        withContext(Dispatchers.IO) {
            database.formDao().deleteFormsBySourceFile(uri.toString())
            if (parseResult.forms.isNotEmpty()) {
                val entities = parseResult.forms.map { FormEntity.fromResolvedForm(it) }
                database.formDao().insertForms(entities)
            }
            
            val recentFile = RecentFileEntity(
                uri = uri.toString(),
                displayName = FileUtils.getDisplayName(uri, contentResolver)
            )
            database.recentFileDao().insertRecentFile(recentFile)
        }
        
        _parsingProgress.value = ParsingProgress.Success(parseResult.forms.size)
        
        return parseResult
    }

    fun getFormsBySourceFile(sourceFile: String) = database.formDao()
        .getFormsBySourceFile(sourceFile)
        .map { entities -> entities.map { FormEntity.toResolvedForm(it) } }

    fun getAllForms() = database.formDao()
        .getAllForms()
        .map { entities -> entities.map { FormEntity.toResolvedForm(it) } }

    suspend fun getFormById(id: String): ResolvedForm? {
        return withContext(Dispatchers.IO) {
            database.formDao().getFormById(id)?.let { FormEntity.toResolvedForm(it) }
        }
    }

    fun getRecentFiles() = database.recentFileDao()
        .getRecentFiles()

    suspend fun deleteRecentFile(uri: String) {
        database.recentFileDao().deleteRecentFile(uri)
    }

    sealed interface ParsingProgress {
        data class Parsing(val message: String) : ParsingProgress
        data class Caching(val message: String) : ParsingProgress
        data class Success(val formCount: Int) : ParsingProgress
        object Idle : ParsingProgress
    }
}
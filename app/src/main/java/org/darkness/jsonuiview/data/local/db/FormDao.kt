package org.darkness.jsonuiview.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import org.darkness.jsonuiview.data.local.entity.FormEntity
import org.darkness.jsonuiview.data.local.entity.RecentFileEntity

@Dao
interface FormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForms(forms: List<FormEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForm(form: FormEntity)

    @Query("SELECT * FROM forms WHERE sourceFile = :sourceFile ORDER BY timestamp DESC")
    fun getFormsBySourceFile(sourceFile: String): Flow<List<FormEntity>>

    @Query("SELECT * FROM forms WHERE id = :id")
    suspend fun getFormById(id: String): FormEntity?

    @Query("SELECT * FROM forms ORDER BY timestamp DESC")
    fun getAllForms(): Flow<List<FormEntity>>

    @Query("DELETE FROM forms WHERE sourceFile = :sourceFile")
    suspend fun deleteFormsBySourceFile(sourceFile: String)

    @Query("DELETE FROM forms")
    suspend fun deleteAllForms()
}

@Dao
interface RecentFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(file: RecentFileEntity)

    @Query("SELECT * FROM recent_files ORDER BY lastOpened DESC LIMIT 10")
    fun getRecentFiles(): Flow<List<RecentFileEntity>>

    @Query("DELETE FROM recent_files WHERE uri = :uri")
    suspend fun deleteRecentFile(uri: String)

    @Query("DELETE FROM recent_files")
    suspend fun clearRecentFiles()
}
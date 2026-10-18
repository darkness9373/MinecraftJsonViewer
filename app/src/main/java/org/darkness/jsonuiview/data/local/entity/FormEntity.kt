package org.darkness.jsonuiview.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json
import org.darkness.jsonuiview.data.model.form.FormComponent
import org.darkness.jsonuiview.data.model.form.FormType
import org.darkness.jsonuiview.data.model.form.ResolvedForm

@Entity(tableName = "forms")
data class FormEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,  // FormType.name
    val componentsJson: String,  // Serialized FormComponent list
    val sourceFile: String,
    val rawJson: String,
    val sourceReference: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        private val json = Json { prettyPrint = true }
        
        fun fromResolvedForm(form: ResolvedForm): FormEntity {
            return FormEntity(
                id = form.id,
                title = form.title,
                type = form.type.name,
                componentsJson = json.encodeToString(form.components),
                sourceFile = form.sourceFile,
                rawJson = form.rawJson,
                sourceReference = form.sourceReference,
            )
        }
        
        fun toResolvedForm(entity: FormEntity): ResolvedForm {
            return ResolvedForm(
                id = entity.id,
                title = entity.title,
                type = FormType.valueOf(entity.type),
                components = json.decodeFromJsonElement<List<FormComponent>>(json.parseToJsonElement(entity.componentsJson)),
                sourceFile = entity.sourceFile,
                rawJson = entity.rawJson,
                sourceReference = entity.sourceReference,
            )
        }
    }
}

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val uri: String,
    val displayName: String,
    val lastOpened: Long = System.currentTimeMillis()
)
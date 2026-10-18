package org.darkness.jsonuiview.data.model.form

import kotlinx.serialization.Serializable

enum class FormType {
    ACTION_FORM,   // from long_form
    MODAL_FORM     // from custom_form
}

@Serializable
sealed interface FormComponent {
    @Serializable
    data class Button(
        val text: String,
        val iconPath: String?
    ) : FormComponent

    @Serializable
    data class Label(
        val text: String
    ) : FormComponent

    @Serializable
    data class Header(
        val text: String
    ) : FormComponent

    @Serializable
    object Divider : FormComponent

    @Serializable
    data class TextField(
        val label: String,
        val placeholder: String,
        val defaultValue: String?
    ) : FormComponent

    @Serializable
    data class Dropdown(
        val label: String,
        val options: List<String>,
        val defaultIndex: Int?
    ) : FormComponent

    @Serializable
    data class Slider(
        val label: String,
        val min: Int,
        val max: Int,
        val step: Int,
        val defaultValue: Int?
    ) : FormComponent

    @Serializable
    data class Toggle(
        val label: String,
        val defaultValue: Boolean?
    ) : FormComponent

    @Serializable
    data class Custom(
        val type: String,
        val properties: Map<String, Any>
    ) : FormComponent
}

@Serializable
data class ResolvedForm(
    val id: String,
    val title: String,
    val type: FormType,
    val components: List<FormComponent>,
    val sourceFile: String,
    val rawJson: String,
    val sourceReference: String,
)

data class ParseResult(
    val forms: List<ResolvedForm>,
    val registry: ElementRegistry,
)
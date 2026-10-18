package org.darkness.jsonuiview.data.model.export

import kotlinx.serialization.Serializable

@Serializable
data class TestFormState(
    val title: String = "",
    val components: List<TestFormComponent> = emptyList(),
) {
    val hasModalComponents: Boolean
        get() = components.any { it is TestFormComponent.TextField || it is TestFormComponent.Dropdown || it is TestFormComponent.Slider || it is TestFormComponent.Toggle }

    fun toJson(): String {
        val json = kotlinx.serialization.json.Json { prettyPrint = true }
        return json.encodeToString(this)
    }

    fun toScriptAPI(): String {
        val builder = StringBuilder()
        builder.appendLine("import { world } from \"@minecraft/server\";")
        builder.appendLine("import { ${if (hasModalComponents) "ModalFormData" else "ActionFormData"} } from \"@minecraft/server-ui\";")
        builder.appendLine()
        builder.appendLine("const form = new ${if (hasModalComponents) "ModalFormData" else "ActionFormData"}()")
        builder.appendLine("    .title(\"${title.replace("\"", "\\\"").replace("\n", "\\n")}\");")
        
        components.forEachIndexed { index, comp ->
            builder.appendLine("    .${comp.toScriptAPI(index)};")
        }
        
        builder.appendLine()
        builder.appendLine("form.show(player).then(response => {")
        builder.appendLine("    if (response.canceled) return;")
        builder.appendLine("    // Handle response")
        if (hasModalComponents) {
            builder.appendLine("    const [${components.filterIsInstance<TestFormComponent.TextField>().map { "text${components.indexOf(it)}" }.joinToString(", ")}] = response.formValues;")
        } else {
            builder.appendLine("    const buttonIndex = response.selection;")
        }
        builder.appendLine("});")
        
        return builder.toString()
    }
}

@Serializable
sealed interface TestFormComponent {
    fun toScriptAPI(index: Int): String

    @Serializable
    data class Button(
        val text: String,
        val iconPath: String?
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return if (iconPath != null && iconPath.isNotBlank()) {
                "button(\"${text.replace("\"", "\\\"")}\", \"$iconPath\")"
            } else {
                "button(\"${text.replace("\"", "\\\"")}\")"
            }
        }
    }

    @Serializable
    data class Label(
        val text: String
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return "label(\"${text.replace("\"", "\\\"")}\")"
        }
    }

    @Serializable
    data class Header(
        val text: String
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return "header(\"${text.replace("\"", "\\\"")}\")"
        }
    }

    @Serializable
    object Divider : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return "divider()"
        }
    }

    @Serializable
    data class TextField(
        val label: String,
        val placeholder: String,
        val defaultValue: String?
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return if (defaultValue != null && defaultValue.isNotBlank()) {
                "textField(\"${label.replace("\"", "\\\"")}\", \"${placeholder.replace("\"", "\\\"")}\", \"${defaultValue.replace("\"", "\\\"")}\")"
            } else {
                "textField(\"${label.replace("\"", "\\\"")}\", \"${placeholder.replace("\"", "\\\"")}\")"
            }
        }
    }

    @Serializable
    data class Dropdown(
        val label: String,
        val options: List<String>,
        val defaultIndex: Int?
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            val optionsStr = options.map { "\"${it.replace("\"", "\\\"")}\"" }.joinToString(", ")
            return if (defaultIndex != null) {
                "dropdown(\"${label.replace("\"", "\\\"")}\", [$optionsStr], $defaultIndex)"
            } else {
                "dropdown(\"${label.replace("\"", "\\\"")}\", [$optionsStr])"
            }
        }
    }

    @Serializable
    data class Slider(
        val label: String,
        val min: Int,
        val max: Int,
        val step: Int,
        val defaultValue: Int?
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return if (defaultValue != null) {
                "slider(\"${label.replace("\"", "\\\"")}\", $min, $max, $step, $defaultValue)"
            } else {
                "slider(\"${label.replace("\"", "\\\"")}\", $min, $max, $step)"
            }
        }
    }

    @Serializable
    data class Toggle(
        val label: String,
        val defaultValue: Boolean?
    ) : TestFormComponent {
        override fun toScriptAPI(index: Int): String {
            return if (defaultValue != null) {
                "toggle(\"${label.replace("\"", "\\\"")}\", $defaultValue)"
            } else {
                "toggle(\"${label.replace("\"", "\\\"")}\")"
            }
        }
    }
}
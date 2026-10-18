package org.darkness.jsonuiview.data.model.jsonui

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.addContextual

@Serializable
data class ServerFormJson(
    @SerialName("namespace") val namespace: String = "server_form",
    @SerialName("main_screen_content") val mainScreenContent: MainScreenContent?,
    @SerialName("long_form") val longForm: UIElement?,
    @SerialName("custom_form") val customForm: UIElement?,
    @SerialName("form_buttons") val formButtons: UIElement?,
    @SerialName("custom_dropdown") val customDropdown: UIElement?,
) {
    @Transient
    val customElements: MutableMap<String, UIElement> = mutableMapOf()
}

@Serializable
data class MainScreenContent(
    @SerialName("type") val type: String?,
    @SerialName("controls") val controls: List<UIControl>?,
    @SerialName("modifications") val modifications: List<Modification>?,
)

@Serializable
data class UIControl(
    @SerialName("name") val name: String = "",
    @SerialName("element") val element: UIElement
) {
    @Transient
    val fullName: String
        get() = name
}

@Serializable
data class UIElement(
    @SerialName("type") val type: String?,
    @SerialName("controls") val controls: List<UIControl>?,
    @SerialName("bindings") val bindings: List<Binding>?,
    @SerialName("modifications") val modifications: List<Modification>?,
    @SerialName("factory") val factory: Factory?,
    @SerialName("size") val size: List<Any>?,
    @SerialName("texture") val texture: String?,
    @SerialName("text") val text: String?,
    @SerialName("layer") val layer: Int?,
    @SerialName("binding_name") val bindingName: String?,
    @SerialName("binding_type") val bindingType: String?,
    @SerialName("source_property_name") val sourcePropertyName: String?,
    @SerialName("target_property_name") val targetPropertyName: String?,
    @SerialName("binding_condition") val bindingCondition: String?,
    @SerialName("source_control_name") val sourceControlName: String?,
    @SerialName("resolve_sibling_scope") val resolveSiblingScope: Boolean?,
    @SerialName("binding_collection_name") val bindingCollectionName: String?,
    @SerialName("binding_name_override") val bindingNameOverride: String?,
    @SerialName("control_name") val controlName: String?,
    @SerialName("array_name") val arrayName: String?,
    @SerialName("operation") val operation: String?,
    @SerialName("where") val where: Binding?,
    @SerialName("target") val target: Binding?,
    @SerialName("target_control") val targetControl: String?,
    @SerialName("value") val value: JsonElement?,
    @SerialName("name") val factoryName: String?,
    @SerialName("control_name") val controlNameFactory: String?,
    @SerialName("control_ids") val controlIds: Map<String, String>?,
    @SerialName("factory_variables") val factoryVariables: List<String>?,
    @SerialName("max_children_size") val maxChildrenSize: Int?,
) {
    @Transient
    val dynamicProps: MutableMap<String, Any> = mutableMapOf()

    fun getDynamicProp(key: String): Any? = dynamicProps[key]
    fun setDynamicProp(key: String, value: Any) { dynamicProps[key] = value }
}

@Serializable
data class Factory(
    @SerialName("name") val name: String,
    @SerialName("control_name") val controlName: String?,
    @SerialName("control_ids") val controlIds: Map<String, String>?,
    @SerialName("factory_variables") val factoryVariables: List<String>?,
    @SerialName("max_children_size") val maxChildrenSize: Int?,
)

@Serializable
data class Binding(
    @SerialName("binding_type") val bindingType: String?,
    @SerialName("binding_name") val bindingName: String?,
    @SerialName("binding_name_override") val bindingNameOverride: String?,
    @SerialName("binding_condition") val bindingCondition: String?,
    @SerialName("source_control_name") val sourceControlName: String?,
    @SerialName("source_property_name") val sourcePropertyName: String?,
    @SerialName("target_property_name") val targetPropertyName: String?,
    @SerialName("binding_collection_name") val bindingCollectionName: String?,
    @SerialName("resolve_sibling_scope") val resolveSiblingScope: Boolean?,
)

@Serializable
data class Modification(
    @SerialName("control_name") val controlName: String?,
    @SerialName("array_name") val arrayName: String,
    @SerialName("operation") val operation: String,
    @SerialName("where") val where: Binding?,
    @SerialName("target") val target: Binding?,
    @SerialName("target_control") val targetControl: String?,
    @SerialName("value") val value: JsonElement?,
)

object JsonUiSerializers {
    val module = SerializersModule().apply {
        addContextual(JsonElementSerializer())
    }
}

class JsonElementSerializer : KSerializer<JsonElement> {
    override val descriptor: SerialDescriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("JsonElement", kotlinx.serialization.descriptors.PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JsonElement) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): JsonElement {
        return kotlinx.serialization.json.Json.parse(decoder.decodeString())
    }
}
package org.darkness.jsonuiview.data.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.darkness.jsonuiview.data.model.form.ParseResult
import org.darkness.jsonuiview.data.model.form.ResolvedForm
import org.darkness.jsonuiview.data.model.form.FormType
import org.darkness.jsonuiview.data.model.jsonui.ServerFormJson
import org.darkness.jsonuiview.data.model.jsonui.UIControl
import org.darkness.jsonuiview.data.model.jsonui.UIElement
import org.darkness.jsonuiview.util.Logger

class JsonUiParser(
    private val referenceResolver: ReferenceResolver,
    private val bindingEvaluator: BindingEvaluator,
    private val modificationApplier: ModificationApplier,
    private val formExtractor: FormExtractor,
) {
    private val logger = Logger("JsonUiParser")
    private val json = Json { 
        ignoreUnknownKeys = true 
        serializersModule = org.darkness.jsonuiview.data.model.jsonui.JsonUiSerializers.module
    }

    suspend fun parseFile(jsonString: String, sourceFile: String): ParseResult {
        return try {
            val serverForm = json.decodeFromJsonElement<ServerFormJson>(json.parseToJsonElement(jsonString))
            parseServerForm(serverForm, sourceFile, jsonString)
        } catch (e: Exception) {
            logger.e("Failed to parse server_form.json", e)
            ParseResult(emptyList(), ElementRegistry(ServerFormJson()))
        }
    }

    private fun parseServerForm(serverForm: ServerFormJson, sourceFile: String, rawJson: String): ParseResult {
        val registry = ElementRegistry(serverForm)
        
        val factories = findFactories(serverForm.mainScreenContent)
        
        val resolvedForms = mutableListOf<ResolvedForm>()
        
        for (factory in factories) {
            factory.controlIds?.forEach { (formTypeKey, reference) ->
                val targetElement = referenceResolver.resolve(reference, registry)
                if (targetElement != null) {
                    val modifiedElement = modificationApplier.apply(targetElement, registry)
                    
                    val bindingContext = bindingEvaluator.evaluate(modifiedElement, registry)
                    
                    val formType = when (formTypeKey) {
                        "long_form" -> FormType.ACTION_FORM
                        "custom_form" -> FormType.MODAL_FORM
                        else -> continue
                    }
                    
                    val components = formExtractor.extract(modifiedElement, formType, bindingContext)
                    val title = (bindingContext["#title_text"] as? String) 
                        ?: (bindingContext["title_text"] as? String)
                        ?: "Untitled Form"
                    
                    val formId = "${sourceFile}#${reference}"
                    
                    resolvedForms.add(ResolvedForm(
                        id = formId,
                        title = title,
                        type = formType,
                        components = components,
                        sourceFile = sourceFile,
                        rawJson = rawJson,
                        sourceReference = reference,
                    ))
                    
                    logger.i("Parsed form: $title (${formType.name}) with ${components.size} components")
                } else {
                    logger.w("Could not resolve reference: $reference")
                }
            }
        }
        
        return ParseResult(resolvedForms, registry)
    }

    private fun findFactories(mainScreenContent: UIElement?): List<UIElement> {
        val factories = mutableListOf<UIElement>()
        
        fun search(element: UIElement?) {
            element?.let {
                if (it.factory != null && it.factory!!.name.isNotBlank()) {
                    factories.add(it)
                }
                it.controls?.forEach { control ->
                    search(control.element)
                }
                if (it.type == "factory" && it.factory != null) {
                    factories.add(it)
                }
            }
        }
        
        search(mainScreenContent)
        return factories
    }
}
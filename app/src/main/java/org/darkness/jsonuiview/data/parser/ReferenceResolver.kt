package org.darkness.jsonuiview.data.parser

import org.darkness.jsonuiview.data.model.jsonui.UIElement
import org.darkness.jsonuiview.util.Logger

class ReferenceResolver {
    private val logger = Logger("ReferenceResolver")

    fun resolve(reference: String, registry: ElementRegistry): UIElement? {
        if (!reference.startsWith("@")) {
            logger.w("Invalid reference format (missing @): $reference")
            return null
        }

        val parts = reference.substring(1).split(".", limit = 2)
        if (parts.size != 2) {
            logger.w("Invalid reference format: $reference")
            return null
        }

        val (refNamespace, controlName) = parts[0] to parts[1]

        if (refNamespace != registry.namespace) {
            logger.w("Cross-namespace reference not yet supported: $refNamespace != ${registry.namespace}")
            return null
        }

        val element = registry.findElement(controlName)
        if (element == null) {
            logger.w("Element not found: $controlName in namespace $refNamespace")
        }
        return element
    }

    fun resolveControlId(controlIds: Map<String, String>?, key: String, registry: ElementRegistry): UIElement? {
        val reference = controlIds?.get(key)
        return reference?.let { resolve(it, registry) }
    }
}
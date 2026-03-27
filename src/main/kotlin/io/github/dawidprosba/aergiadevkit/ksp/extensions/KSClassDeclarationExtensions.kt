package io.github.dawidprosba.aergiadevkit.ksp.extensions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

/**
 * Finds all properties within the class declaration that are annotated with the specified annotation.
 *
 * @param annotationSimpleName The simple name of the annotation to search for.
 * @return A list of properties that are annotated with the specified annotation.
 */
fun KSClassDeclaration.findPropertiesWithAnnotation(annotationSimpleName: String): List<KSPropertyDeclaration> {
    return this.getAllProperties()
        .filter { it.hasAnnotation(annotationSimpleName) }
        .toList()
}

/**
 * Retrieves the specified properties from the companion object of the closest parent class
 * that contains any of the provided property names.
 *
 * @param propertyNames The names of the properties to look for in the companion object of the parent class.
 * @return A map where the keys are the property names that were found, and the values are their corresponding code blocks.
 */
fun KSClassDeclaration.getCompanionPropertiesFromParentClass(vararg propertyNames: String): Map<String, CodeBlock> {
    val superClass = superTypes
        .map { it.resolve() }
        .mapNotNull { it.declaration as? KSClassDeclaration }
        .firstOrNull { it.hasAnyCompanionProperty(*propertyNames) }
        ?: return emptyMap()

    val companionProperties = superClass.getCompanionObject()
        ?.getAllProperties()
        ?.map { it.simpleName.asString() }
        ?.toSet()
        ?: return emptyMap()

    return propertyNames
        .filter { it in companionProperties }
        .associateWith { name ->
            CodeBlock.of(
                "%T.$name",
                ClassName(superClass.packageName.asString(), superClass.simpleName.asString())
            )
        }
}

/**
 * Checks if the companion object of this class declaration contains any of the specified properties.
 *
 * @param propertyNames The names of the properties to check for in the companion object.
 * @return `true` if the companion object contains at least one of the specified properties, otherwise `false`.
 */
fun KSClassDeclaration.hasAnyCompanionProperty(vararg propertyNames: String): Boolean {
    return this.getCompanionObject()
        ?.getAllProperties()
        ?.any { it.simpleName.asString() in propertyNames }
        ?: false
}

fun KSClassDeclaration.getCompanionObject(): KSClassDeclaration? {
    return declarations
        .filterIsInstance<KSClassDeclaration>()
        .firstOrNull { it.isCompanionObject }
}





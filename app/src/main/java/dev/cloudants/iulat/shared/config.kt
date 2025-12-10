package dev.cloudants.iulat.shared

import kotlinx.serialization.json.Json

val KotlinJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

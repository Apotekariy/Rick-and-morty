package com.example.rickandmorty.domain

data class CharacterFilterOptions(
    val name: String? = null,
    val status: Status? = null,
    val species: String? = null,
    val type: String? = null,
    val gender: Gender? = null
) {
    fun hasFilters(): Boolean {
        return !name.isNullOrBlank() ||
                status != null ||
                !species.isNullOrBlank() ||
                !type.isNullOrBlank() ||
                gender != null
    }

    fun toQueryMap(): Map<String, String> {
        val filters = mutableMapOf<String, String>()

        name?.takeIf { it.isNotBlank() }?.let { filters["name"] = it }
        status?.let { filters["status"] = it.apiValue }
        species?.takeIf { it.isNotBlank() }?.let { filters["species"] = it }
        type?.takeIf { it.isNotBlank() }?.let { filters["type"] = it }
        gender?.let { filters["gender"] = it.apiValue }

        return filters
    }

    enum class Status(val apiValue: String, val displayName: String) {
        ALIVE("Alive", "Alive"),
        DEAD("Dead", "Dead"),
        UNKNOWN("unknown", "Unknown")
    }

    enum class Gender(val apiValue: String, val displayName: String) {
        FEMALE("Female", "Female"),
        MALE("Male", "Male"),
        GENDERLESS("Genderless", "Genderless"),
        UNKNOWN("unknown", "Unknown")
    }
}

typealias CharacterStatus = CharacterFilterOptions.Status
typealias CharacterGender = CharacterFilterOptions.Gender
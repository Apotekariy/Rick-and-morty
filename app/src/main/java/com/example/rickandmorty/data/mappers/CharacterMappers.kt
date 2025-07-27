package com.example.rickandmorty.data.mappers

import com.example.rickandmorty.data.local.CharacterEntity
import com.example.rickandmorty.data.remote.CharacterDto
import com.example.rickandmorty.domain.CharacterFromShow


fun CharacterDto.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterEntity.Origin(
            name = origin.name,
            url = origin.url
        ),
        location = CharacterEntity.Location(
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created,
    )
}

fun CharacterEntity.toCharacter(): CharacterFromShow {
    return CharacterFromShow(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterFromShow.Origin(
            name = origin.name,
            url = origin.url
        ),
        location = CharacterFromShow.Location(
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created
    )
}

fun CharacterDto.toCharacter(): CharacterFromShow {
    return CharacterFromShow(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterFromShow.Origin(
            name = origin.name,
            url = origin.url
        ),
        location = CharacterFromShow.Location(
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created,
    )
}

fun CharacterFromShow.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        origin = CharacterEntity.Origin(
            name = origin.name,
            url = origin.url
        ),
        location = CharacterEntity.Location(
            name = location.name,
            url = location.url
        ),
        image = image,
        episode = episode,
        url = url,
        created = created
    )
}

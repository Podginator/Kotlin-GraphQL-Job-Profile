package org.skyworkz.profile.web.graphQL.data.mutations

import graphql.schema.DataFetcher
import org.skyworkz.profile.domain.model.Tag
import org.skyworkz.profile.domain.repository.ITagRepository

class TagMutations(private val tagRepository: ITagRepository) {

    val createTags = DataFetcher {
        val tagMaps = it.getArgument<List<Map<String, Any>>>("tags")
        val tags = tagMaps.map { tag -> Tag(name = tag["name"].toString(), id = null) }

        tagRepository.createTags(tags)
    }

}
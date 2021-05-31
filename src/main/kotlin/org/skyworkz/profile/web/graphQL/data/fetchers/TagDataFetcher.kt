package org.skyworkz.profile.web.graphQL.data.fetchers

import graphql.schema.DataFetcher
import org.skyworkz.profile.domain.repository.ITagRepository

class TagDataFetcher(private val tagRepository: ITagRepository) {

    val getTags = DataFetcher {
        val withJobs = it.getArgument<Boolean>("withJobs") ?: false

        if (withJobs)
            tagRepository.getAllTagsWithJobs()
        else
            tagRepository.getAllTags()
    }

    val getTagById = DataFetcher {
        val id = it.getArgument<Int>("id")
        tagRepository.getById(id)
    }


}
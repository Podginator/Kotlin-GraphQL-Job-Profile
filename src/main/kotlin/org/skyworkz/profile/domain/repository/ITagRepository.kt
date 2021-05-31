package org.skyworkz.profile.domain.repository

import org.skyworkz.profile.domain.model.Tag

interface ITagRepository {
    fun getAllTagsWithJobs() : List<Tag>
    fun getAllTags() : List<Tag>
    fun createTags(tags: List<Tag>) : List<Tag>
    fun getById(id: Int): Tag?
}
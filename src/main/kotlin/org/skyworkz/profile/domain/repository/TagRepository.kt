package org.skyworkz.profile.domain.repository

import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.skyworkz.profile.domain.model.Tag
import org.skyworkz.profile.domain.model.sql.JobTags
import org.skyworkz.profile.domain.model.sql.Jobs
import org.skyworkz.profile.domain.model.sql.Tags
import org.skyworkz.profile.domain.model.sql.toTag

class TagRepository : ITagRepository {
    override fun getAllTagsWithJobs(): List<Tag> {
        return transaction {
            Tags.select { Tags.id inList JobTags.selectAll().map { it[JobTags.tag].value } }
                .mapNotNull { it.toTag() }
        }
    }

    override fun getAllTags(): List<Tag> {
        return transaction {
            Tags.selectAll()
                .mapNotNull { it.toTag() }
        }
    }

    override fun createTags(tags: List<Tag>): List<Tag> {
        return transaction {
            // Check if they exist, if they do return them and remove them from the list.
            // inefficient.
            val lowerCaseTags = tags.map { it.copy(name = it.name.toLowerCase()) }
            val tagNames = lowerCaseTags.map { it.name }.toSet()
            val preExistingTags = Tags.select { Tags.name inList tagNames }
                .mapNotNull { Tag(name = it[Tags.name], id = it[Tags.id].value) }


            // Remove all existing tags from the list then insert.
            val newTags = lowerCaseTags.filter { !(it.name in preExistingTags.map { it.name }) }
            val newInsertedTags = Tags.batchInsert(newTags) {
                this[Tags.name] = it.name
            }.map { Tag(name = it[Tags.name], id = it[Tags.id].value) }

            preExistingTags + newInsertedTags
        }
    }

    override fun getById(id: Int): Tag? {
        return transaction {
            Tags.select { Tags.id eq id }.mapNotNull { it.toTag() }.singleOrNull()
        }
    }
}
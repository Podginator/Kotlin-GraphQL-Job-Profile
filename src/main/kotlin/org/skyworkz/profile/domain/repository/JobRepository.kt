package org.skyworkz.profile.domain.repository

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.model.sql.*
import org.skyworkz.profile.domain.model.toJob
import org.skyworkz.profile.util.sortByListOrder

class JobRepository(private val tagRepository : ITagRepository) : IJobRepository {

    private fun getUserJoin() = Jobs.join(Profiles, JoinType.INNER, additionalConstraint = { Jobs.createdBy eq Profiles.id })

    private fun getDependencies(users: Query) : List<Job> {
        return transaction {
            val jobToId = users.associateTo(mutableMapOf(), { it[Jobs.id].value to it.toJob() })

            val tagNames = JobTags.join(Tags, JoinType.INNER, additionalConstraint = { Tags.id eq JobTags.tag })
                .slice(Tags.id, Tags.name, JobTags.job)
                .select { JobTags.job inList jobToId.keys }

            tagNames.forEach {
                val jobId = it[JobTags.job].value
                jobToId[jobId]!!.tags.add(it.toTag())
            }

            jobToId.values.toList()
        }
    }

    private fun retrievePaginatedResult(query: Query, limit: Int?, from: Int?) : PagedResult<Job> {
        var size : Int? = null
        if (limit != null) {
            size = query.count()
            query.limit(limit, offset = from ?: 0)
        }
        val data = getDependencies(query)
        size = size ?: data.size

        return PagedResult(totalSize = size, data = data)
    }

    override fun create(jobs: List<Job>, createdById: Int): List<Job> {
        return transaction {
            jobs.map { job ->
                val tags = tagRepository.createTags(job.tags)

                val createdJobId = Jobs.insert {
                    it[description] = job.description
                    it[position] = job.position
                    it[hourlyRate] = job.hourlyRate
                    it[createdBy] = EntityID(createdById, Profiles)
                    it[createdAt] = job.createdAt
                }
                    .get(Jobs.id)

                JobTags.batchInsert(tags) {
                    this[JobTags.job] = EntityID(createdJobId.value, Jobs)
                    this[JobTags.tag] = EntityID(it.id!!, Tags)
                }

                getById(createdJobId.value)!!
            }
        }
    }

    override fun size(): Int {
        return transaction {
            Jobs.selectAll().count()
        }
    }

    override fun delete(job: Job) : Boolean {
        return deleteById(job.id!!)
    }

    override fun deleteById(jobId: Int) : Boolean {
        return transaction {
            Jobs.deleteWhere { Jobs.id eq jobId } == 1
        }
    }

    override fun getById(id: Int): Job? {
        return getByIds(listOf(id)).getOrNull(0)
    }

    override fun getByIds(ids: List<Int>): List<Job?> {
        return transaction {
            val jobs = getUserJoin().select({ Jobs.id inList ids })
            getDependencies(jobs).sortByListOrder(ids, { it.id })
        }
    }

    override fun getByUsers(userList: List<User>, limit: Int?, from: Int?): List<PagedResult<Job>> {
       return userList.map { user ->
           transaction {
               val ids = user.tags.map { it.id!! }.toSet()
               val jobTags = JobTags.join(Jobs, JoinType.INNER, additionalConstraint = { Jobs.id eq JobTags.job })
                   .join(Profiles, JoinType.INNER, additionalConstraint = { Profiles.id eq Jobs.createdBy })
                   .select { JobTags.tag inList ids }
               retrievePaginatedResult(jobTags, limit, from)
           }
       }
    }

    override fun getAll(limit: Int?, from: Int?): PagedResult<Job> {
        return transaction {
            val jobs = getUserJoin().selectAll().orderBy(Jobs.createdAt, order = SortOrder.DESC)
            retrievePaginatedResult(jobs, limit, from)
        }
    }

    override fun getByTag(tagId: Int, limit: Int?, from: Int?): PagedResult<Job> {
        return transaction {
            val jobs = JobTags.select { JobTags.tag eq tagId }
            var size: Int? = null

            if (limit != null) {
                size = jobs.count()
                jobs.limit(limit, offset = from ?: 0)
            }


            val jobIds = jobs.mapNotNull { it[JobTags.job].value }
            val data = getByIds(jobIds).filterNotNull()

            PagedResult(totalSize = size ?: data.size, data = data)
        }
    }

}
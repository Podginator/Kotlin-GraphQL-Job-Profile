package org.skyworkz.profile.domain.repository

import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User

interface IJobRepository {
    fun create(jobs: List<Job>, createdById: Int) : List<Job>
    fun size() : Int
    fun delete(job: Job) : Boolean
    fun deleteById(jobId: Int) : Boolean
    fun getById(id: Int) : Job?
    fun getByIds(ids: List<Int>) : List<Job?>
    fun getByUsers(userList: List<User>, limit: Int? = null, from: Int? = null): List<PagedResult<Job>>
    fun getAll(limit: Int? = null, from: Int? = null) : PagedResult<Job>
    fun getByTag(tagId: Int, limit : Int? = null, from: Int? = null) : PagedResult<Job>
}
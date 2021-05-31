package org.skyworkz.profile.web.graphQL.data.mutations

import graphql.schema.DataFetcher
import org.skyworkz.profile.domain.exception.InvalidAccessException
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.repository.IJobRepository
import org.skyworkz.profile.messages.IEventBroker
import org.skyworkz.profile.messages.events.Event.JobCreated
import org.skyworkz.profile.web.graphQL.context.AuthContext

class JobMutations(private val jobRepo: IJobRepository, private val eventBroker : IEventBroker) {

    val createJob = DataFetcher {
        val jobMap = it.getArgument<Map<String, Any>>("job")
        val job = Job.fromMap(jobMap)
        val authContext : AuthContext = it.getContext()

        if (authContext.user == null) {
            throw InvalidAccessException()
        }

        var id: Int? = null
        try {
            val createdJob = jobRepo.create(listOf(job), authContext.user.id!!)
            id = job.id
            eventBroker.commit(JobCreated(job))
            createdJob
        } catch (e: Exception) {
            id?.let { jobRepo.deleteById(it) }
            throw e
        }
    }

    val createJobs = DataFetcher {
        val jobMaps = it.getArgument<List<Map<String, Any>>>("jobs")
        val jobs = jobMaps.map { Job.fromMap(it) }
        val authContext : AuthContext = it.getContext()

        if (authContext.user == null) {
            throw InvalidAccessException()
        }

        var id: Int? = null
        try {
            val createdJobs = jobRepo.create(jobs, authContext.user.id!!)
            createdJobs.forEach { eventBroker.commit(JobCreated(it)) }
            createdJobs
        } catch (e: Exception) {
            id?.let { jobRepo.deleteById(it) }
            throw e
        }
    }

    val deleteJob = DataFetcher {
        val jobId = it.getArgument<Int>("id")
        jobRepo.deleteById(jobId)
    }

}
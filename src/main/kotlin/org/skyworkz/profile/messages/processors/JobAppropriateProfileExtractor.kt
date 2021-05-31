package org.skyworkz.profile.messages.processors

import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.JobUpdate
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IUserRepository

class JobAppropriateProfileExtractor(private val userRepository: IUserRepository) {
    fun extractAppropriateUsersFromJob(job : Job) : Map<User, List<JobUpdate>> {
        val appropriateProfiles = userRepository.getByJob(job)
        val appropriateIds = appropriateProfiles.map { it?.id!! }

        // This method orders the list into the same order as it's input.
        val followers = userRepository.getFollowers(appropriateIds)

        return appropriateProfiles.mapIndexed{ index, user ->
            val jobUpdate = JobUpdate(profile = user!!, job = job)
            followers.get(index).map { it to jobUpdate }
        }
            .flatten()
            .groupBy({ it.first }, { it.second })
    }
}
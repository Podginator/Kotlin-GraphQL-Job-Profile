package org.skyworkz.profile.domain.repository

import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.model.request.CVDto
import org.skyworkz.profile.domain.model.request.UserUpdate

interface IUserRepository {
    fun getAll(limit: Int? = null, from: Int? = null): List<User>
    fun getByJob(jobs: Job): Set<User?>
    fun getByJob(jobs: List<Job>): List<Set<User?>>
    fun getByIds(ids: List<Int>): List<User?>
    fun getByEmails(emails: List<String>): List<User?>
    fun getFollowers(id: Int): List<User>
    fun getFollowers(id: List<Int>): List<List<User>>
    fun getFollowing(id: Int): List<User>
    fun getFollowing(id: List<Int>): List<List<User>>
    fun getCV(id: Int): CVDto?

    fun create(user: User): User
    fun delete(userId: Int)
    fun update(userId: Int, user: UserUpdate): User?
    fun subscribeUser(subscribeeId: Int, subscribedId: Int): Boolean
    fun unsubscribeUser(subscribeeId: Int, subscribedId: Int): Boolean

    fun updateCV(userId: Int, cvJson: String): CVDto?
}
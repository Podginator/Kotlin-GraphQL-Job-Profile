package org.skyworkz.profile.domain.repository

import com.google.gson.Gson
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.model.request.CVDto
import org.skyworkz.profile.domain.model.request.UserUpdate
import org.skyworkz.profile.domain.model.sql.*
import org.skyworkz.profile.util.sortByListOrder

class UserRepository(private val tagRepository: ITagRepository) : IUserRepository {

    private fun getDependencies(users: Query) : List<User> {
        return transaction {
            val userToId = users.associateTo(mutableMapOf(), { it[Profiles.id].value to it.toUser() })
            // Get The associated paragraphs
            val tagNames = ProfileTag.join(Tags, JoinType.INNER, additionalConstraint = {Tags.id eq ProfileTag.tag})
                .slice(Tags.id, Tags.name, ProfileTag.profile)
                .select { ProfileTag.profile inList userToId.keys }

            tagNames.forEach {
                val userId = it[ProfileTag.profile].value
                userToId[userId]!!.tags.add(it.toTag())
            }

            userToId.values.toList()
        }
    }

    override fun create(user: User): User {
        transaction {
            val id = Profiles.insertAndGetId {
                it[email] = user.email.toLowerCase()
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[password] = user.password!!
                it[available] = user.available!!
                it[minimumHourlyRate] = user.minimumHourlyRate!!
            }

            CV.insert {
                it[profileId] = id
            }
        }

        return user
    }

    override fun delete(userId: Int) {
        transaction {
            Profiles.deleteWhere { Profiles.id eq userId }
        }
    }

    override fun update(userId: Int, user: UserUpdate): User? {
        return transaction {
            // Update the base model.
            Profiles.update({ Profiles.id eq userId }) {
                if (user.approved != null) it[approved] = user.approved
                if (user.available != null) it[available] = user.available!!
                if (user.firstName != null) it[firstName] = user.firstName!!
                if (user.lastName != null) it[lastName] = user.lastName!!
                if (user.minimumHourlyRate != null) it[minimumHourlyRate] = user.minimumHourlyRate
            }

            if (user.tags != null) {
                //Delete the existing tags (There might be overlaps)
                ProfileTag.deleteWhere { ProfileTag.profile eq userId }
                // Then add any new ones
                val tags = tagRepository.createTags(user.tags)

                // Then add the new ones in.
                ProfileTag.batchInsert(tags) {
                    this[ProfileTag.profile] = EntityID(userId, Profiles)
                    this[ProfileTag.tag] = EntityID(it.id!!, Tags)
                }
            }

            getByIds(listOf(userId)).getOrNull(0)
        }
    }

    override fun getAll(limit: Int?, from: Int?): List<User> {
        return transaction {
            val profiles = Profiles.selectAll()
            if (limit != null) profiles.limit(limit, offset = from ?: 0)
            getDependencies(profiles)
        }
    }

    override fun getByJob(jobs: Job): Set<User?> {
        return getByJob(listOf(jobs)).get(0)
    }

    override fun getByIds(ids: List<Int>): List<User?> {
        return transaction {
            val profiles = Profiles.select { Profiles.id inList ids }
            getDependencies(profiles).sortByListOrder(ids) { it.id }
        }
    }

    override fun getByEmails(emails: List<String>): List<User?> {
        return transaction {
            val lowerEmails = emails.map { it.toLowerCase() }
            val profiles = Profiles.select { Profiles.email inList lowerEmails }
            getDependencies(profiles).sortByListOrder(emails) { it.email }
        }
    }


    override fun getByJob(jobs: List<Job>) : List<Set<User?>> {
        return transaction {
            val tagIds = jobs.map { it.tags.map { it.id!! }.toSet() }
            tagIds.map {
                val userTag = ProfileTag.join(Profiles, JoinType.INNER, additionalConstraint = {Profiles.id eq ProfileTag.profile })
                    .select { ProfileTag.tag inList it }
                getDependencies(userTag).toSet()
            }
        }
    }

    override fun getFollowers(id: Int): List<User> = getFollowers(listOf(id)).getOrNull(0) ?: listOf()

    override fun getFollowers(id: List<Int>): List<List<User>> {
        return transaction {
            val profileIds = ProfileSubscriptions.select { ProfileSubscriptions.subscription inList id }
                .map { it[ProfileSubscriptions.profile].value to it[ProfileSubscriptions.subscription].value }
                .groupBy({it.second}, {it.first})

            val idsToGet = profileIds.values.flatten()

            val users = Profiles.select { Profiles.id inList idsToGet }
                .mapNotNull { it.toUser() }
                .groupBy { it.id!! }

            id.flatMap { id ->
                profileIds.getOrDefault(id, listOf()).mapNotNull { users[it] }
            }
        }
    }

    override fun getFollowing(id: Int): List<User> {
        return transaction {
            val profileIds = ProfileSubscriptions.select { ProfileSubscriptions.profile eq id }
                .mapNotNull { it[ProfileSubscriptions.subscription] }

            Profiles.select { Profiles.id inList profileIds }
                .mapNotNull { it.toUser() }
        }
    }

    override fun getFollowing(id: List<Int>): List<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribeUser(subscribeeId: Int, subscribedId: Int): Boolean {
        return transaction {
            ProfileSubscriptions.insert {
                it[profile] = EntityID(subscribeeId, Profiles)
                it[subscription] = EntityID(subscribedId, Profiles)
            }[ProfileSubscriptions.profile].value == subscribedId
        }
    }

    override fun unsubscribeUser(subscribeeId: Int, subscribedId: Int): Boolean {
        return transaction {
            ProfileSubscriptions.deleteWhere {
                (ProfileSubscriptions.profile eq subscribeeId) and
                (ProfileSubscriptions.subscription eq subscribedId)
            } == 1
        }
    }

    override fun updateCV(userId: Int, cvJson: String): CVDto {
        return transaction {
            CV.update({ CV.profileId eq userId }) {
                it[content] = cvJson
            }
            Gson().fromJson(cvJson, CVDto::class.java)
        }
    }

    override fun getCV(id: Int): CVDto? {
        return transaction {
            CV.select {
                CV.profileId eq id
            }
                .mapNotNull { it.toCV() }
                .firstOrNull()
        }
    }
}
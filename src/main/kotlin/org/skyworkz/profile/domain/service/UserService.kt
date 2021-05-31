package org.skyworkz.profile.domain.service
import org.mindrot.jbcrypt.BCrypt
import org.skyworkz.profile.domain.exception.DuplicateUserException
import org.skyworkz.profile.domain.exception.InvalidAccessException
import org.skyworkz.profile.domain.exception.InvalidLoginException
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IUserRepository
import org.skyworkz.profile.messages.IEventBroker
import org.skyworkz.profile.messages.events.Event.UserCreated


class UserService(private val userRepository: IUserRepository,
                  private val eventBroker: IEventBroker
) {

    private fun createAndPropagateEntry(user: User) : User {
        var createdUser : User? = null
        try {
            createdUser = userRepository.create(user)
            eventBroker.commit(UserCreated(user))
        } catch (e : Exception) {
            createdUser?.let {
                userRepository.delete(it.id!!)
            }

            throw e
        }

        return createdUser!!
    }

    fun create(user: User) : User? {
        getByEmail(user.email)?.apply {
            throw DuplicateUserException()
        }

        return createAndPropagateEntry(user)
    }

    fun getByEmail(email : String) : User? {
        return userRepository.getByEmails(listOf(email)).getOrNull(0)
    }


    fun authenticate(email: String, password: String) : User? {
        getByEmail(email)?.let {
            //if (!it.approved) throw InvalidAccessException()
            return if (BCrypt.checkpw(password, it.password)) it else throw InvalidLoginException()
        }

        throw InvalidLoginException()
    }


}
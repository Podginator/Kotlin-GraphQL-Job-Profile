package org.skyworkz.profile.web.graphQL.data.fetchers

import graphql.schema.DataFetcher
import org.dataloader.DataLoader
import org.skyworkz.profile.domain.exception.InvalidAccessException
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IUserRepository
import org.skyworkz.profile.web.graphQL.context.AuthContext

class UserDataFetcher(private val userRepository: IUserRepository) {
    val getUserById = DataFetcher {
        val id = it.getArgument<Int>("id")

        val dataLoader: DataLoader<Int, Any> = it.getDataLoader("user")
        dataLoader.load(id)
    }

    val getMe = DataFetcher {
        val authContext : AuthContext = it.getContext()

        if (authContext.user == null) {
            throw InvalidAccessException()
        }

        val dataLoader: DataLoader<Int, Any> = it.getDataLoader("user")
        dataLoader.load(authContext.user.id!!)
    }


    val getAllUsers = DataFetcher {
        val limit = it.getArgument<Int?>("limit")
        val offset = it.getArgument<Int?>("from")

        userRepository.getAll(limit = limit, from = offset)
    }

    val getByEmail = DataFetcher {
        val id = it.getArgument<String>("email")

        val dataLoader: DataLoader<String, Any> = it.getDataLoader("userEmails")
        dataLoader.load(id)
    }

    val getUsersFromJob = DataFetcher {
        val job = it.getSource<Job>()
        val dataLoader: DataLoader<Job, Any> = it.getDataLoader("jobUsers")

        dataLoader.load(job)
    }

    val getFollowersFromUser = DataFetcher {
        val user = it.getSource<User>()
        userRepository.getFollowers(user.id!!)
    }

    val getFollowingFromUser = DataFetcher {
        val user = it.getSource<User>()
        userRepository.getFollowing(user.id!!)
    }

    val getCV = DataFetcher {
        val user = it.getSource<User>()
        userRepository.getCV(user.id!!)
    }

}
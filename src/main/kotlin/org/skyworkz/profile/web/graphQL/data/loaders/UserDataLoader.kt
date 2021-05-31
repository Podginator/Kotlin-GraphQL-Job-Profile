package org.skyworkz.profile.web.graphQL.data.loaders

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IUserRepository
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class UserDataLoader(private val userRepository: IUserRepository) {

    fun getUserByIdLoader() = DataLoader.newDataLoader(BatchLoader<Int, User> {
        CompletableFuture.supplyAsync { -> userRepository.getByIds(it) } as CompletionStage<MutableList<User>>
    })

    fun getUserByEmailLoader() = DataLoader.newDataLoader(BatchLoader<String, User> {
        CompletableFuture.supplyAsync { -> userRepository.getByEmails(it) } as CompletionStage<MutableList<User>>
    })

    fun getUserByJobLoader() = DataLoader.newDataLoader(BatchLoader<Job, User> {
        CompletableFuture.supplyAsync { -> userRepository.getByJob(it) } as CompletionStage<MutableList<User>>
    })

    fun getUserFollowers() = DataLoader.newDataLoader(BatchLoader<Job, User> {
        CompletableFuture.supplyAsync { -> userRepository.getByJob(it) } as CompletionStage<MutableList<User>>
    })
}
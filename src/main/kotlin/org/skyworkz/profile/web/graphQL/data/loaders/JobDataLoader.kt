package org.skyworkz.profile.web.graphQL.data.loaders

import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.skyworkz.profile.domain.model.Job
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IJobRepository
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class JobDataLoader(private val jobRepository: IJobRepository) {
    fun getJobsByUserDataLoader() = DataLoader.newDataLoader(BatchLoader<User, Job> {
        CompletableFuture.supplyAsync { -> jobRepository.getByUsers(it) } as CompletionStage<List<Job>>
    })

    fun getJobsByIdDataLoader() = DataLoader.newDataLoader(BatchLoader<Int, Job> {
        CompletableFuture.supplyAsync { -> jobRepository.getByIds(it) } as CompletionStage<MutableList<Job>>
    })
}
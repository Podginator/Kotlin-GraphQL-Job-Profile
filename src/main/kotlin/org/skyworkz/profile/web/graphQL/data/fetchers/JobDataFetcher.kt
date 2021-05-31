package org.skyworkz.profile.web.graphQL.data.fetchers

import graphql.schema.DataFetcher
import org.dataloader.DataLoader
import org.skyworkz.profile.domain.model.Tag
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.repository.IJobRepository

class JobDataFetcher(private val jobRepository: IJobRepository) {

    val getAllJobs = DataFetcher {
        val limit = it.getArgument<Int?>("limit")
        val offset = it.getArgument<Int?>("from")

        jobRepository.getAll(limit = limit, from = offset)
    }

    val getJobCount = DataFetcher {
        jobRepository.size()
    }

    val getJobsByUser = DataFetcher {
        val job = it.getSource<User>()
        val dataLoader: DataLoader<User, Any> = it.getDataLoader("userJobs")

        dataLoader.load(job)
    }

    val getJobById = DataFetcher {
        val id = it.getArgument<Int>("id")

        val dataLoader: DataLoader<Int, Any> = it.getDataLoader("job")
        dataLoader.load(id)
    }

    val getJobsByTag = DataFetcher {
        val tag = it.getSource<Tag>()
        jobRepository.getByTag(tag.id!!)
    }

}
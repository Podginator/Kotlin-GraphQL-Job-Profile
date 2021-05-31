package org.skyworkz.profile.web.graphQL

import com.google.common.io.Resources
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import org.skyworkz.profile.web.graphQL.data.fetchers.JobDataFetcher
import org.skyworkz.profile.web.graphQL.data.fetchers.TagDataFetcher
import org.skyworkz.profile.web.graphQL.data.fetchers.UserDataFetcher
import org.skyworkz.profile.web.graphQL.data.mutations.JobMutations
import org.skyworkz.profile.web.graphQL.data.mutations.TagMutations
import org.skyworkz.profile.web.graphQL.data.mutations.UserMutations


class GraphQLConfig(private val userDataFetcher: UserDataFetcher,
                    private val jobDataFetcher: JobDataFetcher,
                    private val tagRepository: TagDataFetcher,
                    private val jobMutations : JobMutations,
                    private val userMutations: UserMutations,
                    private val tagMutations: TagMutations
) {
    private fun loadSchema() = Resources.getResource("skyworkz.graphqls").readText()

    private fun buildRuntimeWiring() : RuntimeWiring {
        return newRuntimeWiring()
            .type("Query") { typeWiring  ->
                typeWiring.dataFetcher("user", userDataFetcher.getUserById)
                typeWiring.dataFetcher("me", userDataFetcher.getMe)
                typeWiring.dataFetcher("users", userDataFetcher.getAllUsers)
                typeWiring.dataFetcher("userByEmail", userDataFetcher.getByEmail)
                typeWiring.dataFetcher("jobs", jobDataFetcher.getAllJobs)
                typeWiring.dataFetcher("jobCount", jobDataFetcher.getJobCount)
                typeWiring.dataFetcher("job", jobDataFetcher.getJobById)
                typeWiring.dataFetcher("tags", tagRepository.getTags)
                typeWiring.dataFetcher("tag", tagRepository.getTagById)
            }
            .type("Mutation") { typeWiring ->
                typeWiring.dataFetcher("createJob", jobMutations.createJob)
                typeWiring.dataFetcher("createJobs", jobMutations.createJobs)
                typeWiring.dataFetcher("deleteJob", jobMutations.deleteJob)
                typeWiring.dataFetcher("updateUser", userMutations.updateUser)
                typeWiring.dataFetcher("deleteUser", userMutations.deleteProfile)
                typeWiring.dataFetcher("createTags", tagMutations.createTags)
                typeWiring.dataFetcher("subscribeToUser", userMutations.subscribeToProfile)
                typeWiring.dataFetcher("unsubscribeToUser", userMutations.unsubscribeToProfile)
                typeWiring.dataFetcher("updateCV", userMutations.createCV)
            }
            .type("Job") { typeWiring ->
                typeWiring.dataFetcher("appropriateProfiles", userDataFetcher.getUsersFromJob)
            }
            .type("Tag")  { typeWiring ->
                typeWiring.dataFetcher("jobs", jobDataFetcher.getJobsByTag)
            }
            .type("User") { typeWiring ->
                typeWiring.dataFetcher("appropriateJobs", jobDataFetcher.getJobsByUser)
                typeWiring.dataFetcher("following", userDataFetcher.getFollowingFromUser)
                typeWiring.dataFetcher("followers", userDataFetcher.getFollowersFromUser)
                typeWiring.dataFetcher("cv", userDataFetcher.getCV)
            }
            .build()
    }

    fun getGraphQL() : GraphQL {
        val schemaParser = SchemaParser()
        val schemaGenerator = SchemaGenerator()
        val schemaString: String = loadSchema()

        val typeRegistry = schemaParser.parse(schemaString)
        val wiring = buildRuntimeWiring()
        val schema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring)

        return GraphQL.newGraphQL(schema).build()
    }

}
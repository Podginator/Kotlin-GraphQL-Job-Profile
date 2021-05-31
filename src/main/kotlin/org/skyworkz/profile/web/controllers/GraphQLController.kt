package org.skyworkz.profile.web.controllers

import graphql.ExecutionInput
import graphql.GraphQL
import io.javalin.core.security.Role
import io.javalin.http.Context
import org.dataloader.DataLoaderRegistry
import org.skyworkz.profile.domain.model.User
import org.skyworkz.profile.domain.model.request.GraphQLRequest
import org.skyworkz.profile.web.graphQL.context.AuthContext
import org.skyworkz.profile.web.graphQL.data.loaders.JobDataLoader
import org.skyworkz.profile.web.graphQL.data.loaders.UserDataLoader


class GraphQLController(private val graphQL : GraphQL,
                        private val userLoader: UserDataLoader,
                        private val jobLoader: JobDataLoader
) {


    private fun createExecutionInput(graphQLRequest : GraphQLRequest, ctx : Context) : ExecutionInput {
        val registry = DataLoaderRegistry()
        registry.register("user", userLoader.getUserByIdLoader())
        registry.register("job", jobLoader.getJobsByIdDataLoader())
        registry.register("userEmails", userLoader.getUserByEmailLoader())
        registry.register("jobUsers", userLoader.getUserByJobLoader())
        registry.register("userJobs", jobLoader.getJobsByUserDataLoader())


        val authedUser = ctx.attribute<User?>("user")
        val userRole = ctx.attribute<Role>("role")
        val authContext = AuthContext(authedUser, userRole!!)

        return ExecutionInput.newExecutionInput()
            .dataLoaderRegistry(registry)
            .context(authContext)
            .query(graphQLRequest.query)
            .variables(graphQLRequest.variables ?: emptyMap())
            .build()
    }

    fun post (ctx: Context) {
        ctx.bodyAsClass(GraphQLRequest::class.java).let {
            val executionInput = createExecutionInput(it, ctx)
            ctx.json(graphQL.execute(executionInput))
        }

    }
}
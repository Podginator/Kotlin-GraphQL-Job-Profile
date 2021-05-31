package org.skyworkz.profile.domain.model.request

data class GraphQLRequest
    (
        val query: String,
        val operationName: String?,
        val variables: Map<String, Any>?
    )

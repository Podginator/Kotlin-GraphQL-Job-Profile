package org.skyworkz.profile.web.graphQL.data.mutations

import graphql.schema.DataFetcher
import org.skyworkz.profile.domain.model.request.UserUpdate
import org.skyworkz.profile.domain.repository.IUserRepository
import org.skyworkz.profile.web.graphQL.context.AuthContext

class UserMutations(private val userRepo: IUserRepository) {

    val updateUser = DataFetcher {
        val userUpdateMap = it.getArgument<Map<String, Any>>("userUpdate")
        val userId = it.getArgument<Int>("id")
        val update = UserUpdate.fromMap(userUpdateMap)
        userRepo.update(userId, update)
    }

    val deleteProfile = DataFetcher {
        val userId = it.getArgument<String>("id").toInt()
        userRepo.delete(userId)
    }

    val subscribeToProfile = DataFetcher {
        val userId = it.getArgument<Int>("id")
        val authContext : AuthContext = it.getContext()
        val myId = authContext.user?.id!!

        userRepo.subscribeUser(myId, userId)
    }

    val unsubscribeToProfile = DataFetcher {
        val userId = it.getArgument<Int>("id")
        val authContext : AuthContext = it.getContext()
        val myId = authContext.user?.id!!

        userRepo.unsubscribeUser(myId, userId)
    }

    val createCV = DataFetcher {
        val cvContent = it.getArgument<Map<String, Any>>("cv")["json"] as String
        val authContext : AuthContext = it.getContext()
        val myId = authContext.user?.id!!

        userRepo.updateCV(myId, cvContent)!!
    }

}
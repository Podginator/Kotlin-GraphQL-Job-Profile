package org.skyworkz.profile.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.common.io.Resources
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.core.qualifier.Qualifier
import org.skyworkz.profile.util.Cipher
import org.skyworkz.profile.util.JWTProvider
import org.skyworkz.profile.web.Router
import org.skyworkz.profile.web.controllers.*
import org.skyworkz.profile.web.graphQL.GraphQLConfig
import org.skyworkz.profile.web.handlers.LoggingHandler
import org.koin.dsl.module
import org.skyworkz.profile.domain.repository.*
import org.skyworkz.profile.domain.service.UserService
import org.skyworkz.profile.messages.CoroutineEventBroker
import org.skyworkz.profile.messages.IEventBroker
import org.skyworkz.profile.messages.processors.EventProcessor
import org.skyworkz.profile.messages.processors.IEventProcessor
import org.skyworkz.profile.messages.processors.JobAppropriateProfileExtractor
import org.skyworkz.profile.messages.processors.notifications.EmailNotifier
import org.skyworkz.profile.messages.processors.notifications.WebAppNotifier
import org.skyworkz.profile.web.graphQL.data.fetchers.JobDataFetcher
import org.skyworkz.profile.web.graphQL.data.fetchers.TagDataFetcher
import org.skyworkz.profile.web.graphQL.data.fetchers.UserDataFetcher
import org.skyworkz.profile.web.graphQL.data.loaders.JobDataLoader
import org.skyworkz.profile.web.graphQL.data.loaders.UserDataLoader
import org.skyworkz.profile.web.graphQL.data.mutations.JobMutations
import org.skyworkz.profile.web.graphQL.data.mutations.TagMutations
import org.skyworkz.profile.web.graphQL.data.mutations.UserMutations
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.utils.StringInputStream
import java.io.FileInputStream

object KoinConfig {

    private val eventModule = module {
        single { NotificationRepository(
            DynamoDbClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .build()
            ) as INotificationRepository
        }
        single {
            val firebaseOptions = StringInputStream(System.getenv("FIREBASE_CREDS"))
            val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseOptions))
                .build()

            val firebaseApp = FirebaseApp.initializeApp(options)
            FirebaseMessaging.getInstance(firebaseApp)
        }
        single { WebAppNotifier(get(), get()) }
        single { SesClient.builder().region(Region.of(System.getenv("AWS_REGION"))).build()}
        single { EmailNotifier(get()) }
        single { JobAppropriateProfileExtractor(get()) }
        single { EventProcessor(get(), get(), get()) as IEventProcessor}
        single { CoroutineEventBroker(get()) }
    }

    private val jwtModule = module {
        single { JWTProvider(Cipher.algorithm) }
        single { AuthConfig(get()) }
    }

    private val graphQLModule = module {
        single { JobDataFetcher(get()) }
        single { JobMutations(get(), get<CoroutineEventBroker>()) }
        single { UserMutations(get()) }
        single { TagMutations(get()) }
        single { JobDataLoader(get()) }
        single { UserDataFetcher(get()) }
        single { UserDataLoader(get()) }
        single { TagDataFetcher(get()) }
        single { GraphQLConfig(get(), get(), get(), get(), get(), get()).getGraphQL() }
    }

    private val controllerModules = module {
        single { TagRepository() as ITagRepository}
        single { UserRepository(get()) as IUserRepository }
        single { UserService(get(), get<CoroutineEventBroker>()) }
        single { JobRepository(get()) as IJobRepository }
        single { GraphQLController(get(), get(), get()) }
        single { WebNotificationController(get())}
        single { AuthController(get(), get())}
    }

    private val healthController = module {
        single { HealthController() }
    }

    private val middlewares = module {
        single { LoggingHandler() }
    }

    private val configModule = module {
        single { AppConfig() }
        single { Router(get(), get(), get(), get(), get())}
    }

    internal val allModules = listOf(jwtModule, controllerModules, configModule, healthController, middlewares, eventModule, graphQLModule)
}

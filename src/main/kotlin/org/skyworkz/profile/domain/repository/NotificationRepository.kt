package org.skyworkz.profile.domain.repository

import org.skyworkz.profile.messages.processors.notifications.NotificationInformation
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest


class NotificationRepository(private val dynamoDb: DynamoDbClient) : INotificationRepository {

    private val dynamoDbTableName = System.getenv("DYNAMODB_TABLE_NAME")

    override fun upsert(notification: NotificationInformation): NotificationInformation {
        val putItemRequest = PutItemRequest.builder()
            .item(mapOf(
                "id" to AttributeValue.builder().n(notification.id.toString()).build(),
                "userAgent" to AttributeValue.builder().s(notification.userAgent).build(),
                "token" to AttributeValue.builder().s(notification.token).build()
            )
            )
            .tableName(dynamoDbTableName)
            .build()


        dynamoDb.putItem(putItemRequest)
        return notification
    }

    override fun getById(id: Int): List<NotificationInformation> {
        val getItemRequest = QueryRequest
            .builder()
            .tableName(dynamoDbTableName)
            .keyConditionExpression("id = :id")
            .expressionAttributeValues(mapOf(":id" to AttributeValue.builder().n(id.toString()).build()))
            .build()

        val items = dynamoDb.query(getItemRequest)


        return items.items().map {
            NotificationInformation(id = it["id"]?.n()?.toInt()!!, userAgent = it["userAgent"]?.s()!!, token = it["token"]?.s()!!)
        }
    }
}
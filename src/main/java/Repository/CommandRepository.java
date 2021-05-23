package Repository;


import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Model.Account;
import Model.Request.CommandRequest;
import Model.Response.CommandResponse;
import Exception.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.inject.Inject;
import java.security.SecureRandom;
import java.util.*;

import static Util.Constants.*;

public class CommandRepository {

    private DynamoDbClient dynamoDb;
    @Inject
    ObjectMapper objectMapper;

    private final AccountComponent accountComponent;


    public CommandRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDb = dynamoDbClient;
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }

    private Map<String, AttributeValue> generateCreateEventItem(CommandRequest commandRequest, Context context) throws JsonProcessingException {

        Map<String, AttributeValue> item = new HashMap<>();
        LambdaLogger logger = context.getLogger();
        String accountId = generateAccountId();
        item.put("eventName", AttributeValue.builder().s("accountCreated").build());
        item.put("timestamp", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());
        item.put("eventBody", AttributeValue.builder().s(objectMapper.writeValueAsString(new Account(accountId,commandRequest.getAccountVO()))).build());
        item.put("Account_Id", AttributeValue.builder().s(accountId).build());
        return item;
    }

    private String generateAccountId() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        String id = String.format("%05d", num);
        return id;
    }


    public CommandResponse generateCreateEvent(CommandRequest commandRequest, Context context) throws JsonProcessingException {

        context.getLogger().log("Json : " + objectMapper.writeValueAsString(commandRequest));
        Map<String, AttributeValue> item = generateCreateEventItem(commandRequest, context);


        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(DB_EVENT_TABLE)
                .item(item)
                .conditionExpression("attribute_not_exists(Aggregate_ID)")
                .build());


        return CommandResponse.builder()
                .timestamp(Long.valueOf(item.get("timestamp").n()))
                .accountId(item.get("Account_Id").s())
                .message("Account created successfully")
                .build();
    }


    public CommandResponse generateUpdateEvent(CommandRequest updateRequest, Context context, String accountId) throws AccountDoesNotExistException, TableDoesNotExistException {
        Map<String, AttributeValue> updateItem;
        try {
            boolean itemToUpdate = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName(DB_QUERY_TABLE)
                    .key(Collections.singletonMap("Account_Id",
                            AttributeValue.builder()
                                    .s(accountId)
                                    .build()))
                    .build())
                    .hasItem();

            if (!itemToUpdate) {
                throw new AccountDoesNotExistException("Account " + accountId + " does not exist");
            }

            updateItem = generateUpdateItemEvent(updateRequest, accountId);
            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName(DB_EVENT_TABLE)
                    .item(updateItem)
                    .build());

        } catch (ResourceNotFoundException | JsonProcessingException e) {
            throw new TableDoesNotExistException("Query table " + DB_QUERY_TABLE + " does not exist");
        }


        return CommandResponse.builder()
                .timestamp(Long.valueOf(updateItem.get("timestamp").n()))
                .accountId(accountId)
                .message("Account updated successfully")
                .build();
    }

    private Map<String, AttributeValue> generateUpdateItemEvent(CommandRequest updateRequest, String accountId) throws JsonProcessingException {

        Map<String, AttributeValue> item = new HashMap<>();

        item.put("eventName", AttributeValue.builder().s("accountUpdated").build());
        item.put("Account_Id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
        item.put("timestamp", AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());
        item.put("eventBody", AttributeValue.builder().s(objectMapper.writeValueAsString(new Account(accountId, updateRequest.getAccountVO()))).build());


        return item;

    }

    public CommandResponse generateDeleteEvent(Context context, String deleteAccountId) throws AccountDoesNotExistException, TableDoesNotExistException {
        Map<String,AttributeValue> deleteItem;
        try {
            boolean itemToDelete = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName(DB_QUERY_TABLE)
                    .key(Collections.singletonMap("Account_Id",
                            AttributeValue.builder()
                                    .s(deleteAccountId)
                                    .build()))
                    .build())
                    .hasItem();

            if (!itemToDelete) {
                throw new AccountDoesNotExistException("Account " + deleteAccountId + " does not exist");
            }
            deleteItem = new HashMap<>();
            deleteItem.put("eventName",AttributeValue.builder().s("accountDeleted").build());
            deleteItem.put("timestamp",AttributeValue.builder().n(String.valueOf(System.currentTimeMillis())).build());
            deleteItem.put("Account_Id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
            deleteItem.put("eventBody",AttributeValue.builder().s(objectMapper.writeValueAsString(new Account(deleteAccountId,null))).build());

            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName(DB_EVENT_TABLE)
                    .item(deleteItem)
                    .build());

        } catch (ResourceNotFoundException | JsonProcessingException e) {
            throw new TableDoesNotExistException("Query table " + DB_QUERY_TABLE + " does not exist");
        }

        return CommandResponse.builder()
                .timestamp(Long.valueOf(deleteItem.get("timestamp").n()))
                .accountId(deleteAccountId)
                .message("Account deleted successfully")
                .build();
    }
}

package Config;

import Repository.AccountModelRepository;
import Repository.CommandRepository;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import lambda.QueryHandlerFunction;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import javax.inject.Singleton;

@Module
public class AccountModule {

    @Singleton
    @Provides
    AmazonSQS sqs(){
        return AmazonSQSClientBuilder.standard().build();
    }


    @Singleton
    @Provides
    DynamoDbClient dynamoDb() {

        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        builder.httpClient(ApacheHttpClient.builder().build());

        return builder.build();
    }

    @Singleton
    @Provides
    DynamoDBMapper dynamoDBMapper(){
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
        return new DynamoDBMapper(dynamoDB);
    }

    @Singleton
    @Provides
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Singleton
    @Provides
    public CommandRepository commandRepository(DynamoDbClient dynamoDb) {
        return new CommandRepository(dynamoDb);
    }

    @Singleton
    @Provides
    public AccountModelRepository accountModelRepository(DynamoDBMapper dynamoDBMapper){

        return new AccountModelRepository(dynamoDBMapper);
    }


}

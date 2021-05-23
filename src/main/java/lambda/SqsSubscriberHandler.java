package lambda;

import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Model.Account;
import Model.Event;
import Repository.AccountModelRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.inject.Inject;

import static Util.Constants.*;


public class SqsSubscriberHandler implements RequestHandler<SQSEvent,Void> {
    @Inject
    ObjectMapper objectMapper ;
    @Inject
    AccountModelRepository repository;
    @Inject
    AmazonSQS sqs;

    private final AccountComponent accountComponent;

    public SqsSubscriberHandler(){
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }


    @SneakyThrows
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        LambdaLogger logger = context.getLogger();

        for(SQSMessage message : sqsEvent.getRecords()){
            logger.log("message recieved from sqs : "+message.getBody());


            Event event = objectMapper.treeToValue(
                    objectMapper.readTree(message.getBody()), Event.class);
            logger.log(event.toString());

            sqs.deleteMessage(EVENT_QUEUE_URL,message.getReceiptHandle());


            Account account = objectMapper.readValue(event.getBody(),Account.class);
            logger.log("Account : "+account);
            switch(event.getEventName()){
                case ACCOUNT_CREATED_EVENT:
                    logger.log("Saving account in query database  ");
                    repository.saveAccount(account,context,event.getTimeStamp());
                    break;

                case ACCOUNT_UPDATED_EVENT:
                    repository.updateAccount(account,context,event.getTimeStamp());
                    break;

                case ACCOUNT_DELETED_EVENT:
                    repository.deleteAccount(account.getAccountId(),context,event.getTimeStamp());
                    break;
            }
        }
        return null;
    }
}

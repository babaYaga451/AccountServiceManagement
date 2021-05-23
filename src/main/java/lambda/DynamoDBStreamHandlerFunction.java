package lambda;


import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Model.Event;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.inject.Inject;
import java.util.Map;

import static Util.Constants.EVENT_QUEUE;


public class DynamoDBStreamHandlerFunction implements RequestHandler<DynamodbEvent, Void> {

    @Inject
    AmazonSQS sqs;
    @Inject
    ObjectMapper objectMapper;

    private final AccountComponent accountComponent;

    public DynamoDBStreamHandlerFunction(){
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }

    @SneakyThrows
    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        Map<String,AttributeValue> item;
        LambdaLogger logger = context.getLogger();
        for(DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            Event event = new Event();
            item = record.getDynamodb().getNewImage();
            logger.log("eventName : " + item.get("eventName").getS());
            event.setEventName(item.get("eventName").getS());
            logger.log("timestamp : " + record.getDynamodb().getApproximateCreationDateTime().toString());
            event.setTimeStamp(record.getDynamodb().getApproximateCreationDateTime().toString());
            event.setEventId(record.getEventID());
            logger.log(record.getEventID());
            event.setBody(item.get("eventBody").getS());
            logger.log(item.get("eventBody").getS());

//            try {
//                File file = new File("s3://raghua-bucket/events.txt");
//                FileOutputStream fileOutputStream = new FileOutputStream
//                        (file);
//
//                ObjectOutputStream out = new ObjectOutputStream
//                        (fileOutputStream);
//                out.writeObject(event);
//                out.close();
//                fileOutputStream.close();
//            } catch (IOException ex) {
//                logger.log("Exception occured while serialization of event object " + ex.getMessage());
//            }
            String queueUrl = sqs.getQueueUrl(EVENT_QUEUE).getQueueUrl();

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(objectMapper.writeValueAsString(event))
                    .withDelaySeconds(5);
            sqs.sendMessage(send_msg_request);

            logger.log("Message sent to queue : "+objectMapper.writeValueAsString(event));
        }
            return null;
    }
}

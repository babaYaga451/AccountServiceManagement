package lambda;

import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Repository.CommandRepository;
import Model.Request.CommandRequest;
import Model.Response.CommandResponse;
import Model.Response.ErrorMessage;
import Model.Response.GatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import Exception.*;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;


import static Util.Constants.*;


public class CommandHandlerFunction implements CommandRequestStreamHandler {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    CommandRepository commandRepository;

    private final AccountComponent accountComponent;

    public CommandHandlerFunction(){
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output,
                              Context context) throws IOException {
        final JsonNode event;

        LambdaLogger logger = context.getLogger();
        try {
            event = objectMapper.readTree(input);
        } catch (JsonMappingException e) {
            writeInvalidJsonInStreamResponse(objectMapper, output, e.getMessage());
            return;
        }
        if (event == null) {
            writeInvalidJsonInStreamResponse(objectMapper, output, "event was null");
            return;
        }
        JsonNode requestBody = event.findValue("body");
        if (requestBody == null) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(
                                    new ErrorMessage("Body was null",
                                            STATUS_BAD_REQUEST)),
                            APPLICATION_JSON, STATUS_BAD_REQUEST));
            return;
        }
        final String method = event.findValue("httpMethod").asText();

        String command = "deleteAccount";
        CommandRequest request = null;

        if(!method.equals("DELETE")) {

            try {
                request = objectMapper.treeToValue(
                        objectMapper.readTree(requestBody.asText()), CommandRequest.class);
                logger.log("Request in JsonString : " + objectMapper.writeValueAsString(request));
            } catch (JsonParseException | JsonMappingException e) {
                objectMapper.writeValue(output,
                        new GatewayResponse<>(
                                objectMapper.writeValueAsString(
                                        new ErrorMessage("Invalid JSON in body: "
                                                + e.getMessage(), STATUS_BAD_REQUEST)),
                                APPLICATION_JSON, STATUS_BAD_REQUEST));
                return;
            }
            command = request.getCommandName();
            logger.log("Command : " + command);
        }

        switch(command){
                case CREATE_ACCOUNT:
                    try{
                        CommandResponse commandResponse = commandRepository.generateCreateEvent(request,context);
                        logger.log("Generating create account event");
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString(commandResponse),
                                        APPLICATION_JSON,STATUS_CREATED
                                ));
                    }catch(CouldNotCreateAccountException e){
                            objectMapper.writeValue(output,
                                    new GatewayResponse<>(
                                            objectMapper.writeValueAsString(
                                                    new ErrorMessage(e.getMessage(),
                                                            STATUS_INTERNAL_SERVER_ERROR)),
                                            APPLICATION_JSON, STATUS_INTERNAL_SERVER_ERROR));
                    }
                    break;
                case UPDATE_ACCOUNT:
                    final JsonNode pathParameterMap = event.findValue("pathParameters");
                    final String accountId = Optional.ofNullable(pathParameterMap)
                            .map(mapNode -> mapNode.get("id"))
                            .map(JsonNode::asText)
                            .orElse(null);
                    logger.log("accountId : "+accountId);
                    if (isNullOrEmpty(accountId)) {
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString("Account id not provided"),
                                        APPLICATION_JSON, STATUS_BAD_REQUEST));
                        return;
                    }
                    try{
                        CommandResponse commandResponse = commandRepository.generateUpdateEvent(request,context,accountId);
                        logger.log("Generating account update event");
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString(commandResponse),
                                        APPLICATION_JSON,STATUS_CREATED
                                ));
                    }catch(AccountDoesNotExistException | TableDoesNotExistException e){
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString(
                                                new ErrorMessage(e.getMessage(),
                                                        STATUS_INTERNAL_SERVER_ERROR)),
                                        APPLICATION_JSON, STATUS_INTERNAL_SERVER_ERROR));
                    }
                    break;
                case DELETE_ACCOUNT:
                    final JsonNode deletePathParameterMap = event.findValue("pathParameters");
                    final String deleteAccountId = Optional.ofNullable(deletePathParameterMap)
                            .map(mapNode -> mapNode.get("id"))
                            .map(JsonNode::asText)
                            .orElse(null);
                    logger.log("DeleteAccountId : "+deleteAccountId);
                    if (isNullOrEmpty(deleteAccountId)) {
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString("Account id not provided"),
                                        APPLICATION_JSON, STATUS_BAD_REQUEST));
                        return;
                    }
                    try{
                        CommandResponse commandResponse = commandRepository.generateDeleteEvent(context,deleteAccountId);
                        logger.log("Generating account delete event");
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString(commandResponse),
                                        APPLICATION_JSON,STATUS_CREATED
                                ));
                    }catch(AccountDoesNotExistException | TableDoesNotExistException e){
                        objectMapper.writeValue(output,
                                new GatewayResponse<>(
                                        objectMapper.writeValueAsString(
                                                new ErrorMessage(e.getMessage(),
                                                        STATUS_INTERNAL_SERVER_ERROR)),
                                        APPLICATION_JSON, STATUS_INTERNAL_SERVER_ERROR));
                    }
                    break;
        }
    }
}

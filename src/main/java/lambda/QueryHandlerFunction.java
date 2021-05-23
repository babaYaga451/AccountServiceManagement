package lambda;

import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Model.AccountModel;
import Model.Response.GatewayResponse;
import Repository.AccountModelRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static Util.Constants.*;

public class QueryHandlerFunction implements CommandRequestStreamHandler {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    AccountModelRepository accountModelRepository;

    private final AccountComponent accountComponent;

    public QueryHandlerFunction(){
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        final JsonNode event;

        LambdaLogger logger = context.getLogger();
        logger.log(objectMapper.toString());
        try {
            event = objectMapper.readTree(input);
            logger.log("Event : "+event);
        } catch (JsonMappingException e) {
            writeInvalidJsonInStreamResponse(objectMapper, output, e.getMessage());
            return;
        }
        if (event == null) {
            writeInvalidJsonInStreamResponse(objectMapper, output, "event was null");
            return;
        }
        final JsonNode pathParameterMap = event.findValue("pathParameters");
        final String path = Optional.ofNullable(pathParameterMap)
                .map(JsonNode::fieldNames)
                .get().next();



        logger.log("PATH : "+path);

        if (isNullOrEmpty(path)) {
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString("Account id not provided"),
                            APPLICATION_JSON, STATUS_BAD_REQUEST));
            return;
        }

        if(path.equals("countryCode")){
            final String countryCode = Optional.ofNullable(pathParameterMap)
                    .map(mapNode -> mapNode.get("countryCode"))
                    .map(JsonNode::asText)
                    .orElse(null);
            logger.log("countryCode : "+countryCode);
            if (isNullOrEmpty(countryCode)) {
                objectMapper.writeValue(output,
                        new GatewayResponse<>(
                                objectMapper.writeValueAsString("Account id not provided"),
                                APPLICATION_JSON, STATUS_BAD_REQUEST));
                return;
            }
            List<AccountModel> result = accountModelRepository.queryUsingCountryCode(countryCode);
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(result),
                            APPLICATION_JSON,STATUS_OK
                    ));
        }
        if(path.equals("id")){
            final String accountId = Optional.of(pathParameterMap)
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

            AccountModel result = accountModelRepository.getAccount(accountId);
            objectMapper.writeValue(output,
                    new GatewayResponse<>(
                            objectMapper.writeValueAsString(result),
                            APPLICATION_JSON,STATUS_OK
                    ));
        }

    }
}

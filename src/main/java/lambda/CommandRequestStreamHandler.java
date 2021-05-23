package lambda;

import Model.Response.ErrorMessage;
import Model.Response.GatewayResponse;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

import static Util.Constants.*;

public interface CommandRequestStreamHandler extends com.amazonaws.services.lambda.runtime.RequestStreamHandler {

    ErrorMessage REQUEST_WAS_NULL_ERROR
            = new ErrorMessage("Request was null", STATUS_BAD_REQUEST);


    default void writeInvalidJsonInStreamResponse(ObjectMapper objectMapper,
                                                  OutputStream output,
                                                  String details) throws IOException {
        objectMapper.writeValue(output, new GatewayResponse<>(
                objectMapper.writeValueAsString(new ErrorMessage("Invalid JSON in body: "
                        + details,
                        STATUS_BAD_REQUEST))
                        ,APPLICATION_JSON
                        ,STATUS_BAD_REQUEST));
    }

    default boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty();
    }
}

package Model.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public class GatewayResponse<T>{

    private final T body;
    private final Map<String,String> headers;
    private final int statusCode;

    public GatewayResponse(final T body,final Map<String,String> headers, final int statusCode){
        this.body = body;
        this.headers = headers;
        this.statusCode = statusCode;
    }
}

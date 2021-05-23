package Model.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
@JsonAutoDetect
public class ErrorMessage {
    private final String message;
    private final int statusCode;
}

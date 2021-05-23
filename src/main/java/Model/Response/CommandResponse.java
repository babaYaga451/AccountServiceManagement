package Model.Response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommandResponse {

    private String accountId;
    private Long timestamp;
    private String message;

}

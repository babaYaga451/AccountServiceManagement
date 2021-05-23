package Model.Request;

import Model.AccountVO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommandRequest  {

    private String commandName;
    @JsonSerialize
    private AccountVO accountVO;

}

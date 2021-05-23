package Model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect
@ToString
public class Account {

    private String accountId;
    private AccountVO accountVO;

}

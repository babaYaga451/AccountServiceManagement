package Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect
@ToString
public class AccountVO {

    private String userName;
    private String countryCode;
    @JsonSerialize
    private Address address;

}

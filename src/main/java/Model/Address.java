package Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter
@Setter
@ToString
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String city;
    private String street;
    private String pinCode;
}

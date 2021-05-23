package Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonAutoDetect
@ToString
public class Event {

    //private static final long serialversionUID = 129348938L;

    private String eventName;
    private String timeStamp;
    private String eventId;
    private String body;

}

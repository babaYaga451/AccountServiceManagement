package translator;

import Model.Address;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class AddressTranslator implements DynamoDBTypeConverter<String, Address> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(Address address) {
        String jsonString = null;
        try{
            jsonString = objectMapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public Address unconvert(String s) {
        Address address = null;
        try {
            address = objectMapper.readValue(s,Address.class);
        } catch (JsonParseException | JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}

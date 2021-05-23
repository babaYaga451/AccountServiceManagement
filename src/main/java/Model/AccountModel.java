package Model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;
import translator.AddressTranslator;

@ToString
@NoArgsConstructor
@Getter
@Setter
@DynamoDBTable(tableName = "query_model")
public class AccountModel {

    @DynamoDBHashKey(attributeName = "Account_Id")
    private String accountId;

    @DynamoDBIndexRangeKey(attributeName = "username" , globalSecondaryIndexName = "countryIndex")
    private String username;

    @DynamoDBIndexHashKey(attributeName = "countryCode" ,globalSecondaryIndexName = "countryIndex")
    private String countryCode;

    @DynamoDBAttribute(attributeName = "address")
    @DynamoDBTypeConverted(converter = AddressTranslator.class)
    private Address address;

    @DynamoDBVersionAttribute(attributeName = "version")
    private Long version;
    @DynamoDBAttribute(attributeName = "timestamp")
    private String timestamp;
    @DynamoDBAttribute(attributeName = "active")
    private boolean isDeleted;





}

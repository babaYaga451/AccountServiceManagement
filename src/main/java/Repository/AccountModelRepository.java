package Repository;

import Config.AccountComponent;
import Config.DaggerAccountComponent;
import Model.Account;
import Model.AccountModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.List;


public class AccountModelRepository {

    DynamoDBMapper dynamoDBMapper;
    private AccountComponent accountComponent ;

    public AccountModelRepository(DynamoDBMapper dynamoDBMapper){
        this.dynamoDBMapper = dynamoDBMapper;
        accountComponent = DaggerAccountComponent.builder().build();
        accountComponent.inject(this);
    }

    public void deleteAccount(String deleteId, Context context, String timeStamp) {

        LambdaLogger logger = context.getLogger();
        AccountModel model = new AccountModel();
        model.setAccountId(deleteId);

        AccountModel deleteAccount = dynamoDBMapper.load(model);
        deleteAccount.setTimestamp(timeStamp);
        deleteAccount.setDeleted(true);
        try {
            dynamoDBMapper.save(deleteAccount);
            logger.log("Deleted account : "+deleteAccount.toString());
        } catch (ConditionalCheckFailedException e) {
            logger.log("Error : "+e.getMessage());
        }


    }

    public void saveAccount(Account account, Context context, String timeStamp) {
        LambdaLogger logger = context.getLogger();
        logger.log("creating new account..");

        AccountModel model = new AccountModel();
        model.setAccountId(account.getAccountId());
        model.setCountryCode(account.getAccountVO().getCountryCode());
        model.setTimestamp(timeStamp);
        model.setUsername(account.getAccountVO().getUserName());
        model.setDeleted(false);
        model.setAddress(account.getAccountVO().getAddress());



        try {
            dynamoDBMapper.save(model);
            logger.log("Saved account to db : "+model.toString());
        } catch (ConditionalCheckFailedException e) {
            logger.log("Error : "+e.getMessage());
        }
    }

    public void updateAccount(Account account, Context context, String timeStamp) {
        LambdaLogger logger = context.getLogger();
        AccountModel model = new AccountModel();
        model.setAccountId(account.getAccountId());

        AccountModel updateAccount = dynamoDBMapper.load(model);
        updateAccount.setCountryCode(account.getAccountVO().getCountryCode());
        updateAccount.setTimestamp(timeStamp);
        updateAccount.setUsername(account.getAccountVO().getUserName());
        updateAccount.setDeleted(false);
        updateAccount.setAddress(account.getAccountVO().getAddress());

        try {
            dynamoDBMapper.save(updateAccount);
            logger.log("Updated account : "+updateAccount.toString());
        } catch (ConditionalCheckFailedException e) {
            logger.log("Error : "+e.getMessage());
        }
    }

    public List<AccountModel> queryUsingCountryCode(String countryCode) {
        AccountModel model = new AccountModel();
        model.setCountryCode(countryCode);
        DynamoDBQueryExpression<AccountModel> queryExpression =
                new DynamoDBQueryExpression<AccountModel>()
                .withHashKeyValues(model)
                .withIndexName("countryIndex")
                .withConsistentRead(false);

        List<AccountModel> result = dynamoDBMapper.query(AccountModel.class,queryExpression);

        return result;
    }

    public AccountModel getAccount(String accountId) {
        AccountModel model = new AccountModel();
        model.setAccountId(accountId);
        return dynamoDBMapper.load(model);
    }
}

package Config;


import Repository.AccountModelRepository;
import Repository.CommandRepository;
import dagger.Component;
import lambda.CommandHandlerFunction;
import lambda.DynamoDBStreamHandlerFunction;
import lambda.QueryHandlerFunction;
import lambda.SqsSubscriberHandler;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AccountModule.class})
public interface AccountComponent {

    void inject(CommandHandlerFunction requestHandler);

    void inject(CommandRepository commandRepository);

    void inject(SqsSubscriberHandler subscriberHandler);

    void inject(DynamoDBStreamHandlerFunction streamHandlerFunction);

    void inject(AccountModelRepository accountModelRepository);

    void inject(QueryHandlerFunction queryHandlerFunction);
}

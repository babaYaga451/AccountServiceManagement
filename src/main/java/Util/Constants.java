package Util;

import java.util.Collections;
import java.util.Map;

public class Constants {

    public static final String UPDATE_EXPRESSION
            = "SET accountId = :aid, username = :pre ADD version :o";
    public static final String ACCOUNT_ID = "accountId";
    public static final String COMMAND_ID = "commandId";
    public static final String USERNAME_WAS_NULL = "username was null";
    public static final String COUNTRY_CODE_WAS_NULL = "country code was null";
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final Map<String, String> APPLICATION_JSON = Collections.singletonMap("Content-Type",
            "application/json");
    public static final String DB_EVENT_TABLE = "event_model";
    public static final String DB_QUERY_TABLE = "query_model";
    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String UPDATE_ACCOUNT = "updateAccount";
    public static final String DELETE_ACCOUNT = "deleteAccount";
    public static final String ACCOUNT_CREATED_EVENT = "accountCreated";
    public static final String ACCOUNT_UPDATED_EVENT = "accountUpdated";
    public static final String ACCOUNT_DELETED_EVENT = "accountDeleted";
    public static final String EVENT_QUEUE = "EventsQueue";
    public static final String EVENT_QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/724160787723/EventsQueue";

}

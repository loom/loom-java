package io.loom.eventsourcing.amazon;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;

import static com.amazonaws.services.dynamodbv2.util.TableUtils.deleteTableIfExists;

abstract class LocalDynamoDB {
    private LocalDynamoDB() { }

    private static final AmazonDynamoDB client = createClient();
    private static final DynamoDBMapper mapper = createMapper();

    private static AmazonDynamoDB createClient() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:8000", "ap-northeast-2"))
                .build();
    }

    private static DynamoDBMapper createMapper() {
        return new DynamoDBMapper(
                client, new DynamoDBMapperConfig.Builder().build());
    }

    static {
        deleteStreamEventsTableIfExists();
        createStreamEventsTable();
    }

    private static void deleteStreamEventsTableIfExists() {
        final DeleteTableRequest request =
                getMapper().generateDeleteTableRequest(StreamEvent.class);
        deleteTableIfExists(client, request);
    }

    private static void createStreamEventsTable() {
        client.createTable(getMapper()
                .generateCreateTableRequest(StreamEvent.class)
                .withBillingMode(BillingMode.PAY_PER_REQUEST));
    }

    static DynamoDBMapper getMapper() {
        return mapper;
    }
}

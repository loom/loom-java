package io.loom.eventsourcing.amazon;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.UUID;

@DynamoDBTable(tableName = "StreamEvents")
public class StreamEvent {
    private UUID streamId;
    private long version;
    private String eventType;
    private String eventData;
    private Long concurrencyVersion;

    @DynamoDBHashKey(attributeName = "StreamId")
    public UUID getStreamId() {
        return streamId;
    }

    public void setStreamId(UUID streamId) {
        this.streamId = streamId;
    }

    @DynamoDBRangeKey(attributeName = "Version")
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @DynamoDBAttribute(attributeName = "EventType")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @DynamoDBAttribute(attributeName = "EventData")
    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    @DynamoDBVersionAttribute(attributeName = "ConcurrencyVersion")
    public Long getConcurrencyVersion() {
        return concurrencyVersion;
    }

    public void setConcurrencyVersion(Long concurrencyVersion) {
        this.concurrencyVersion = concurrencyVersion;
    }
}

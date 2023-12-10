package adaper;

import com.google.gson.*;
import enums.Status;

import java.lang.reflect.Type;


public class StatusAdapter implements JsonSerializer<Status>, JsonDeserializer<Status> {

    @Override
    public JsonElement serialize(Status src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(String.valueOf(src));
    }

    @Override
    public Status deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Status status;
        switch (json.getAsString()) {
            case "IN_PROGRESS": {
                status = Status.IN_PROGRESS;
                break;
            }
            case "DONE": {
                status = Status.DONE;
                break;
            }
            default: {
                status = Status.NEW;
                break;
            }
        }
        return status;
    }
}
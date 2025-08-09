package org.tbk.lad.lnaddress.spi.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * e.g.
 * {
 * "callback": "https://legend.lnbits.com/lnurlp/api/v1/lnurl/cb/xxx",
 * "commentAllowed": 250, (optional)
 * "minSendable": 1000,
 * "maxSendable": 1000000000,
 * "metadata": "[[\"text/plain\", \"Personal lnurlp for user\"]]",
 * "tag": "payRequest"
 * }
 * <p>
 * See <a href="https://github.com/lnurl/luds/blob/luds/12.md">LUD-12: Comments in payRequest</a>
 */
@Value
@Builder
@Jacksonized
public class LnurlPayCallbackData {
    @NonNull
    String callback;

    @NonNull
    Long maxSendable;

    @NonNull
    Long minSendable;

    @Singular("addMetadata")
    @JsonSerialize(using = MetadataSerializer.class)
    @JsonDeserialize(using = MetadataDeserializer.class)
    List<List<String>> metadata;

    @NonNull
    @Builder.Default
    String tag = "payRequest";

    @Nullable
    Integer commentAllowed;

    public int getCommentAllowed() {
        return commentAllowed == null ? 0 : commentAllowed;
    }

    public static class MetadataSerializer extends JsonSerializer<List<List<String>>> {

        @Override
        public void serialize(List<List<String>> value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            String listAsString = "[%s]".formatted(value.stream()
                    .map(arr -> arr.stream()
                            .map("\"%s\""::formatted)
                            .collect(Collectors.joining(","))
                    )
                    .map("[%s]"::formatted)
                    .collect(Collectors.joining(",")));

            jgen.writeString(listAsString);
        }
    }

    public static class MetadataDeserializer extends JsonDeserializer<List<List<String>>> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public List<List<String>> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return objectMapper.readValue(p.getText(), new TypeReference<>() {
            });
        }
    }
}

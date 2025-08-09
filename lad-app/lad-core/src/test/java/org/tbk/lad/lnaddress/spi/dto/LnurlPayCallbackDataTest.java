package org.tbk.lad.lnaddress.spi.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LnurlPayCallbackDataTest {

    @Test
    void itShouldSerializeDataCorrectly() throws JsonProcessingException {
        LnurlPayCallbackData data = LnurlPayCallbackData.builder()
                .callback("https://www.example.org")
                .addMetadata(List.of("1", "1"))
                .addMetadata(List.of("2", "2"))
                .addMetadata(List.of("3", "3"))
                .tag("any")
                .minSendable(1L)
                .maxSendable(-1L)
                .commentAllowed(21)
                .build();

        String json = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .withFeatures(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(data);

        assertThat(json, is("""
                {
                  "callback" : "https://www.example.org",
                  "maxSendable" : -1,
                  "minSendable" : 1,
                  "metadata" : "[[\\"1\\",\\"1\\"],[\\"2\\",\\"2\\"],[\\"3\\",\\"3\\"]]",
                  "tag" : "any",
                  "commentAllowed" : 21
                }"""));
    }
}
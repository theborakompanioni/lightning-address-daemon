package org.tbk.lad.lnaddress.spi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

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
@JsonDeserialize(builder = LnurlPayCallbackData.LnurlPayCallbackDataBuilder.class)
public class LnurlPayCallbackData {
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class LnurlPayCallbackDataBuilder {
    }

    @NonNull
    String callback;

    @NonNull
    Long maxSendable;

    @NonNull
    Long minSendable;

    @NonNull
    String metadata;

    @NonNull
    @Builder.Default
    String tag = "payRequest";

    @Nullable
    Integer commentAllowed;

    public int getCommentAllowed() {
        return commentAllowed == null ? 0 : commentAllowed;
    }
}

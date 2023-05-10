package org.tbk.lad.lnaddress.spi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * e.g.
 * {
 * "pr": string, // bech32-serialized lightning invoice
 * "routes": [], // an empty array
 * "disposable": boolean
 * "successAction": {
 * "description": "Thank you for your purchase. Here is your receipt",
 * "url": "https://ts.dergigi.com/i/xxx/receipt",
 * "tag":"url"
 * }
 * }
 * <p>
 * See <a href="https://github.com/lnurl/luds/blob/luds/11.md">LUD-11: Disposable and storeable payRequests.</a>
 * See also <a href="https://github.com/lnurl/luds/blob/luds/09.md">LUD-09: successAction field for payRequest.</a>
 */
@Value
@Builder
@JsonDeserialize(builder = LnurlPayInvoiceData.LnurlPayInvoiceDataBuilder.class)
public class LnurlPayInvoiceData {
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class LnurlPayInvoiceDataBuilder {
    }

    @NonNull
    String pr;

    @NonNull
    @Singular("route")
    List<Object> routes;

    @Nullable
    Boolean disposable;

    SuccessAction successAction;

    @Value
    @Builder
    @JsonDeserialize(builder = SuccessAction.SuccessActionBuilder.class)
    public static class SuccessAction {
        @JsonPOJOBuilder(withPrefix = "")
        public static final class SuccessActionBuilder {
        }

        String tag;
        String description;
        String url;
    }
}

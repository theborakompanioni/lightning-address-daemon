package org.tbk.lad.lnaddress.spi.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
@Jacksonized
public class LnurlPayInvoiceData {
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
    @Jacksonized
    public static class SuccessAction {
        String tag;
        String description;
        String url;
    }
}

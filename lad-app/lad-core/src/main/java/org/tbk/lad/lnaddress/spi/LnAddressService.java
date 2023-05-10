package org.tbk.lad.lnaddress.spi;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayCallbackData;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayInvoiceData;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface LnAddressService {

    Mono<LnurlPayCallbackData> toLnurlPayCallbackData(LnAddressParts lnAddressParts, LnAddressCallbackUrlResolver callbackUrlResolver);

    Mono<LnurlPayInvoiceData> toLnurlPayInvoiceData(LnurlPayCallbackData data, AmountAndComment amountAndComment);

    @Value
    @Builder
    class AmountAndComment {
        @NonNull
        Long amount;

        @Nullable
        String comment;

        public Optional<String> getComment() {
            return Optional.ofNullable(comment);
        }
    }
}

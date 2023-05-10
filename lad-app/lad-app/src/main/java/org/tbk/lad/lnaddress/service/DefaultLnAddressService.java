package org.tbk.lad.lnaddress.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.tbk.lad.lnaddress.spi.LnAddressCallbackUrlResolver;
import org.tbk.lad.lnaddress.spi.LnAddressService;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayCallbackData;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayInvoiceData;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
public class DefaultLnAddressService implements LnAddressService {
    public interface InvoiceProvider {
        String fetchInvoice(AmountAndComment amountAndComment) throws Exception;
    }

    @NonNull
    private final InvoiceProvider invoiceProvider;

    @Override
    public Mono<LnurlPayCallbackData> toLnurlPayCallbackData(LnAddressParts lnAddressParts, LnAddressCallbackUrlResolver callbackUrlResolver) {
        URI callbackUrl = callbackUrlResolver.resolveLnurlPayCallbackUrl(lnAddressParts);

        List<String> metadataList = List.of(
                String.format("[\"text/plain\",\"Deposit to %s\"]", lnAddressParts.getUsername()),
                String.format("[\"text/identifier\",\"%s\"]", lnAddressParts.getRaw())
        );

        return Mono.just(LnurlPayCallbackData.builder()
                .callback(callbackUrl.toString())
                .minSendable(1_000L)
                .maxSendable(100_000_000_000L)
                .commentAllowed(256)
                .metadata("[" + String.join(",", metadataList) + "]")
                .build());
    }

    @Override
    public Mono<LnurlPayInvoiceData> toLnurlPayInvoiceData(LnurlPayCallbackData data, AmountAndComment amountAndComment) {

        if (data.getCommentAllowed() <= 0 && amountAndComment.getComment().isPresent()) {
            throw new IllegalArgumentException("Comment not allowed");
        }
        if (amountAndComment.getAmount() > data.getMaxSendable() || amountAndComment.getAmount() < data.getMinSendable()) {
            throw new IllegalArgumentException("Illegal amount value: Must be between min and max.");
        }

        try {
            return Mono.just(LnurlPayInvoiceData.builder()
                    .pr(invoiceProvider.fetchInvoice(amountAndComment))
                    .successAction(LnurlPayInvoiceData.SuccessAction.builder()
                            .tag("message")
                            .description("Thank you")
                            .build())
                    .build());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

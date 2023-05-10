package org.tbk.lad.lnaddress.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tbk.lad.lnaddress.spi.LnAddressCallbackUrlResolver;
import org.tbk.lad.lnaddress.spi.LnAddressResolver;
import org.tbk.lad.lnaddress.spi.LnAddressService;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayCallbackData;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayInvoiceData;
import org.tbk.lad.lnaddress.LnAddressApplicationProperties;
import org.tbk.tor.hs.HiddenServiceDefinition;

import java.time.Duration;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tags({
        @Tag(name = "lnaddress")
})
@ApiResponses({
        @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "403", content = @Content),
})
public class LnAddressApi {
    @NonNull
    private final LnAddressApplicationProperties properties;

    @NonNull
    private final LnAddressResolver lnAddressResolver;

    @NonNull
    private final LnAddressService lnAddressService;

    @NonNull
    private final HiddenServiceDefinition onionServiceDefinition;

    @GetMapping(path = "/.well-known/lnurlp/{username}")
    public ResponseEntity<LnurlPayCallbackData> lnurlpUsername(@PathVariable("username") String username) {
        LnurlPayCallbackData result = toLnurlPayCallbackData(username);

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache()
                .noTransform()
                .mustRevalidate());

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    @GetMapping(path = "/api/v1/lnurl/pay/{username}")
    public ResponseEntity<LnurlPayInvoiceData> lnurlpCallback(@PathVariable("username") String username,
                                                              @RequestParam("amount") long millsatoshi,
                                                              @RequestParam(name = "comment", required = false) String comment) {
        LnurlPayCallbackData callbackResult = toLnurlPayCallbackData(username);

        LnurlPayInvoiceData result = lnAddressService.toLnurlPayInvoiceData(callbackResult, LnAddressService.AmountAndComment.builder()
                        .amount(millsatoshi)
                        .comment(comment)
                        .build())
                .blockOptional(Duration.ofSeconds(30))
                .orElseThrow(() -> new IllegalStateException("Remote timed out (invoice data)."));

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache()
                .noTransform()
                .mustRevalidate());

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    private LnurlPayCallbackData toLnurlPayCallbackData(String username) {
        String lnaddress = normalizeInternalLnAddress(username);

        LnAddressParts lnAddressParts = lnAddressResolver.resolveLnAddressParts(lnaddress);

        return lnAddressService.toLnurlPayCallbackData(lnAddressParts, createCallbackUrlResolver(lnAddressParts))
                .blockOptional(Duration.ofSeconds(30))
                .orElseThrow(() -> new IllegalStateException("Remote timed out (callback data)."));
    }

    private String normalizeInternalLnAddress(String username) {
        // TODO: take domain from Tor Hidden Service if it is empty
        //      otherwise throw exception, as it should not be taken from Host header
        String host = properties.getDomain().orElseGet(() -> {
            String vhost = onionServiceDefinition.getVirtualHostOrThrow();
            int port = onionServiceDefinition.getVirtualPort();
            return vhost + (port == 80 ? "" : ":" + port);
        });

        String address = String.format("%s@%s", username, host);
        LnAddressParts lnAddressParts = lnAddressResolver.resolveLnAddressParts(address);

        return String.format("%s@%s", lnAddressParts.getUsername(), lnAddressParts.getDomain());
    }

    private static LnAddressCallbackUrlResolver createCallbackUrlResolver(LnAddressParts lnAddressParts) {
        return parts -> WebMvcLinkBuilder.linkTo(
                        methodOn(LnAddressApi.class).lnurlpCallback(lnAddressParts.getUsername(), 0L, null)
                ).toUriComponentsBuilder()
                .replaceQueryParams(null)
                .build()
                .toUri();
    }
}
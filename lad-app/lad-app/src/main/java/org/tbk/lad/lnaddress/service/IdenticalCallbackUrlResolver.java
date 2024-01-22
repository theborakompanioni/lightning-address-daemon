package org.tbk.lad.lnaddress.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.tbk.lad.lnaddress.spi.LnAddressCallbackUrlResolver;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

import java.net.URI;

@RequiredArgsConstructor
public class IdenticalCallbackUrlResolver implements LnAddressCallbackUrlResolver {

    @NonNull
    private final HttpRequest request;

    @Override
    public URI resolveLnurlPayCallbackUrl(LnAddressParts parts) {
        return ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()).build().toUri();
    }

    // https://docs.spring.io/spring-hateoas/docs/current/reference/html/#server.link-builder.forwarded-headers
    // ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(methodOn(YourController.class).getSomeEntityMethod(parameterId, parameterTwoId))
    // WebMvcLinkBuilder.linkTo(Controller.class).slash("url").withSelfRel().getHref();
}
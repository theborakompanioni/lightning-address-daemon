package org.tbk.lad.lnaddress.service;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbk.lad.lnaddress.spi.LnAddressResolver;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

import java.util.Collections;
import java.util.Optional;

public class DefaultLnAddressResolver implements LnAddressResolver {
    @Override
    public LnAddressParts resolveLnAddressParts(String raw) {
        if (!raw.contains("@")) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }
        if (raw.contains("?") || raw.contains("#")) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        int atSignIndex = raw.lastIndexOf("@");
        int lastPlusSignIndex = raw.lastIndexOf("+");

        String username = (lastPlusSignIndex <= 0 ? raw.substring(0, atSignIndex) : raw.substring(0, lastPlusSignIndex))
                .replace(".", "");

        String commentOrNull = Optional.of(lastPlusSignIndex <= 0 ? "" : raw.substring(lastPlusSignIndex + 1, atSignIndex))
                .filter(String::isBlank)
                .orElse(null);
        String domain = raw.substring(atSignIndex + 1);

        boolean isTor = domain.endsWith(".onion");
        String scheme = isTor || domain.startsWith("localhost:") ? "http" : "https";

        String sanitizedDomain = UriComponentsBuilder.fromHttpUrl("%s://%s".formatted(scheme, domain))
                .replacePath(null)
                .replaceQuery(null)
                .fragment(null)
                .build()
                .normalize()
                .toUri()
                .getAuthority();

        if ("".equals(username) || sanitizedDomain.length() <= 2) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        return LnAddressParts.builder()
                .raw(raw)
                .username(username)
                .comment(commentOrNull)
                .scheme(scheme)
                .domain(sanitizedDomain)
                .isTor(isTor)
                .build();
    }
}

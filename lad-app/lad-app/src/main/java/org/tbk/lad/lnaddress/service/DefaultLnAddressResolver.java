package org.tbk.lad.lnaddress.service;

import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbk.lad.lnaddress.spi.LnAddressResolver;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

import java.util.Collections;
import java.util.Optional;

public class DefaultLnAddressResolver implements LnAddressResolver {
    @Override
    public LnAddressParts resolveLnAddressParts(@NonNull String raw) {
        if (!raw.contains("@")) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }
        if (raw.contains("?") || raw.contains("#")) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        int atSignIndex = raw.lastIndexOf("@");
        int firstPlusSignIndex = raw.indexOf("+");
        if (firstPlusSignIndex > atSignIndex) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        String username = (firstPlusSignIndex <= 0 ? raw.substring(0, atSignIndex) : raw.substring(0, firstPlusSignIndex))
                .replace(".", "");
        if (username.isBlank()) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        String domain = raw.substring(atSignIndex + 1);
        if (domain.isBlank()) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        String commentOrNull = Optional.of(firstPlusSignIndex <= 0 ? "" : raw.substring(firstPlusSignIndex + 1, atSignIndex))
                .filter(it -> !it.isBlank())
                .orElse(null);

        boolean isTor = domain.endsWith(".onion");
        boolean isLocal = "localhost".equals(domain) || domain.startsWith("localhost:");
        String scheme = isTor || isLocal ? "http" : "https";

        String sanitizedDomain = sanitizeDomain(domain, scheme);

        return LnAddressParts.builder()
                .raw(raw)
                .username(username)
                .comment(commentOrNull)
                .scheme(scheme)
                .domain(sanitizedDomain)
                .isTor(isTor)
                .build();
    }

    private static String sanitizeDomain(String domain, String scheme) {
        String sanitizedDomain = UriComponentsBuilder.fromHttpUrl("%s://%s".formatted(scheme, domain))
                .replacePath(null)
                .replaceQuery(null)
                .fragment(null)
                .build()
                .normalize()
                .toUri()
                .getAuthority();

        if (sanitizedDomain.length() <= 2) {
            throw new ConstraintViolationException("Invalid address", Collections.emptySet());
        }

        return sanitizedDomain;
    }
}

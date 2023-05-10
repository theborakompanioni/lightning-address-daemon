package org.tbk.lad.lnaddress.spi.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class LnAddressParts {
    @NonNull
    String raw;

    @NonNull
    String username;

    @NonNull
    String scheme;

    @NonNull
    String domain;

    @NonNull
    Boolean isTor;

    String comment;

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }
}

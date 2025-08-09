package org.tbk.lad.lnaddress.spi.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.annotation.Nullable;
import java.util.Optional;

@Value
@Builder
@Jacksonized
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

    @Nullable
    String tag;

    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    public String getIdentifier() {
        return "%s@%s".formatted(username, domain);
    }
}

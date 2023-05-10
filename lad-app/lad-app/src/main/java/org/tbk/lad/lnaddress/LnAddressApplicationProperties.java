package org.tbk.lad.lnaddress;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.lnaddress",
        ignoreUnknownFields = false
)
public class LnAddressApplicationProperties {
    private String domain;

    public Optional<String> getDomain() {
        return Optional.ofNullable(domain);
    }
}

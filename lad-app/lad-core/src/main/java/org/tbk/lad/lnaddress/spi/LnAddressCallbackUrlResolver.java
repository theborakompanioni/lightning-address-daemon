package org.tbk.lad.lnaddress.spi;

import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

import java.net.URI;

public interface LnAddressCallbackUrlResolver {

    URI resolveLnurlPayCallbackUrl(LnAddressParts parts);

}

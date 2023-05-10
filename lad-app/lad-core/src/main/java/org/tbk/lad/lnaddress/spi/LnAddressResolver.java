package org.tbk.lad.lnaddress.spi;

import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

public interface LnAddressResolver {

    LnAddressParts resolveLnAddressParts(String raw);

}

package org.tbk.lad.lnaddress;

import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbk.lad.lnaddress.service.DefaultLnAddressResolver;
import org.tbk.lad.lnaddress.service.DefaultLnAddressService;
import org.tbk.lad.lnaddress.spi.LnAddressResolver;
import org.tbk.lad.lnaddress.spi.LnAddressService;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LnAddressApplicationProperties.class)
class LnAddressConfig {

    @Bean
    LnAddressResolver lnAddressResolver() {
        return new DefaultLnAddressResolver();
    }

    @Bean
    LnAddressService lnAddressService(DefaultLnAddressService.InvoiceProvider lndInvoiceProvider) {
        return new DefaultLnAddressService(lndInvoiceProvider);
    }

    // TODO: currently hardcoded LND - make it work with other implementations also.
    @Bean
    DefaultLnAddressService.InvoiceProvider lndInvoiceProvider(SynchronousLndAPI lndApi) {
        return amountAndComment -> {
            AddInvoiceResponse addInvoiceResponse = lndApi.addInvoice(new Invoice(LightningApi.Invoice.newBuilder()
                    .setValueMsat(amountAndComment.getAmount())
                    .setMemo(amountAndComment.getComment().orElse(""))
                    .build()));
            return addInvoiceResponse.getPaymentRequest();
        };
    }
}

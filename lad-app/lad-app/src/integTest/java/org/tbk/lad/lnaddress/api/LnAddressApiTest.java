package org.tbk.lad.lnaddress.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import org.tbk.lad.lnaddress.spi.dto.LnurlPayCallbackData;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
@ActiveProfiles("test")
class LnAddressApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldPreparePayment() throws Exception {
        mockMvc.perform(get("/.well-known/lnurlp/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tag").value(is("payRequest")))
                .andExpect(jsonPath("callback").isString())
                .andExpect(jsonPath("minSendable").isNumber())
                .andExpect(jsonPath("maxSendable").isNumber())
                .andExpect(jsonPath("metadata").isString())
                .andExpect(jsonPath("commentAllowed").isNumber());
    }

    @Test
    void itShouldFetchInvoice() throws Exception {
        String body = mockMvc.perform(get("/.well-known/lnurlp/test")).andReturn()
                .getResponse().getContentAsString();

        LnurlPayCallbackData result = new ObjectMapper().readValue(body, LnurlPayCallbackData.class);

        String url = UriComponentsBuilder.fromUriString(result.getCallback())
                .queryParam("amount", result.getMinSendable())
                .build()
                .toUriString();

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("pr").value(startsWith("lnbcrt1")))
                .andExpect(jsonPath("routes").isArray())
                .andExpect(jsonPath("successAction").isMap())
                .andExpect(jsonPath("successAction.tag").value(is("message")))
                .andExpect(jsonPath("successAction.description").value(is("Thank you")));
    }
}

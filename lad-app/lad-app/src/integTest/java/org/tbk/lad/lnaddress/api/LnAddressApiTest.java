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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(
        // using a different working-directory prevents "tor binary is still in use" exception
        properties = "org.tbk.tor.working-directory=tor-working-dir-mockmvc"
)
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
@ActiveProfiles("test")
class LnAddressApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itShouldPreparePayment() throws Exception {
        mockMvc.perform(get("/.well-known/lnurlp/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("tag").value(is("payRequest")))
                .andExpect(jsonPath("callback").value(startsWith("http")))
                .andExpect(jsonPath("minSendable").isNumber())
                .andExpect(jsonPath("maxSendable").isNumber())
                .andExpect(jsonPath("metadata").value(both(startsWith("[[\"")).and(endsWith("\"]]"))))
                .andExpect(jsonPath("commentAllowed").isNumber());
    }

    @Test
    void itShouldPreparePaymentWithTag() throws Exception {
        String body = mockMvc.perform(get("/.well-known/lnurlp/test+my+tag")).andReturn()
                .getResponse().getContentAsString();

        LnurlPayCallbackData result = objectMapper.readValue(body, LnurlPayCallbackData.class);

        List<List<String>> metadata = result.getMetadata();
        assertThat(metadata, hasSize(3));
        assertThat(metadata.get(0).get(0), is("text/plain"));
        assertThat(metadata.get(0).get(1), is("Deposit to test"));
        assertThat(metadata.get(1).get(0), is("text/identifier"));
        assertThat(metadata.get(1).get(1), both(startsWith("test@")).and(endsWith(".onion")));
        assertThat(metadata.get(2).get(0), is("text/tag"));
        assertThat(metadata.get(2).get(1), is("my+tag"));
    }

    @Test
    void itShouldFetchInvoice() throws Exception {
        String body = mockMvc.perform(get("/.well-known/lnurlp/test")).andReturn()
                .getResponse().getContentAsString();

        LnurlPayCallbackData result = objectMapper.readValue(body, LnurlPayCallbackData.class);

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

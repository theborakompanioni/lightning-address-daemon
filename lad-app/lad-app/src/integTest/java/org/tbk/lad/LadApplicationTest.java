package org.tbk.lad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@ActiveProfiles("test")
class LadApplicationTest {

    @Autowired(required = false)
    private LadApplicationConfig ladApplicationConfig;

    @Test
    void contextLoads() {
        assertThat(ladApplicationConfig, is(notNullValue()));
    }

}

package org.tbk.lad;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.message.GetInfoResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Configuration(proxyBeanMethods = false)
class LadApplicationConfig implements InitializingBean {

    @Value("${app.home}")
    private String homeDir;

    @Override
    public void afterPropertiesSet() throws IOException {
        checkAppHomeDirectory();
    }

    @SuppressFBWarnings(
            value = "PATH_TRAVERSAL_IN",
            justification = "Path is controlled by operator not by user input."
    )
    private void checkAppHomeDirectory() throws IOException {
        log.debug("Checking home directory '{}'", homeDir);

        File dir = Files.createDirectories(Paths.get(homeDir)).toFile();
        if (!dir.exists()) {
            throw new IllegalStateException("App home directory does not exist and could not be created");
        }
        if (!dir.canRead()) {
            throw new IllegalStateException("Cannot read from app home directory. Please check file privileges on " + homeDir);
        }
        if (!dir.canWrite()) {
            throw new IllegalStateException("Cannot write to app home directory. Please check file privileges on " + homeDir);
        }
    }

    @Bean
    @Profile("!test")
    public ApplicationRunner mainRunner(SynchronousLndAPI lndApi) {
        return args -> {
            GetInfoResponse info = lndApi.getInfo();
            log.info("[lnd] identity_pubkey: {}", info.getIdentityPubkey());
            log.info("[lnd] alias: {}", info.getAlias());
            log.info("[lnd] version: {}", info.getVersion());
        };
    }
}

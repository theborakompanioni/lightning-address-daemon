[![Build](https://github.com/theborakompanioni/lightning-address-daemon/actions/workflows/build.yml/badge.svg)](https://github.com/theborakompanioni/lightning-address-daemon/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/release/theborakompanioni/lightning-address-daemon.svg?maxAge=3600)](https://github.com/theborakompanioni/lightning-address-daemon/releases/latest)
[![License](https://img.shields.io/github/license/theborakompanioni/lightning-address-daemon.svg?maxAge=2592000)](https://github.com/theborakompanioni/lightning-address-daemon/blob/master/LICENSE)


<p align="center">
    <img src="https://github.com/theborakompanioni/lightning-address-daemon/blob/master/docs/assets/images/logo.png" alt="Logo" width="255" />
</p>


⚡ lightning-address-daemon
===

Lightning Addresses over Tor.

e.g. zap@0123456789abcdef.onion

TODO:
- [ ] rename to lightning-address-daemon lad
- [ ] integration tests
- [ ] docs (logo, description, etc)
- [ ] multiple node implementations (currently only LND)
- [ ] cleanup dependencies
- [ ] configurable callback data (min, max, comment, etc.)
- [ ] rename files and dirs (e.g. database filename)
- [ ] error status on illegal args (e.g. amount lower than min, currently a 500)


## Run
```shell
./gradlew -p lad-app/lad-app bootRun --args='--spring.profiles.active=development'
```

```shell
 ▄▄▄     ▄▄▄▄▄▄▄ ▄▄▄▄▄▄
█   █   █       █      █
█   █   █   ▄   █  ▄    █
█   █   █  █▄█  █ █ █   █
█   █▄▄▄█       █ █▄█   █
█       █   ▄   █       █
█▄▄▄▄▄▄▄█▄▄█ █▄▄█▄▄▄▄▄▄█

INFO 48685 : Starting LadApplication using Java 18.0.2.1 with PID 48685
INFO 48685 : The following 1 profile is active: "development"
INFO 48685 : Starting Tor
INFO 48685 : Tomcat started on port(s): 8080 (http) with context path ''
INFO 48685 : Started LadApplication in 13.918 seconds (process running for 14.328)
INFO 48685 : [tor] virtual host: 012345...abcdef.onion
INFO 48685 : [tor] virtual port: 80
INFO 48685 : [tor] directory: /home/user/.lad-dev/tor-working-dir/spring_boot_app
INFO 48685 : [lnd] identity_pubkey: 012345...abcdef
INFO 48685 : [lnd] alias: tbk-lnd-example-application
INFO 48685 : [lnd] version: 0.16.1-beta commit=v0.16.1-beta
```

## Development

### Requirements
- java >=17

### Build
```shell script
./gradlew build -x test
```
 
### Test
```shell script
./gradlew test integrationTest --rerun-tasks
```

### Dependency Verification
Gradle is used for checksum and signature verification of dependencies.

```shell script
# write metadata for dependency verification
./gradlew --write-verification-metadata pgp,sha256 --export-keys
```

See [Gradle Userguide: Verifying dependencies](https://docs.gradle.org/current/userguide/dependency_verification.html)
for more information.

### Checkstyle
[Checkstyle](https://github.com/checkstyle/checkstyle) with adapted [google_checks](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
is used for checking Java source code for adherence to a Code Standard.

```shell script
# check for code standard violations with checkstyle
./gradlew checkstyleMain --rerun-tasks
```

### SpotBugs
[SpotBugs](https://spotbugs.github.io/) is used for static code analysis.

```shell script
# invoke static code analysis with spotbugs
./gradlew spotbugsMain --rerun-tasks
```


## Contributing
All contributions and ideas are always welcome. For any question, bug or feature request, 
please create an [issue](https://github.com/theborakompanioni/lightning-address-daemon/issues). 
Before you start, please read the [contributing guidelines](contributing.md).


## Resources

- Lightning Address: https://lightningaddress.com
- Lightning Network: https://lightning.network
- Bitcoin: https://bitcoin.org/en/getting-started
---
- LUD-16: https://github.com/lnurl/luds/blob/luds/16.md
- LUD-06: https://github.com/lnurl/luds/blob/luds/06.md
- LUD-09: https://github.com/lnurl/luds/blob/luds/09.md
- LUD-11: https://github.com/lnurl/luds/blob/luds/11.md
- LUD-12: https://github.com/lnurl/luds/blob/luds/12.md
- Spring Boot (GitHub): https://github.com/spring-projects/spring-boot
- jMolecules (GitHub): https://github.com/xmolecules/jmolecules
- sqlite (GitHub): https://github.com/xerial/sqlite-jdbc


## License

The project is licensed under the Apache License. See [LICENSE](LICENSE) for details.

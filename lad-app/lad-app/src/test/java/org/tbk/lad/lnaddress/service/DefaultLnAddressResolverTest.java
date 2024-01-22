package org.tbk.lad.lnaddress.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.tbk.lad.lnaddress.spi.dto.LnAddressParts;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class DefaultLnAddressResolverTest {

    private static final DefaultLnAddressResolver sut = new DefaultLnAddressResolver();

    @Test
    void shouldResolveStandardAddress() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("alice@example.com");
        assertThat(lnAddressParts.getComment()).isEmpty();
        assertThat(lnAddressParts.getDomain()).isEqualTo("example.com");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("alice@example.com");
        assertThat(lnAddressParts.getScheme()).isEqualTo("https");
    }

    @Test
    void shouldResolveAddressWithDots() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e@example.com");
        assertThat(lnAddressParts.getComment()).isEmpty();
        assertThat(lnAddressParts.getDomain()).isEqualTo("example.com");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e@example.com");
        assertThat(lnAddressParts.getScheme()).isEqualTo("https");
    }

    @Test
    void shouldResolveAddressWithComment() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e+c.o.m.m.e.n.t.1@example.com");
        assertThat(lnAddressParts.getComment()).isEqualTo(Optional.of("c.o.m.m.e.n.t.1"));
        assertThat(lnAddressParts.getDomain()).isEqualTo("example.com");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e+c.o.m.m.e.n.t.1@example.com");
        assertThat(lnAddressParts.getScheme()).isEqualTo("https");
    }

    @Test
    void shouldResolveAddressWithEmptyComment() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e+@example.com");
        assertThat(lnAddressParts.getComment()).isEmpty();
        assertThat(lnAddressParts.getDomain()).isEqualTo("example.com");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e+@example.com");
        assertThat(lnAddressParts.getScheme()).isEqualTo("https");

        LnAddressParts lnAddressParts2 = sut.resolveLnAddressParts("a.l.i.c.e+   @example.com");
        assertThat(lnAddressParts2.getComment()).isEmpty();
    }

    @Test
    void shouldResolveTorAddress() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e+c.o.m.m.e.n.t.2@example.onion");
        assertThat(lnAddressParts.getComment()).isEqualTo(Optional.of("c.o.m.m.e.n.t.2"));
        assertThat(lnAddressParts.getDomain()).isEqualTo("example.onion");
        assertThat(lnAddressParts.getIsTor()).isTrue();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e+c.o.m.m.e.n.t.2@example.onion");
        assertThat(lnAddressParts.getScheme()).isEqualTo("http");
    }

    @Test
    void shouldResolveLocalAddress() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e+c.o.m.m.e.n.t.3@localhost");
        assertThat(lnAddressParts.getComment()).isEqualTo(Optional.of("c.o.m.m.e.n.t.3"));
        assertThat(lnAddressParts.getDomain()).isEqualTo("localhost");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e+c.o.m.m.e.n.t.3@localhost");
        assertThat(lnAddressParts.getScheme()).isEqualTo("http");
    }

    @Test
    void shouldResolveLocalAddressWithPort() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("a.l.i.c.e+c.o.m.m.e.n.t.4@localhost:8080");
        assertThat(lnAddressParts.getComment()).isEqualTo(Optional.of("c.o.m.m.e.n.t.4"));
        assertThat(lnAddressParts.getDomain()).isEqualTo("localhost:8080");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("alice");
        assertThat(lnAddressParts.getRaw()).isEqualTo("a.l.i.c.e+c.o.m.m.e.n.t.4@localhost:8080");
        assertThat(lnAddressParts.getScheme()).isEqualTo("http");
    }

    @Test
    void shouldResolveAddressWithMultiplePlusSigns() {
        LnAddressParts lnAddressParts = sut.resolveLnAddressParts("bob+here+you+are@localhost:8080");
        assertThat(lnAddressParts.getComment()).isEqualTo(Optional.of("here+you+are"));
        assertThat(lnAddressParts.getDomain()).isEqualTo("localhost:8080");
        assertThat(lnAddressParts.getIsTor()).isFalse();
        assertThat(lnAddressParts.getUsername()).isEqualTo("bob");
        assertThat(lnAddressParts.getRaw()).isEqualTo("bob+here+you+are@localhost:8080");
        assertThat(lnAddressParts.getScheme()).isEqualTo("http");
    }

    @Test
    void shouldThrowErrorOnInvalidAddresses() {
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts(""));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("?"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("#"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts(" "));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("@"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("alice@"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("alice@   "));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("alice@ + "));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("@example.com"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("   @example.com"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts(" + @example.com"));
        assertThrowsExactly(ConstraintViolationException.class, () -> sut.resolveLnAddressParts("bob@example+com"));

        IllegalStateException ise = assertThrowsExactly(IllegalStateException.class, () -> sut.resolveLnAddressParts("bob@example com"));
        assertThat(ise.getMessage()).startsWith("Could not create URI object: Illegal character in authority at index");
    }
}

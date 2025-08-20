package com.example.user.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppUser getAppUserSample1() {
        return new AppUser()
            .id(1L)
            .login("login1")
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .phone("phone1")
            .role("role1")
            .cin("cin1")
            .address("address1")
            .municipalityId(1L);
    }

    public static AppUser getAppUserSample2() {
        return new AppUser()
            .id(2L)
            .login("login2")
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .phone("phone2")
            .role("role2")
            .cin("cin2")
            .address("address2")
            .municipalityId(2L);
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .login(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .role(UUID.randomUUID().toString())
            .cin(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .municipalityId(longCount.incrementAndGet());
    }
}

package com.rovi.policy_engine.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    @Override
    public String getEncodedPassword(String username) {
        return users.get(username);
    }

    @Override
    public void save(String username, String encodedPassword) {
        users.put(username, encodedPassword);
    }

    @Override
    public boolean exists(String username) {
        return users.containsKey(username);
    }
}

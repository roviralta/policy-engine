package com.rovi.policy_engine.repository;

public interface UserRepository {
    String getEncodedPassword(String username);
    void save(String username, String encodedPassword);
    boolean exists(String username);
}

package in.alexlu.billingsoftware.service;

import in.alexlu.billingsoftware.io.UserRequest;
import in.alexlu.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);
    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}

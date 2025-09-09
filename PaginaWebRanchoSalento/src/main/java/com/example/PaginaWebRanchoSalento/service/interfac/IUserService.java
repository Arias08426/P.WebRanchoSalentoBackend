package com.example.PaginaWebRanchoSalento.service.interfac;


import com.example.PaginaWebRanchoSalento.dto.LoginRequest;
import com.example.PaginaWebRanchoSalento.dto.Response;
import com.example.PaginaWebRanchoSalento.model.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);

}

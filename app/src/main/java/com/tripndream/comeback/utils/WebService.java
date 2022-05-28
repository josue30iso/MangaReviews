package com.tripndream.comeback.utils;

public class WebService {
    public static final String HOST = "192.168.100.210";

    /**************************************** REVIEWS **************************************************/
    public static final String URL_PUB_ADD = "http://"+HOST+"/comeback/api/public/publicaciones/addReview";
    public static final String URL_PUB_EDIT = "http://"+HOST+"/comeback/api/public/publicaciones/editReview";
    public static final String URL_PUB_DELETE = "http://"+HOST+"/comeback/api/public/publicaciones/delete";
    public static final String URL_PUB_LIST = "http://"+HOST+"/comeback/api/public/publicaciones/listFilter";
    /*************************************** USUARIOS **************************************************/
    public static final String URL_USER_REGISTER = "http://"+HOST+"/comeback/api/public/usuarios/register";
    public static final String URL_USER_LOGIN = "http://"+HOST+"/comeback/api/public/usuarios/login";
    /*************************************** COLONIAS ***************************************************/
    public static final String URL_COLONIAS = "http://"+HOST+"/comeback/api/public/colonias/listar";
}

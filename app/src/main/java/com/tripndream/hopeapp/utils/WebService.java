package com.tripndream.hopeapp.utils;

public class WebService {
    public static final String HOST = "192.168.100.20";

    /**************************************** REVIEWS **************************************************/
    public static final String URL_PUB_ADD = "http://"+HOST+"/haweb/api/public/publicaciones/addReview";
    public static final String URL_PUB_EDIT = "http://"+HOST+"/haweb/api/public/publicaciones/editReview";
    public static final String URL_PUB_LISTBYUSER = "http://"+HOST+"/haweb/api/public/publicaciones/listByUser";
    public static final String URL_PUB_DELETE = "http://"+HOST+"/haweb/api/public/publicaciones/delete";
    public static final String URL_PUB_LISTBYCAT = "http://"+HOST+"/haweb/api/public/publicaciones/listCat";
    /***************************************** ZONAS ***************************************************/
    public static final String URL_PUB_LISTALLZONES = "http://"+HOST+"/haweb/api/public/categorias/listar";
    /*************************************** USUARIOS **************************************************/
    public static final String URL_USER_REGISTER = "http://"+HOST+"/haweb/api/public/usuarios/register";
    public static final String URL_USER_LOGIN = "http://"+HOST+"/haweb/api/public/usuarios/login";
    /*************************************** ROUTES ***************************************************/
    public static final String URL_IMAGES = "http://"+HOST+"/haweb/img/RecipesImg/";
}

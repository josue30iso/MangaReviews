package com.tripndream.hopeapp.utils;

public class WebService {
    public static final String HOST = "192.168.100.210";

    /**************************************** REVIEWS **************************************************/
    public static final String URL_PUB_ADD = "http://"+HOST+"/haweb/api/public/publicaciones/addPublicacion";
    public static final String URL_PUB_SAVE = "http://"+HOST+"/haweb/api/public/publicaciones/guardarPublicacion";
    public static final String URL_PUB_EDIT = "http://"+HOST+"/haweb/api/public/publicaciones/editPublicacion";
    public static final String URL_PUB_LISTBYUSER = "http://"+HOST+"/haweb/api/public/publicaciones/listByUser";
    public static final String URL_PUB_DELETE = "http://"+HOST+"/haweb/api/public/publicaciones/delete";
    public static final String URL_PUB_LISTBYCAT = "http://"+HOST+"/haweb/api/public/publicaciones/listCat";
    public static final String URL_PUB_COMENTS = "http://"+HOST+"/haweb/api/public/publicaciones/obtenerComentarios";
    public static final String URL_PUB_SAVE_COMENTS = "http://"+HOST+"/haweb/api/public/publicaciones/guardarComentario";
    public static final String URL_PUB_VALIDAR = "http://"+HOST+"/haweb/api/public/publicaciones/validaPublicacion";
    public static final String URL_PUB_RECHAZAR = "http://"+HOST+"/haweb/api/public/publicaciones/rechazarPublicacion";
    /***************************************** ZONAS ***************************************************/
    public static final String URL_PUB_LISTALLZONES = "http://"+HOST+"/haweb/api/public/categorias/listar";
    /*************************************** USUARIOS **************************************************/
    public static final String URL_USER_REGISTER = "http://"+HOST+"/haweb/api/public/usuarios/register";
    public static final String URL_USER_LOGIN = "http://"+HOST+"/haweb/api/public/usuarios/login";
    public static final String URL_USER_RECOV = "http://"+HOST+"/haweb/api/public/usuarios/recovery";
    public static final String URL_USER_VERIF = "http://"+HOST+"/haweb/api/public/usuarios/verificar";
    public static final String URL_USER_CAMPAS = "http://"+HOST+"/haweb/api/public/usuarios/cambiarPasswd";
}

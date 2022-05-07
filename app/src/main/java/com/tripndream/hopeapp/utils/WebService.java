package com.tripndream.hopeapp.utils;

public class WebService {
    public static final String HOST = "192.168.100.200";

    /**************************************** REVIEWS **************************************************/
    public static final String URL_REVIEW_ADD = "http://"+HOST+"/haweb/api/public/reviews/addReview";
    public static final String URL_REVIEW_EDIT = "http://"+HOST+"/haweb/api/public/reviews/editReview";
    public static final String URL_REVIEW_LISTBYUSER = "http://"+HOST+"/haweb/api/public/reviews/listByUser";
    public static final String URL_REVIEW_DELETE = "http://"+HOST+"/haweb/api/public/reviews/delete";
    public static final String URL_REVIEW_LISTBYCAT = "http://"+HOST+"/haweb/api/public/reviews/listCat";
    /*************************************** USUARIOS **************************************************/
    public static final String URL_USER_REGISTER = "http://"+HOST+"/haweb/api/public/usuarios/register";
    public static final String URL_USER_LOGIN = "http://"+HOST+"/haweb/api/public/usuarios/login";
    /*************************************** ROUTES ***************************************************/
    public static final String URL_IMAGES = "http://"+HOST+"/haweb/img/RecipesImg/";
}

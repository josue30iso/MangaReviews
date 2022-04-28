package com.tripndream.mangareviews.utils;

public class WebService {
    public static final String HOST = "192.168.100.209";

    /**************************************** REVIEWS **************************************************/
    public static final String URL_REVIEW_ADD = "http://"+HOST+"/mrweb/api/public/reviews/addReview";
    public static final String URL_REVIEW_EDIT = "http://"+HOST+"/mrweb/api/public/reviews/editReview";
    public static final String URL_REVIEW_LISTBYUSER = "http://"+HOST+"/mrweb/api/public/reviews/listByUser";
    public static final String URL_REVIEW_DELETE = "http://"+HOST+"/mrweb/api/public/reviews/delete";
    public static final String URL_REVIEW_LISTBYCAT = "http://"+HOST+"/mrweb/api/public/reviews/listCat";
    /*************************************** USUARIOS **************************************************/
    public static final String URL_USER_REGISTER = "http://"+HOST+"/mrweb/api/public/usuarios/register";
    public static final String URL_USER_LOGIN = "http://"+HOST+"/mrweb/api/public/usuarios/login";
    /*************************************** ROUTES ***************************************************/
    public static final String URL_IMAGES = "http://"+HOST+"/mrweb/img/RecipesImg/";
}

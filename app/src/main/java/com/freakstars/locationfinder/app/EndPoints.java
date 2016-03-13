package com.freakstars.locationfinder.app;

/**
 * Created by Parth on 07-03-2016.
 */
public class EndPoints {
    public static final String BASE_URL="http://friendlocator.esy.es/gcm_chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String SEND_SINGLE_USER = BASE_URL + "/users/_ID_/message";
    public static final String APP_BASE_URL="http://friendlocator.esy.es/friendlocator/v1";
    public static final String APP_LOGIN=APP_BASE_URL +"/login";
    public static final String SIGNUP=APP_BASE_URL+"/register";
    public static final String LOGOUT=APP_BASE_URL+"/logout";

}



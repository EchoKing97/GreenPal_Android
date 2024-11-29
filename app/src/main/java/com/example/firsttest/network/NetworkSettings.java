package com.example.firsttest.network;

public class NetworkSettings {
    private static final String HOST = "10.0.2.2";
    private static final String PORT = "8080";
    public static final String SIGN_IN_UP = "http://"+ HOST +":"+PORT + "/api/users/register";
    public static final String LOGIN = "http://"+ HOST +":"+PORT + "/api/users/login";
    public static final String ADD_PLANT = "http://"+ HOST +":"+PORT + "/api/plants/add";
    public static final String GET_PLANT = "http://"+ HOST +":"+PORT + "/api/plants/user/plants";
    public static final String ADD_OPERATION = "http://"+ HOST +":"+PORT + "/api/operationRecords/add";
    public static final String GET_OPERATION = "http://"+ HOST +":"+PORT + "/api/operationRecords/recent";
    public static final String UPLOAD_USER_IMAGE = "http://"+ HOST +":"+PORT + "/api/user-image/upload";
    public static final String GET_USER_IMAGE = "http://"+ HOST +":"+PORT + "/api/user-image/get/";
}

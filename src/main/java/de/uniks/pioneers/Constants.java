package de.uniks.pioneers;

public class Constants {
    // application
    public static final String LOGIN_SCREEN_TITLE = "Pioneers - Login";
    public static final String SIGNUP_SCREEN_TITLE = "Pioneers - Create new account";
    public static final String LOBBY_SCREEN_TITLE = "Pioneers - Lobby";

    public static final String CHAT_SCREEN_TITLE = "Pioneers - Chat";

    // network
    public static final String BASE_URL = "https://pioneers.uniks.de";

    public static final String API_V1_PREFIX = "/api/v1";

    public static final String LOGIN_URL = BASE_URL + API_V1_PREFIX + "/auth/login";
    public static final String SIGN_UP_URL = BASE_URL + API_V1_PREFIX + "/users";


    public static final String JSON_NAME = "name";
    public static final String JSON_PASSWORD = "password";
    public static final String JSON_AVATAR = "avatar";

}

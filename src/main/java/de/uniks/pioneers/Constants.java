package de.uniks.pioneers;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

public class Constants {
    // application
    public static final String LOGIN_SCREEN_TITLE = "Pioneers - Login";
    public static final String SIGNUP_SCREEN_TITLE = "Pioneers - Create new account";
    public static final String LOBBY_SCREEN_TITLE = "Pioneers - Lobby";
    public static final String EDIT_PROFILE_SCREEN_TITLE = "Pioneers - Edit Profile";
    public static final String CHAT_SCREEN_TITLE = "Pioneers - Chat";
    public static final String RULES_SCREEN_TITLE = "Pioneers - Rules";
    public static final String NOOP = "noop";
    public static final String REMEMBER_ME = "RememberMe";

    // network
    public static final String BASE_URL = "https://pioneers.uniks.de";
    public static final String BASE_URL_WSS = "wss://pioneers.uniks.de";

    public static final int AVATAR_CHAR_LIMIT = 16384;

    public static final String API_V1_PREFIX = "/api/v1";
    public static final String WS_V1_PREFIX = "/ws/v1";
    public static final String EVENTS_AUTH_TOKEN = "/events?authToken=";

    public static final String LOGIN_URL = BASE_URL + API_V1_PREFIX + "/auth/login";
    public static final String SIGN_UP_URL = BASE_URL + API_V1_PREFIX + "/users";


    public static final String JSON_NAME = "name";
    public static final String JSON_PASSWORD = "password";
    public static final String JSON_AVATAR = "avatar";

    public static final String DEFAULT_AVATAR = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIEAAACBCAMAAADQfiliAAAAYFBMVEX///9UWV1JT1Onqarp6upZXmFRVlpCSE1MUlY/RUpITVL7+/vJysuztbZFS0/09PS8vb5fY2fY2dnP0NFrb3JkaWzj4+R/goVydnmFiIqLjpE0O0HDxcWVl5mgo6R3e31GjsV0AAADbklEQVR4nO2bCZajIBBAgxo2FxS3iYnd97/laL9JTLoTpEihPe/xL8APFFRZkMMhEAi8Saq6LI/jOM86lW4/vCrHlgkmZ5gQ7ViqLYdPsz9CckoWKJfiT7bVTKR5K+9Hv1nIKt/EoW7Zs/G/HFhbex9fncSr8b8cxMlzPGjGDePPcKl9CpRPA+B7OJT+BOJidfyZIvYl8MGsBAhhnhRKYSlAiPCyENpuCf4thIdwVNV6EC7QCn9TniKAACHRCVvgIkEChMgLssERsgYz9IgrUNpuxAWGuh/SFjoF0yS0mIkysz8KFkSGaNDDp2CahB5PIDk6CBByTNAMMngczjC8ZRjWioLn8AHNAHQgL9AKS0C5CUwKWMlBuy3CtAxYGfICS0oLEVZuKJ0NsA7mHJoXr8h8b4NodwO0OSid5wArDtz3AtaxvP95oBwFCEErmHfPC78gN+5fH+xfIx1OTnUi5mdT7VQrY/aU0sbhe6FBbazt/s30C74bDzWkfzFToHcWB1iClHin0RUF+nilrYfGpoYEo48+0nQ2n60FzpifzXfk1v1ErOrsp4JdNKKVh0+4vGzsL1CG3cN6oKNrNWPEO58CU4YYjdNA2ej/mqVuipd3LEXj/45lIi0r+axs47IqN7v1qwchvt21CTFs8vtvpNn4SYRgRVEwIcjnuNlV371E0ulLnucX3SU7DB8I/CrSmc0HTXRdxmPfVIReIVXTD3FZa9/bUiX1xzQyZ4XknD6ciZRzWTA+mXzUiafLb1UODRMRpcbcSGkkWDPgv8jQcXUuuG21THlxrmLEYjXJKx6Bv5kiXuU4xYoeyMuCYEVCkuF9B92vvnkwwVn/3mJ0/dn1buE2EefefR7UGL3z+2/zEI2OO0NXrr3U70SVU/kUGx/dwKAC/ipDNdCGgZmiAa5EQjAi4B5OQL09TfBW4AolgH3ZWZ+/IAX777nE4vvUSYFZLgTszQtIwfJ9zIB1DPzErr8FePgEx+apVIK9DR/h66HQezZYfZgB6tm5wNZOBacXJxDWJqGz7xm6cjafS6PfKJjho0lAOdxkQKHGLKl9ngVXhCkWY9c7bgjSVK143wkzxrdaDg/gHAxaQyB6y4oPBoYM6Xi3CsVwFxsMgkEwCAbBIBgEg2AQDJ4Y0GgL6GsDFW/Dpn9IDQT+F/4C0Q86zlXYUp4AAAAASUVORK5CYII=";

    public static final int MAX_OPEN_CHATS = 3;

    public static final int MAX_LOADING_CHATS = 2;

    public static final int OPEN_CHATS_COUNTER_MAX_VALUE = 4;

    public static final String LOADING_CHAT_TEXT = "Loading...";

    public static final String DELETE_MESSAGE_TEXT = "This message was deleted";

    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);

    public static final byte[] encryptKeySeed = "UGk8LmWKk2WXPEQGJAsOzOYUMfSjasH3".getBytes();

}

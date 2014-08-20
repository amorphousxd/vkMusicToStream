package com.company;

/**
 * Created by Pavel on 25.07.14.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class VkApi extends Application {

    private String client_id = "4475346"; //"2971510"; //4475346
    private String scope = "messages,status,wall";
    private String redirect_uri = "https://oauth.vk.com/blank.html";
    private String display = "popup";
    private String response_type = "token";
    private static String access_token;

    private Scene scene;

    @Override
    public void start(final Stage stage) throws Exception {
        String url = "https://oauth.vk.com/authorize?" +
                "client_id="+client_id+
                "&scope="+scope+
                "&redirect_uri="+redirect_uri+
                "&display="+display+
                "&v=5.23"+
                "&response_type="+response_type;
        BorderPane borderPane = new BorderPane();

        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();

        webEngine.load(url);
        borderPane.setCenter(browser);

        webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            public void handle(WebEvent<String> event) {
                if (event.getSource() instanceof WebEngine) {
                    WebEngine we = (WebEngine) event.getSource();
                    String location = we.getLocation();
                    if (location.startsWith(redirect_uri) && location.contains("access_token")) {
                        try {
                            URL url = new URL(location);
                            String[] params = url.getRef().split("&");
                            Map<String, String> map = new HashMap<String, String>();
                            for (String param : params) {
                                String name = param.split("=")[0];
                                String value = param.split("=")[1];
                                map.put(name, value);
                            }
                            System.out.println("The access token: "+map.get("access_token"));
                            access_token = map.get("access_token");
                            stage.hide();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // create scene
        stage.setTitle("VkAuthorization");
        scene = new Scene(borderPane, 750, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static String getStatus() {
        String url = "https://api.vk.com/method/"+
                "status.get"+
                "?out=0"+
                "&access_token="+access_token;
        String line = "";
        try {
            URL url2 = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream()));
            line = reader.readLine();
            reader.close();

        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        }

        Gson gson = new Gson();
        String status = line;
        JsonElement jsonElement = gson.toJsonTree(status);
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(status);
        JsonObject response = o.getAsJsonObject("response");

        return response.get("text").toString();
    }

    public static String getNewMessage() throws IOException, NoSuchAlgorithmException, URISyntaxException {
        //Ранее описанный код получения списка сообщений
        //формируем строку запроса
        String url = "https://api.vk.com/method/"+
                "messages.get"+
                "?out=0"+
                "&access_token="+access_token;
        String line = "";
        try {
            URL url2 = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream()));
            line = reader.readLine();
            reader.close();

        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        }

        return line;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
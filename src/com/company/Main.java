package com.company;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jndi.url.dns.dnsURLContext;
import jdk.nashorn.api.scripting.JSObject;
import midi.Drum;
import midi.Tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Pavel on 25.07.14.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException, AWTException {
        //Создадим раскрывающееся меню
        PopupMenu popup = new PopupMenu();
        //Создадим элемент меню
        MenuItem exitItem = new MenuItem("Выход");
        //Добавим для него обработчик
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //Добавим пункт в меню
        popup.add(exitItem);
        SystemTray systemTray = SystemTray.getSystemTray();
        //получим картинку
        Image image = ImageIO.read(new File("D:\\Projects\\JavaProjects\\GetVkMusic\\res\\img\\VK-02.jpg"));//Toolkit.getDefaultToolkit().getImage("vk_icon.png");
        TrayIcon trayIcon = new TrayIcon(image,"VkMusicToStream",popup);
        trayIcon.setImageAutoSize(true);
        //добавим иконку в трей
        systemTray.add(trayIcon);

        trayIcon.displayMessage("VkMusicToStream", "Соединяемся с сервером", TrayIcon.MessageType.INFO);
        VkApi.main(args);
        //
        trayIcon.displayMessage("VkMusicToStream", "Соединение установлено", TrayIcon.MessageType.INFO);


        String oldStatus = VkApi.getStatus();
        String newStatus;
        File outputFile = new File("status.txt");
        FileWriter outputFileWriter = new FileWriter(outputFile, false);
        outputFileWriter.write(oldStatus);
        outputFileWriter.close();


        System.out.println("Current status: " + oldStatus);
        //Бескоечный цикл
        int i = 0;
        for (;;){
            // Запросы на сервер можно подавать раз в 3 секунды
            Thread.sleep(3000); // ждем три секунды
            if (i == 15000){  // Если прошло 45 000 сек (Время взято с запасом, токен дается на день )
                VkApi.main(args); // Обновляем токен
                Thread.sleep(3000);    // Запросы шлем только раз в три секунды
                i = 0;
            }
            //Здесь отработка
            newStatus = VkApi.getStatus();
            if (!newStatus.equals(oldStatus)) {
                oldStatus = newStatus;
                FileWriter outputFileWriter2 = new FileWriter(outputFile, false);
                outputFileWriter2.write(oldStatus);
                outputFileWriter2.close();
              //  trayIcon.displayMessage("VkMusicToStream", "Сейчас играет:\n"+newStatus,TrayIcon.MessageType.INFO);
                //Tools.playDrum(Drum.d53_Ride_Bell, 127, 0);
            }
            i++;
        }
    }
}

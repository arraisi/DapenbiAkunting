package id.co.dapenbi.main.controller;

import id.co.dapenbi.core.util.SecurityUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

@Controller
public class SessionWebSocketController {

    @MessageMapping("/connectionStatus")
    @SendTo("/dapenbi-websocket/session/status")
    public Boolean sessionStatus(){

        try{
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}

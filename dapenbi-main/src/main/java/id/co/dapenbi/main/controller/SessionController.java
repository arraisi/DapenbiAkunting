package id.co.dapenbi.main.controller;

import id.co.dapenbi.core.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/session")
public class SessionController {

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/check")
    public ResponseEntity<Boolean> method(HttpServletRequest request) throws ParseException {
        Boolean result = false;
        String currentUser = SecurityUtil.getUsername();
        if(!currentUser.equals("anonymousUser")){
            result = true;
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}

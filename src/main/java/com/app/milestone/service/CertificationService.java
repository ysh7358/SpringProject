package com.app.milestone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationService {
    public void certifiedPhoneNumber(String userPhoneNumber, int randomNumber) {
//        coolsms api키
        String api_key = "NCSSPLLWZCTD7ET7";
        String api_secret = "QMRPJQGEX3TZFCLXL0E56JUCSE3UJTKX";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", userPhoneNumber);    // 수신전화번호
        params.put("from", "01065559107");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "[milestone] 회원가입 인증번호는" + "[" + randomNumber + "]" + "입니다."); // 문자 내용 입력
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            log.info(obj.toString());
        } catch (CoolsmsException e) {
            log.info(e.getMessage());
            log.info("에러 코드 : " + e.getCode());
        }

    }
}

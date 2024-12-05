package com.ftcs.accountservice.feature.serivce.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {

    public JSONObject getSuccessResponse(String message, Object data) throws JSONException {
        return getResponse("success", message, data);
    }


    public JSONObject getErrorResponse(String message) throws JSONException {
        return getResponse("error", message, null);
    }

    public JSONObject getSuccessResponse(String message) throws JSONException {
        return getResponse("success", message, null);
    }

    public JSONObject getResponseLogin(String status, String token, String message) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("message", message);
        response.put("token", token);
        return response;
    }
    public JSONObject getResponseCaptcha(String status, String captcha) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("captcha", captcha);
        return response;
    }
    public JSONObject getResponse(String status, String message, Object data) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}

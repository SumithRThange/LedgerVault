package com.timeboundwallet.network;

import org.json.JSONObject;

import java.util.Iterator;

import retrofit2.Response;

public class ApiErrorParser {

    private ApiErrorParser() {
    }

    public static String parse(Response<?> response, String fallback) {
        try {
            if (response == null || response.errorBody() == null) {
                return fallback;
            }

            String raw = response.errorBody().string();
            if (raw == null || raw.isBlank()) {
                return fallback;
            }

            JSONObject json = new JSONObject(raw);
            if (json.has("error")) {
                String error = json.optString("error");
                if (!error.isBlank()) {
                    return error;
                }
            }

            Iterator<String> keys = json.keys();
            if (keys.hasNext()) {
                String firstKey = keys.next();
                String message = json.optString(firstKey);
                if (!message.isBlank()) {
                    return message;
                }
            }

            return fallback;
        } catch (Exception ex) {
            return fallback;
        }
    }
}

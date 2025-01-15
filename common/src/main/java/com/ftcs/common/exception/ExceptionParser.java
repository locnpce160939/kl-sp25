package com.ftcs.common.exception;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExceptionParser {
    public String uniqueIndexMessageParser(Exception e) {
        Pattern pattern = Pattern.compile("(The duplicate key value is \\(.*?\\))");
        Matcher matcher = pattern.matcher(e.getMessage());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}

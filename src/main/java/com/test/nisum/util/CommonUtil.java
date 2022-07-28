package com.test.nisum.util;

import org.springframework.context.MessageSource;

import java.util.Locale;

public final class CommonUtil {

    private CommonUtil() {
//        Empty
    }

    public static String getMessage(MessageSource messageSource, String messageKey, Object... arguments){
        return messageSource.getMessage(messageKey, arguments, Locale.getDefault());
    }
}

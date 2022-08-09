package com.chick.net.disk.driver.enums;

import org.thymeleaf.util.StringUtils;

/**
 * @ClassName ContentTypeEnum
 * @Author xiaokexin
 * @Date 2022-08-05 16:02
 * @Description ContentTypeEnum
 * @Version 1.0
 */
public enum ContentTypeEnum {
    HTML("text/html;charset=UTF-8"),
    JSON("application/json;charset=UTF-8");

    private String contentType;

    ContentTypeEnum(String contentType) {
        this.contentType = contentType;
    }

    public static ContentTypeEnum getByValue(String value){
        if (StringUtils.isEmpty(value)){
            return null;
        }
        for(ContentTypeEnum transactType : values()){
            if (value.equals(transactType.getContentType())) {
                return transactType;
            }
        }
        return null;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

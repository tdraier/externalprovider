package org.jahia.services.content.impl.external;

import javax.jcr.Binary;
import java.util.List;
import java.util.Map;

public class ExternalData {
    private String id;
    private String path;
    private String type;
    private List<String> mixin;
    private Map<String,String[]> properties;
    private Map<String,Map<String,String[]>> i18nProperties;
    private Map<String,Binary[]> binaryProperties;

    public ExternalData(String id, String path, String type, Map<String, String[]> properties) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public Map<String,String[]> getProperties() {
        return properties;
    }

    public Map<String,Map<String,String[]>> getI18nProperties() {
        return i18nProperties;
    }

    public void setI18nProperties(Map<String, Map<String, String[]>> i18nProperties) {
        this.i18nProperties = i18nProperties;
    }

    public Map<String, Binary[]> getBinaryProperties() {
        return binaryProperties;
    }

    public void setBinaryProperties(Map<String, Binary[]> binaryProperties) {
        this.binaryProperties = binaryProperties;
    }

    public List<String> getMixin() {
        return mixin;
    }

    public void setMixin(List<String> mixin) {
        this.mixin = mixin;
    }
}

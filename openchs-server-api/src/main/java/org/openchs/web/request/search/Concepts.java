package org.openchs.web.request.search;

import java.util.List;

public class Concepts {
    private String name;

    private String uuid;

    private String dataType;

    private String widgetType;

    private String searchScope;

    private String value;

    private List<String> values;

    private MinMax minmax;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }

    public String getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(String searchScope) {
        this.searchScope = searchScope;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public MinMax getMinmax() {
        return minmax;
    }

    public void setMinmax(MinMax minmax) {
        this.minmax = minmax;
    }

    @Override
    public String toString() {
        return "Concepts{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", dataType='" + dataType + '\'' +
                ", widgetType='" + widgetType + '\'' +
                ", searchScope='" + searchScope + '\'' +
                ", value='" + value + '\'' +
                ", values=" + values +
                ", minmax=" + minmax +
                '}';
    }
}

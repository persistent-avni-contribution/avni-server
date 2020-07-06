package org.openchs.web.request.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.joda.time.LocalDate;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinMax {

    private LocalDate minValue;

    private LocalDate maxValue;

    private Long minValueInt;

    private Long maxValueInt;

    public LocalDate getMinValue() {
        return minValue;
    }

    public void setMinValue(LocalDate minValue) {
        this.minValue = minValue;
    }

    public LocalDate getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(LocalDate maxValue) {
        this.maxValue = maxValue;
    }

    public Long getMinValueInt() {
        return minValueInt;
    }

    public void setMinValueInt(Long minValueInt) {
        this.minValueInt = minValueInt;
    }

    public Long getMaxValueInt() {
        return maxValueInt;
    }

    public void setMaxValueInt(Long maxValueInt) {
        this.maxValueInt = maxValueInt;
    }

    @Override
    public String toString() {
        return "{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}

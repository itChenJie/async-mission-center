package com.mission.center.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateNumberBean {
    private Long successNumber;
    private Long failNumber;
    private Long totalNumber;

    public UpdateNumberBean(Long totalNumber) {
        this.totalNumber = totalNumber;
    }

    public UpdateNumberBean(Long successNumber, Long failNumber) {
        this.successNumber = successNumber;
        this.failNumber = failNumber;
    }
}

package com.mission.center.core.bean;

import com.mission.center.constant.IeTaskState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateStateBean {
    private IeTaskState newState;
    private IeTaskState oldState;
    private String desc;
    public UpdateStateBean(IeTaskState newState) {
        this.newState = newState;
    }

    public UpdateStateBean(IeTaskState newState, IeTaskState oldState) {
        this.newState = newState;
        this.oldState = oldState;
    }

}

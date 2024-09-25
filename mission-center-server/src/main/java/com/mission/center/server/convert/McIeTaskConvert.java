package com.mission.center.server.convert;

import com.mission.center.constant.IeTaskType;
import com.mission.center.server.entity.McIeTask;
import com.mission.center.server.vo.IeTaskAddRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface McIeTaskConvert {
    @Mappings({
            @Mapping(source = "userId",target = "serviceModelUserId")
    })
    McIeTask toIeTask(IeTaskAddRequest request);
}

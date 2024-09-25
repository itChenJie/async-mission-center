
package com.mission.center.util;

import lombok.Data;

@Data
public class PageTotal {

    /**
     * 总行数
     */
    private Integer total;

    /**
     * 分片页大小
     */
    private Integer pageSize;

    public static PageTotal of(Integer total, Integer pageSize) {
        PageTotal pageTotal = new PageTotal();
        pageTotal.setPageSize(pageSize);
        pageTotal.setTotal(total);
        return pageTotal;
    }

}

package com.simple.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 高振中
 * @summary 【分页】数据
 * @date 2024-05-10 21:45:31
 **/
@Setter
@Getter
@AllArgsConstructor
public class Page<T> {
    private List<T> dataList; /* 数据列表 */
    private Integer size; /* 每页记录数(页大小) */
    private Integer rowCount;/* 记录总数 */
    private Integer page;/* 当前页 */
}

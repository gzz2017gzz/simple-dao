package com.simple.common.base;

import com.simple.common.base.utils.FieldUtil;

public interface UserIdProvider {
    default Long userId() {
        return FieldUtil.userId();
    }
}
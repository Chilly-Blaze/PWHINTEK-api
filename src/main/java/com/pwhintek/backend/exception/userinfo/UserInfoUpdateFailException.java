package com.pwhintek.backend.exception.userinfo;

import cn.hutool.core.util.StrUtil;

import static com.pwhintek.backend.constant.UserInfoConstants.UPDATE_DUPLICATION;
import static com.pwhintek.backend.constant.GlobalConstants.UPDATE_ERROR;

/**
 * 更新错误异常
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 25 15:30
 */
public class UserInfoUpdateFailException extends UserInfoException {
    public UserInfoUpdateFailException(String message, String data) {
        super(message, data);
    }

    public static UserInfoUpdateFailException getInstance(String info, String data) {
        return new UserInfoUpdateFailException(StrUtil.format(UPDATE_DUPLICATION, info, info), data);
    }

    public static UserInfoUpdateFailException getInstance(String data) {
        return new UserInfoUpdateFailException(UPDATE_ERROR, data);
    }
}

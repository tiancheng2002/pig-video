package com.zhu.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordVo {

    private String username;

    private String oldPassword;

    private String newPassword;

}

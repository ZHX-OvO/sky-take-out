package com.sky.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


//这个就是用于构建对象的，没什么用，就是高级一点而已
@Builder
//下面三个注解就是用于生成构造器和getset方法的
@Data
@NoArgsConstructor
@AllArgsConstructor
//描述一个 Java 类（DTO，实体类等）的作用和属性信息
@ApiModel(description = "员工登录返回的数据格式")
public class EmployeeLoginVO implements Serializable {

    @ApiModelProperty("主键值")
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("jwt令牌")
    private String token;

}

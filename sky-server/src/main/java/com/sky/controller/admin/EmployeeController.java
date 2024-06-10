package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDTO
     * @return {@link Result }
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);

        return Result.success();
    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO 前端传过来搜索名字，页码，每页多少
     * @return {@link Result }<{@link PageResult }>
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询：{}", employeePageQueryDTO);
        //分页查询的结果封装成这个，这个就是估计的格式
        PageResult pageResult=employeeService.pageQuary(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工
     * @param status  路径变量
     * @param id 想要禁用的员工的id
     * @return {@link Result }
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用禁用员工:{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查找员工
     * @param id
     * @return {@link Result }<{@link Employee }>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查找员工信息")
    public Result<Employee> selsectById(@PathVariable Long id){
        log.info("查找员工信息:{}",id);
        Employee employee=employeeService.selsectById(id);
        return Result.success(employee);
    }


    @PutMapping()
    @ApiOperation("更新员工数据")
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息:{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}

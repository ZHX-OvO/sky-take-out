package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        //对象属性的拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置pojo的其他属性
        //1表示正常，0表示锁定
        //写数字1的话，相当于后期如果这个1的含义改变了，我还得修改全部的代码，不可以
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码，默认密码123456，同时还需要md5加密
        //PasswordConstant.DEFAULT_PASSWORD这个就是123456的常量
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));


        //下面注释掉是因为有AutoFill这个注释来帮我们干了，给公共字段统一添加上面值
        //设置创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());


        //设置创建这个用的人和修改这个用户的人
        //TODO
        //这里就是获取threadlocal中的东西
        //long id=BaseContext.getCurrentId();
        //employee.setCreateUser(id);
        //employee.setUpdateUser(id);

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询方法
     * @param employeePageQueryDTO
     * @return {@link PageResult }
     */
    @Override
    public PageResult pageQuary(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询，本质就是在后面的sql语句上面加上limit
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        //这里面还有一个name属性，用于定向查找
        //这里就是返回了一个arraylist集合，里面的元素是Employee
        Page<Employee> page=employeeMapper.pageQuary(employeePageQueryDTO);

        //获取总共有多少条数据
        long total = page.getTotal();
        //这个就是分页查询的结果了，封装成一个个pojo
        List<Employee> result = page.getResult();

        return new PageResult(total,result);
    }


    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee=new Employee();
        employee.setStatus(status);
        employee.setId(id);

        //数据封装好了，调用mapper对象与数据库交互
        employeeMapper.update(employee);
    }

    /**
     * 根据id查找员工
     * @param id
     * @return {@link Employee }
     */
    @Override
    public Employee selsectById(Long id) {
        Employee employee=employeeMapper.selsectById(id);
        //把密码屏蔽掉
        employee.setPassword("****");
        return employee;
    }


    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        //对象的属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);

        //下面注释掉是因为有AutoFill这个注释来帮我们干了，给公共字段统一添加上面值
        //还得设置一下什么时间修改的，什么人修改的
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);
    }
}

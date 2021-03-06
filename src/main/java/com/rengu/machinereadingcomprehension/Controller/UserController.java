package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import com.rengu.machinereadingcomprehension.Service.UserService;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/login")
    public ResultEntity userLogin(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultService.resultBuilder(loginUser);
    }

    @GetMapping(value = "/ranking")
    public ResultEntity userRanking(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(userService.userRanking(type));
    }

    // 建立普通用户
    @PostMapping
    public ResultEntity saveUser(@RequestParam(value = "badge") MultipartFile badge, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "email") String email, @RequestParam(value = "telephoneNumber") String telephoneNumber, @RequestParam(value = "name") String name, @RequestParam(value = "age") int age, @RequestParam(value = "sex") int sex, @RequestParam(value = "teamName") String teamName, @RequestParam(value = "organization") String organization) throws IOException {
        return ResultService.resultBuilder(userService.saveUser(badge, username, password, email, telephoneNumber, name, age, sex, teamName, organization));
    }

    // 建立管理员用户
    @PostMapping(value = "/admin")
    public ResultEntity saveAdminUser(UserEntity userArgs) {
        return ResultService.resultBuilder(userService.saveAdminUser(userArgs));
    }

    // 删除用户
    @DeleteMapping(value = "{userId}")
    public ResultEntity deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
        return ResultService.resultBuilder(userId + "删除成功");
    }

    //修改用户
    @PostMapping(value = "/{userId}")
    public ResultEntity patchUser(@PathVariable(value = "userId") String userId, UserEntity userArgs) {
        return ResultService.resultBuilder(userService.patchUser(userId, userArgs));
    }

    // 修改用户密码
    @PostMapping(value = "/{userId}/password")
    public ResultEntity patchUserPassword(@PathVariable(value = "userId") String userId, @RequestParam(value = "password") String password) {
        return ResultService.resultBuilder(userService.patchUserPassword(userId, password));
    }

    // 忘记密码检查
    @PostMapping(value = "/forgetpasswordcheck")
    public ResultEntity forgetpasswordcheck(UserEntity userEntity) {
        return ResultService.resultBuilder(userService.forgetpasswordcheck(userEntity));
    }

    // 忘记密码
    @PostMapping(value = "/forgetpassword")
    public ResultEntity forgetPassword(UserEntity userEntity) {
        return ResultService.resultBuilder(userService.forgetPassword(userEntity));
    }

    // 重新提交
    @PostMapping(value = "/{userId}/recommit")
    public ResultEntity recommit(@PathVariable(value = "userId") String userId, @RequestParam(value = "badge", required = false) MultipartFile badge, UserEntity userArgs) throws IOException {
        return ResultService.resultBuilder(userService.recommit(userId, badge, userArgs));
    }

    // 审核通过
    @PatchMapping(value = "/{userId}/accept")
    public ResultEntity patchUserAccept(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.patchUserAccept(userId));
    }

    // 审核未通过
    @PatchMapping(value = "/{userId}/denied")
    public ResultEntity patchUser(@PathVariable(value = "userId") String userId, @RequestParam(value = "message") String message) {
        return ResultService.resultBuilder(userService.patchUserDenied(userId, message));
    }

    // 查看用户
    @GetMapping(value = "/{userId}")
    public ResultEntity getUserById(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.getUserById(userId));
    }

    // 查看所有用户
    @GetMapping
    public ResultEntity getUser() {
        return ResultService.resultBuilder(userService.getUser());
    }

    // 根据角色查看用户
    @GetMapping(value = "/byRoleName")
    public ResultEntity getUserByRoleName(@RequestParam(value = "rolename") String rolename) {
        return ResultService.resultBuilder(userService.getUserByRoleName(rolename));
    }

    // 查看用户证件
    @GetMapping(value = "/{userId}/badge")
    public void getUserBadge(HttpServletResponse httpServletResponse, @PathVariable(value = "userId") String userId) throws IOException {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.USER_ID_PARAM_NOT_FOUND);
        }
        UserEntity userEntity = userService.getUserById(userId);
        httpServletResponse.reset();
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setContentType("image/*");
        // 文件流输出
        IOUtils.copy(new FileInputStream(userEntity.getBadgePath()), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    // 提交文件
    @PutMapping(value = "/{userId}/commitfile")
    public ResultEntity commitFile(@PathVariable(value = "userId") String userId, @RequestParam(value = "ref") MultipartFile ref, @RequestParam(value = "type") int type) throws Exception {
        switch (type) {
            case 0:
                return ResultService.resultBuilder(userService.commitFile_T(userId, ref));
            case 1:
                return ResultService.resultBuilder(userService.commitFile_P(userId, ref));
            case 2:
                return ResultService.resultBuilder(userService.commitFile_F(userId, ref));
            default:
                throw new RuntimeException("类型错误");
        }
    }

    // 保存团队成员
    @PostMapping(value = "/{userId}/crew")
    public ResultEntity saveCrew(@PathVariable(value = "userId") String userId, CrewEntity crewArgs) {
        return ResultService.resultBuilder(userService.saveCrew(userId, crewArgs));
    }

    // 删除团队成员
    @DeleteMapping(value = "/{userId}/crew/{crewId}")
    public ResultEntity deleteCrew(@PathVariable(value = "userId") String userId, @PathVariable(value = "crewId") String crewId) {
        return ResultService.resultBuilder(userService.deleteCrew(userId, crewId));
    }

    // 修改团队成员
    @PatchMapping(value = "/{userId}/crew/{crewId}")
    public ResultEntity patchCrew(@PathVariable(value = "userId") String userId, @PathVariable(value = "crewId") String crewId, CrewEntity crewArgs) {
        return ResultService.resultBuilder(userService.patchCrew(userId, crewId, crewArgs));
    }

    // 查询团队成员
    @GetMapping(value = "/{userId}/crew/{crewId}")
    public ResultEntity getCrewById(@PathVariable(value = "userId") String userId, @PathVariable(value = "crewId") String crewId) {
        return ResultService.resultBuilder(userService.getCrewById(userId, crewId));
    }

    // 查询谈队成员
    @GetMapping(value = "/{userId}/crew")
    public ResultEntity getCrewByUserId(@PathVariable(value = "userId") String userId) throws ParseException {
        return ResultService.resultBuilder(userService.getCrewByUserId(userId));
    }

    // 查询成绩历史
    @GetMapping(value = "/{userId}/scorelogsbytype")
    public ResultEntity getScoreLogByUser(@PathVariable(value = "userId") String userId, @RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(userService.getScoreLogByUserAndType(userId, type));
    }

    // 查询成绩历史
    @GetMapping(value = "/{userId}/scorelogs")
    public ResultEntity getScoreLogByUser(@PathVariable(value = "userId") String userId) {
        return ResultService.resultBuilder(userService.getScoreLogByUser(userId));
    }

    @GetMapping(value = "/time")
    public ResultEntity getTime() {
        return ResultService.resultBuilder(new Date().getTime());
    }

    @PutMapping(value = "formatValue")
    public ResultEntity formatValue() {
        return ResultService.resultBuilder(userService.formatValue());
    }
}

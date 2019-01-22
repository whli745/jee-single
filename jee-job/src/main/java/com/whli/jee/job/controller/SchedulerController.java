package com.whli.jee.job.controller;

import com.whli.jee.core.page.Page;
import com.whli.jee.core.web.controller.BaseController;
import com.whli.jee.core.web.entity.ResponseBean;
import com.whli.jee.job.entity.Scheduler;
import com.whli.jee.job.service.ISchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>类功能<p>
 *
 * @author whli
 * @version 2018/12/24 14:04
 */
@RestController
@RequestMapping("/scheduler/job")
@Api(description = "定时任务API")
public class SchedulerController extends BaseController<Scheduler>{
    @Autowired
    private ISchedulerService schedulerService;

    @Override
    @PostMapping("/findByPage")
    @ApiOperation("分页查询定时任务")
    public ResponseBean findByPage(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        if (entity != null) {
            Page<Scheduler> page = new Page<Scheduler>(entity.getCurrentPage(),entity.getPageSize());
            page = schedulerService.findByPage(entity,page);
            responseBean.setCount(page.getTotal());
            responseBean.setResults(page.getPageRecords());
        }
        return responseBean;
    }

    @Override
    @PostMapping("/add")
    @ApiOperation("增加定时任务")
    public ResponseBean add(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        int rows = schedulerService.add(entity);
        if (rows > 0) {
            responseBean.setResults(true);
            responseBean.setMessage("新增任务成功！");
        }
        return responseBean;
    }

    @Override
    @PostMapping("/update")
    @ApiOperation("修改定时任务")
    public ResponseBean update(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        int rows = schedulerService.update(entity);
        if (rows > 0) {
            responseBean.setResults(true);
            responseBean.setMessage("修改任务成功！");
        }
        return responseBean;
    }

    @Override
    @PostMapping("/delete")
    @ApiOperation("删除定时任务")
    public ResponseBean delete(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        schedulerService.delete(entity);
        responseBean.setResults(true);
        responseBean.setMessage("删除任务成功！");
        return responseBean;
    }

    @ApiIgnore
    @Override
    public Scheduler findByPK(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        return null;
    }

    @ApiIgnore
    @Override
    public Scheduler findByNo(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        return null;
    }

    @ApiIgnore
    @Override
    public Scheduler findByName(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        return null;
    }

    @ApiIgnore
    @Override
    public List<Scheduler> findAll(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        return null;
    }

    @ApiIgnore
    @Override
    public void exportExcel(Scheduler entity, HttpServletResponse response) throws Exception {

    }

    @ApiIgnore
    @Override
    public ResponseBean importExcel(MultipartFile file) throws Exception {
        return null;
    }

    @ApiIgnore
    @Override
    public void exportTemplate(Scheduler entity, HttpServletResponse response) throws Exception {

    }

    /**
     * 开始任务
     * @param entity
     * @return
     */
    @PostMapping("/start")
    @ApiOperation("开始定时任务")
    public ResponseBean startTask(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        Boolean rows = schedulerService.startTask(entity);
        if (rows != null && rows) {
            responseBean.setResults(true);
            responseBean.setMessage("开始任务成功！");
        }
        return responseBean;
    }

    /**
     * 开始任务
     * @param entity
     * @return
     */
    @PostMapping("/startAll")
    @ApiOperation("开始所有任务")
    public ResponseBean startAllTask(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        Boolean rows = schedulerService.startAll();
        if (rows != null && rows) {
            responseBean.setResults(true);
            responseBean.setMessage("开始任务成功！");
        }
        return responseBean;
    }

    /**
     * 暂停任务
     * @param entity
     * @return
     */
    @PostMapping("/stop")
    @ApiOperation("暂停定时任务")
    public ResponseBean stopTask(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        Boolean rows = schedulerService.stopTask(entity);
        if (rows != null && rows) {
            responseBean.setResults(true);
            responseBean.setMessage("停止任务成功！");
        }
        return responseBean;
    }

    /**
     * 暂停任务
     * @param entity
     * @return
     */
    @PostMapping("/stopAll")
    @ApiOperation("暂停所有任务")
    public ResponseBean stopAllTask(@RequestBody Scheduler entity, HttpServletRequest req) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        Boolean rows = schedulerService.stopAll();
        if (rows != null && rows) {
            responseBean.setResults(true);
            responseBean.setMessage("停止任务成功！");
        }
        return responseBean;
    }
}

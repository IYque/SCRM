package cn.iyque.controller;



import cn.hutool.json.JSONUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYqueCustomerSeasVo;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueCustomerSeas;
import cn.iyque.entity.IYqueUser;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueCustomerSeasService;
import cn.iyque.utils.IYqueExcelUtils;
import cn.iyque.utils.SecurityUtils;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import cn.iyque.utils.ServletUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;


/**
 * 客户公海
 */
@RestController
@RequestMapping("/seas")
@Slf4j
public class IYqueCustomerSeasController {


    @Autowired
    IYqueCustomerSeasService yqueCustomerSeasService;


    /**
     * 公海下载模版
     */
    @GetMapping("/export")
    public void export() {

        IYqueExcelUtils.exprotExcel(
                ServletUtils.getResponse(), IYqueCustomerSeas.class, new ArrayList<>(), "客户公海_" + System.currentTimeMillis()
        );

    }


    /**
     * 公海导入
     * @param file
     * @return
     */
    @PostMapping(value ="/importData",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult importData(@RequestPart("allocateUsers") String allocateUsers, @RequestPart("file") MultipartFile file) {

        if(StringUtils.isNotEmpty(allocateUsers)){
            yqueCustomerSeasService.importData( JSONUtil.toList(allocateUsers,IYqueUser.class),file);
        }

        return new ResponseResult();
    }


    /**
     * 更新公海客户相关动态
     * @param customerSeas
     * @return
     */
    @PostMapping("/updateCustomerSeasState")
    public ResponseResult  updateCustomerSeasState(@RequestBody IYqueCustomerSeas customerSeas){
        yqueCustomerSeasService.updateCustomerSeasState(customerSeas);
        return new ResponseResult();
    }


    /**
     * 获取客户公海列表
     * @param customerSeas
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueCustomerSeas> findAll(IYqueCustomerSeas customerSeas){



        Page<IYqueCustomerSeas> customerInfos = yqueCustomerSeasService.findAll(customerSeas,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(customerInfos.getContent(),customerInfos.getTotalElements());
    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/batchDelete/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueCustomerSeasService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 通过id提醒
     *
     * @param ids id列表
     * @return 结果
     */
    @GetMapping(path = "/distribute/{ids}")
    public ResponseResult distribute(@PathVariable("ids") Long[] ids){

        try {
            yqueCustomerSeasService.distribute(ids);
        }catch (IYqueException e){

            return new ResponseResult(HttpStatus.ERROR,e.getMsg(),null);
        }


        return new ResponseResult();
    }






}

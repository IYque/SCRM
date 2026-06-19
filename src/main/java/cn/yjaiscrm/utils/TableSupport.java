package cn.iyque.utils;


import cn.iyque.domain.PageDomain;

/**
 * 表格数据处理
 *
 * @author ruoyi
 */
public class TableSupport {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "page";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "size";



    /**
     * 封装分页对象(jpa)
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        Integer pageNum = ServletUtils.getParameterToInt(PAGE_NUM);
        pageDomain.setPageNum(pageNum==null?0:pageNum-1);
        Integer parameterToInt = ServletUtils.getParameterToInt(PAGE_SIZE);
        pageDomain.setPageSize(parameterToInt == null?10:ServletUtils.getParameterToInt(PAGE_SIZE));
        return pageDomain;
    }

    /**
     * 封装分页对象(为了mybatis-plus)
     */
    public static PageDomain getPageDomainMybatis()
    {
        PageDomain pageDomain = new PageDomain();
        Integer pageNum = ServletUtils.getParameterToInt(PAGE_NUM);
        pageDomain.setPageNum(pageNum);
        Integer parameterToInt = ServletUtils.getParameterToInt(PAGE_SIZE);
        pageDomain.setPageSize(parameterToInt == null?10:ServletUtils.getParameterToInt(PAGE_SIZE));
        return pageDomain;
    }

    public static PageDomain buildPageMybaitsRequest()
    {
        return getPageDomainMybatis();
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }
}

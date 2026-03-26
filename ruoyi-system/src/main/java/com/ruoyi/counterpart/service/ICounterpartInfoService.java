package com.ruoyi.counterpart.service;

import java.util.List;
import com.ruoyi.counterpart.domain.CounterpartInfo;

/**
 * 相对方信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-17
 */
public interface ICounterpartInfoService 
{
    /**
     * 查询相对方信息
     * 
     * @param id 相对方信息主键
     * @return 相对方信息
     */
    public CounterpartInfo selectCounterpartInfoById(Long id);

    /**
     * 查询相对方信息列表
     * 
     * @param counterpartInfo 相对方信息
     * @return 相对方信息集合
     */
    public List<CounterpartInfo> selectCounterpartInfoList(CounterpartInfo counterpartInfo);

    /**
     * 新增相对方信息
     * 
     * @param counterpartInfo 相对方信息
     * @return 结果
     */
    public int insertCounterpartInfo(CounterpartInfo counterpartInfo);

    /**
     * 修改相对方信息
     * 
     * @param counterpartInfo 相对方信息
     * @return 结果
     */
    public int updateCounterpartInfo(CounterpartInfo counterpartInfo);

    /**
     * 批量删除相对方信息
     * 
     * @param ids 需要删除的相对方信息主键集合
     * @return 结果
     */
    public int deleteCounterpartInfoByIds(Long[] ids);

    /**
     * 删除相对方信息信息
     * 
     * @param id 相对方信息主键
     * @return 结果
     */
    public int deleteCounterpartInfoById(Long id);

    void batchInsert(List<CounterpartInfo> list);
}

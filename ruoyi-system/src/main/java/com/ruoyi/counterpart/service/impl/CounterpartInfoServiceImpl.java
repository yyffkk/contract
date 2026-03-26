package com.ruoyi.counterpart.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.counterpart.mapper.CounterpartInfoMapper;
import com.ruoyi.counterpart.domain.CounterpartInfo;
import com.ruoyi.counterpart.service.ICounterpartInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 相对方信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-17
 */
@Service
public class CounterpartInfoServiceImpl implements ICounterpartInfoService 
{
    @Autowired
    private CounterpartInfoMapper counterpartInfoMapper;

    /**
     * 查询相对方信息
     * 
     * @param id 相对方信息主键
     * @return 相对方信息
     */
    @Override
    public CounterpartInfo selectCounterpartInfoById(Long id)
    {
        return counterpartInfoMapper.selectCounterpartInfoById(id);
    }

    /**
     * 查询相对方信息列表
     * 
     * @param counterpartInfo 相对方信息
     * @return 相对方信息
     */
    @Override
    public List<CounterpartInfo> selectCounterpartInfoList(CounterpartInfo counterpartInfo)
    {
        return counterpartInfoMapper.selectCounterpartInfoList(counterpartInfo);
    }

    /**
     * 新增相对方信息
     * 
     * @param counterpartInfo 相对方信息
     * @return 结果
     */
    @Override
    public int insertCounterpartInfo(CounterpartInfo counterpartInfo)
    {
        counterpartInfo.setCreateTime(DateUtils.getNowDate());
        return counterpartInfoMapper.insertCounterpartInfo(counterpartInfo);
    }

    /**
     * 修改相对方信息
     * 
     * @param counterpartInfo 相对方信息
     * @return 结果
     */
    @Override
    public int updateCounterpartInfo(CounterpartInfo counterpartInfo)
    {
        return counterpartInfoMapper.updateCounterpartInfo(counterpartInfo);
    }

    /**
     * 批量删除相对方信息
     * 
     * @param ids 需要删除的相对方信息主键
     * @return 结果
     */
    @Override
    public int deleteCounterpartInfoByIds(Long[] ids)
    {
        return counterpartInfoMapper.deleteCounterpartInfoByIds(ids);
    }

    /**
     * 删除相对方信息信息
     * 
     * @param id 相对方信息主键
     * @return 结果
     */
    @Override
    public int deleteCounterpartInfoById(Long id)
    {
        return counterpartInfoMapper.deleteCounterpartInfoById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<CounterpartInfo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (CounterpartInfo item : list) {
            // 设置创建时间
            item.setCreateTime(DateUtils.getNowDate());
            // 单条插入
            counterpartInfoMapper.insertCounterpartInfo(item);
        }
    }
}

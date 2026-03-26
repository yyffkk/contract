package com.ruoyi.contract.mapper;

import com.ruoyi.contract.domain.BizContractOperateLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BizContractOperateLogMapper {

    @Select("select id, contract_id as contractId, contract_type as contractType, action, detail, operator, operate_time as operateTime from biz_contract_operate_log where contract_id = #{contractId} and contract_type = #{contractType} order by operate_time desc, id desc")
    List<BizContractOperateLog> selectByContract(@Param("contractId") Long contractId, @Param("contractType") String contractType);

    @Insert("insert into biz_contract_operate_log(contract_id, contract_type, action, detail, operator, operate_time, create_by, create_time, update_by, update_time) values(#{contractId}, #{contractType}, #{action}, #{detail}, #{operator}, #{operateTime}, #{createBy}, now(), #{updateBy}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizContractOperateLog log);
}

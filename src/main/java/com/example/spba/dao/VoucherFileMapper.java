package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.VoucherFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VoucherFileMapper extends BaseMapper<VoucherFile> {

    /**
     * 查询指定 identity_number + index_no 下的最大 seq（加行级锁）
     */
    Integer selectMaxSeqForUpdate(@Param("identityNumber") String identityNumber,
                                  @Param("indexNo") String indexNo);
}

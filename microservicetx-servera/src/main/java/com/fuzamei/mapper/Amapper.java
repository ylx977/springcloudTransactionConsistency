package com.fuzamei.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * Created by ylx on 2018/12/25.
 */
@Mapper
public interface Amapper {


    @Update("update accounta set money = money + #{moneys}, utime = #{time} where id = #{id}")
    int updateMoneya(@Param("id") String id,
                     @Param("moneys") Double moneys,
                     @Param("time") Long time);
}

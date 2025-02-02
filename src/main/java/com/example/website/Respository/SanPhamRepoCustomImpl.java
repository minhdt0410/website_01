package com.example.website.Respository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;

import com.example.website.Response.SanPhamOfficeResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import com.example.website.Utill.DataUtil;

@Repository
public class SanPhamRepoCustomImpl implements SanPhamRepoCustom {
    @Autowired
    EntityManager em;

    @Override
    public List<SanPhamOfficeResponse> findSanPhamOffice() {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                    select
                    ctsp.id idchitietsanpham,
                    sp.ten_san_pham tenSanPham,
                    case
                        when spkm.id is not null then spkm.gia_moi 
                        else ctsp.gia_ban 
                        end as gia,
                    ms.ten_mau_sac tenmausac,
                    s.ten_size tensize
                    from san_pham sp 
                    join chi_tiet_san_pham ctsp on sp.id = ctsp.id_san_pham
                    join mau_sac ms on ms.id = ctsp.id_mau_sac 
                    join [size] s on s.id = ctsp.id_size 
                    left join san_pham_khuyen_mai spkm on ctsp.id = spkm.id_chi_tiet_san_pham 
                    order by sp.ten_san_pham
                """);

        Query query = em.createNativeQuery(String.valueOf(sql));
        List<Object[]> queryResult = query.getResultList();

        List<SanPhamOfficeResponse> rs = DataUtil.convertObjectsToClassDynamic(
                DataUtil.changeParamTypeSqlToJava("idchitietsanpham,tenSanPham,gia, tenmausac, tensize"), queryResult, SanPhamOfficeResponse.class);
        return rs;
    }
    
}

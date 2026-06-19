package cn.iyque.dao;

import cn.iyque.entity.IYqueComplainAnnex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IYQueComplaintAnnexDao extends JpaRepository<IYqueComplainAnnex,Long> {

    List<IYqueComplainAnnex> findByComplainId(Long complainId);

}

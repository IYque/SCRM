package cn.iyque.dao;

import cn.iyque.domain.IYqueScreenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueScreenshotDao extends JpaRepository<IYqueScreenshot,Long>, JpaSpecificationExecutor<IYqueScreenshot> {
}

package cn.iyque.service;

import cn.iyque.domain.FunctionRoute;
import java.util.List;

public interface FunctionRouteService {
    List<FunctionRoute> getAllFunctionRoutes();
    String getFunctionRoutesAsText();
}

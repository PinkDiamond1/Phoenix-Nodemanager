package app.service.configuration;

import org.springframework.ui.Model;

import java.util.Map;

public interface IAddRootComponentToModel {

    void loadToModel(Model model);

    void save(Map<String, Object> formParams);

}

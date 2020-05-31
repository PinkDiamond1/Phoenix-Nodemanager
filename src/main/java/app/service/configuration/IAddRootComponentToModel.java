package app.service.configuration;

import org.springframework.ui.Model;

import java.util.Map;

public interface IAddRootComponentToModel {

    void loadToModel(final Model model);

    void save(final Map<String, Object> formParams);

}

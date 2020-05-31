package app.service.configuration.parse;

import app.component.RootComponent;

import java.util.Map;

@FunctionalInterface
public interface IParseRootComponent {

    RootComponent getRootComponent(final Map<String, Object> map);

}

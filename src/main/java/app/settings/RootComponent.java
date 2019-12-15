package app.settings;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RootComponent implements ISettingsComponent {

    public static final String ROOTNAME = "apex";

    private HashMap<String, Object> components;

    @Override
    public Map<String, Object> toMap() {
        final HashMap<String, Object> result = new HashMap<>();
        result.put(ROOTNAME, components);
        return result;
    }

    @Override
    public String getRootName(){
        return ROOTNAME;
    }

}

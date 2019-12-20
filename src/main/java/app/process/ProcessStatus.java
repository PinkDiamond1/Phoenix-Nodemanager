package app.process;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessStatus {

    FINISHED("finished"),
    RUNNING("running"),
    FAILED("failed"),
    INSTALL("install");

    private String status;

}

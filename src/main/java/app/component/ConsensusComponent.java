package app.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsensusComponent {

    @JsonIgnore
    public static final String NAME = "consensus";

    @JsonProperty(value = SettingsField.CONSENSUS_TIME_ERROR)
    private int acceptableTimeError;

    @JsonProperty(value = SettingsField.CONSENSUS_AWARD)
    private double candidateAward;

    @JsonProperty(value = SettingsField.CONSENSUS_ELECT_TIME)
    private int electeTime;

    @JsonProperty(value = SettingsField.CONSENSUS_INITIAL_WITNESS)
    private ArrayList<HashMap<String, String>> initialWitness;

    @JsonProperty(value = SettingsField.CONSENSUS_INTERVAL)
    private int produceInterval;

    @JsonProperty(value = SettingsField.CONSENSUS_REPETITIONS)
    private int producerRepetitions;

    @JsonProperty(value = SettingsField.CONSENSUS_TOTAL_WITNESS)
    private int totalWitnessNum;

    @JsonProperty(value = SettingsField.CONSENSUS_WITNESS)
    private int witnessNum;

}

package app.service.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IQueryProposal {

    List<Map<String, String>> getAllActiveProposals();

    HashMap<String, List<Integer>> getAllActiveVotes();

    String getAllProposalsRaw();

}

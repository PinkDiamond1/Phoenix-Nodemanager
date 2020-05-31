package app.controller;

import app.config.ApplicationPaths;
import app.service.query.IQueryProducer;
import app.service.query.IQueryProposal;
import app.service.transaction.IProposalTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.PROPOSAL_PAGE)
public class ProposalController {

    @Autowired
    @Qualifier("DataQueryLogic")
    private IQueryProducer producerQuery;

    @Autowired
    @Qualifier("DataQueryLogic")
    private IQueryProposal proposalQuery;

    @Autowired
    @Qualifier("TransactionLogic")
    private IProposalTx proposalTx;

    @GetMapping
    public String getProposalPage(final Model model) {
        final Optional<String> producerAccount = producerQuery.getProducerAddress();
        model.addAttribute("producer", producerAccount.isEmpty() ?
                "No registered Producer found" :
                producerAccount.get());

        model.addAttribute("currentTimestamp", Instant.now().toEpochMilli() + (73 * 3600 * 1000));

        final List<Map<String, String>> proposals = proposalQuery.getAllActiveProposals();
        model.addAttribute("proposals", proposals);
        model.addAttribute("proposalIDs", proposals.stream()
                .map(proposal -> proposal.get("proposalID"))
                .collect(Collectors.toList()));

        model.addAttribute("voteData", proposalQuery.getAllActiveVotes());

        return ApplicationPaths.PROPOSAL_PAGE;
    }

    @PostMapping(params = "action=new")
    public String newProposal(@RequestParam(value = "producer") final String producer,
                              @RequestParam(value = "password") final String password,
                              @RequestParam(value = "proposalType") final int type,
                              @RequestParam(value = "amount") final double amount,
                              @RequestParam(value = "timestamp") final long timestamp){
        proposalTx.createNewProposal(producer, password, type, amount, timestamp);
        return ApplicationPaths.PROPOSAL_PATH;
    }

    @PostMapping(params = "action=vote-yes")
    public String voteProposalYes(@RequestParam(value = "producer") final String producer,
                                  @RequestParam(value = "proposalID") final String proposalID,
                                  @RequestParam(value = "password") final String password){
        proposalTx.voteOnProposal(producer, password, proposalID, true);
        return ApplicationPaths.PROPOSAL_PATH;
    }

    @PostMapping(params = "action=vote-no")
    public String voteProposalNo(@RequestParam(value = "producer") final String producer,
                                 @RequestParam(value = "proposalID") final String proposalID,
                                 @RequestParam(value = "password") final String password){
        proposalTx.voteOnProposal(producer, password, proposalID, false);
        return ApplicationPaths.PROPOSAL_PATH;
    }

    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getProposals() {
        return proposalQuery.getAllProposalsRaw();
    }
}

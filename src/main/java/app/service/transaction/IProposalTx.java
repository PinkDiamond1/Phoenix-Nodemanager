package app.service.transaction;

public interface IProposalTx {

    void voteOnProposal(String producer, String password, String proposalID, boolean value);

    void createNewProposal(String producer, String password, int type, double amount, long timestamp);

}

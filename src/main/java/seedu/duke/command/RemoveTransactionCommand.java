package seedu.duke.command;

import seedu.duke.exception.DukeException;
import seedu.duke.ui.Ui;
import seedu.duke.classes.StateManager;

import java.util.HashMap;

public class RemoveTransactionCommand extends Command {

    private static final String MISSING_IDX = "Index cannot be empty...";
    private static final String INVALID_IDX = "Please enter a valid index.";
    private static final String MISSING_TYPE = "Please indicate the transaction type.";

    private static final String INVALID_TYPE = "Please indicate either /type in or /type out.";

    private static final String ERROR_MSG = "Error encountered when removing transaction.";

    public RemoveTransactionCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        throwIfInvalidDescOrArgs();
        removeTransaction(ui);
    }

    private void throwIfInvalidDescOrArgs() throws DukeException {
        assert getDescription() != null;
        assert getArgs() != null;

        if (getDescription().isBlank()) {
            throw new DukeException(MISSING_IDX);
        }
        String description = getDescription();
        if (!isInteger(description)) {
            throw new DukeException(INVALID_IDX);
        }

        String typeArg = getArg("type");
        if (typeArg == null) {
            throw new DukeException(MISSING_TYPE);
        }

        if (!(typeArg.equalsIgnoreCase("in") || typeArg.equalsIgnoreCase("out"))) {
            throw new DukeException(INVALID_TYPE);
        }
    }

    private boolean isInteger(String description) {
        try {
            Integer.parseInt(description);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void removeTransaction(Ui ui) throws DukeException {
        String type = getArg("type").toLowerCase();
        int maxSize = getTransactionMaxSize(type);
        int idx = parseIdx(maxSize) - 1; //-1 due to 0 based indexing for arraylist
        assert idx >= 0 : "Index should be a valid integer greater than 0";

        boolean isSuccess = false;
        String transactionDescription = "";
        if (type.equals("in")) {
            transactionDescription = StateManager.getStateManager().getIncome(idx)
                    .getTransaction().getDescription();
            isSuccess = StateManager.getStateManager().removeIncome(idx);
        } else if (type.equals("out")) {
            transactionDescription = StateManager.getStateManager().getExpense(idx)
                    .getTransaction().getDescription();
            isSuccess = StateManager.getStateManager().removeExpense(idx);
        }
        if (!isSuccess) {
            throw new DukeException(ERROR_MSG);
        }
        printSuccess(ui, transactionDescription, idx + 1); // idx + 1 for format to show to user
    }

    private int getTransactionMaxSize(String type) {
        int maxSize = 0;
        if (type.equals("in")) {
            maxSize = StateManager.getStateManager().getIncomesSize();
        } else if (type.equals("out")) {
            maxSize = StateManager.getStateManager().getExpensesSize();
        }
        return maxSize;
    }

    private int parseIdx(int maxSize) throws DukeException {
        int index = Integer.parseInt(getDescription());
        if (index < 1 || index > maxSize) {
            throw new DukeException(INVALID_IDX);
        }
        return index;
    }

    private void printSuccess(Ui ui, String description, int idx) {
        String type = getArg("type").toLowerCase();
        String transactionType = type.equals("in") ? "income" : "expense";
        String msg = "Successfully remove " + transactionType + " no." + idx + ": " + description;
        ui.print(msg);
    }
}
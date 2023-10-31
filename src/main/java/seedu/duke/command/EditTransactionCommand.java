package seedu.duke.command;

import seedu.duke.classes.Expense;
import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.exception.DukeException;
import seedu.duke.parser.Parser;
import seedu.duke.ui.Ui;

import java.util.HashMap;

public class EditTransactionCommand extends Command {

    private static final String MISSING_IDX = "Index cannot be empty...";
    private static final String INVALID_IDX = "Please enter a valid index.";
    private static final String MISSING_TYPE = "Please indicate the transaction type.";
    private static final String INVALID_TYPE = "Please indicate either /type in or /type out.";
    private static final String MISSING_EDIT = "Please enter the category to edit";
    private static final String TOO_MANY_ARGUMENTS = "Please enter only one category to edit";
    private static final String BAD_AMOUNT = "Invalid amount value specified...";
    private static final String DATE_EDIT = "Can not edit Date...";

    private static final String ERROR_MSG = "Error encountered when editing transaction.";

    protected static final String AMOUNT_ARG = "amount";
    protected static final String DESCRIPTION_ARG = "description";
    protected static final String GOAL_ARG = "goal";
    protected static final String DATE_ARG = "date";

    public EditTransactionCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        throwIfInvalidDescOrArgs();
        editTransaction(ui);
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

        if (!getArg(DATE_ARG).isBlank()) {
            throw new DukeException(DATE_EDIT);
        }

        if (getArgs().size() == 1) {
            throw new DukeException(MISSING_EDIT);
        }

        if (getArgs().size() > 2) {
            throw new DukeException(TOO_MANY_ARGUMENTS);
        }

        if (!getArg(AMOUNT_ARG).isBlank()) {
            Double amount = Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG));
            if (amount == null) {
                throw new DukeException(BAD_AMOUNT);
            }
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

    private void editTransaction(Ui ui) throws DukeException {
        String type = getArg("type").toLowerCase();
        String newDescription = "";
        // Double newAmount = Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG));
        Double newAmount = 0.0;
        String newGoalDescription = "";

        int maxSize = getTransactionMaxSize(type);
        int idx = parseIdx(maxSize) - 1; //-1 due to 0 based indexing for arraylist
        assert idx >= 0 : "Index should be a valid integer greater than 0";

        boolean isSuccess = false;
        String transactionDescription = "";
        if (type.equals("in")) {
            Income income = StateManager.getStateManager().getIncome(idx);
            transactionDescription = StateManager.getStateManager().getIncome(idx)
                    .getTransaction().getDescription();
            if (!getArg(DESCRIPTION_ARG).isBlank()) {
                newDescription = getArg(DESCRIPTION_ARG);
                StateManager.getStateManager().getIncome(idx)
                        .getTransaction().setDescription(newDescription);
            }

            else if (!getArg(AMOUNT_ARG).isBlank()) {
                newAmount = Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG));
                StateManager.getStateManager().getIncome(idx)
                        .getTransaction().setAmount(newAmount);
            }

            else if (!getArg(GOAL_ARG).isBlank()) {
                newGoalDescription = getArg(GOAL_ARG);
                StateManager.getStateManager().getIncome(idx)
                        .getGoal().setDescription(newGoalDescription);;
            }
            
            Income editedIncome = StateManager.getStateManager().getIncome(idx);
            isSuccess = !(editedIncome == income) ? true : false;

        } else if (type.equals("out")) {
            Expense expense = StateManager.getStateManager().getExpense(idx);
            transactionDescription = StateManager.getStateManager().getExpense(idx)
                    .getTransaction().getDescription();
            if (!getArg(DESCRIPTION_ARG).isBlank()) {
                newDescription = getArg(DESCRIPTION_ARG);
                StateManager.getStateManager().getExpense(idx)
                        .getTransaction().setDescription(newDescription);
            }

            else if (!getArg(AMOUNT_ARG).isBlank()) {
                newAmount = Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG));
                StateManager.getStateManager().getExpense(idx)
                        .getTransaction().setAmount(newAmount);
            }
            
            Expense editedExpense = StateManager.getStateManager().getExpense(idx);
            isSuccess = !(editedExpense == expense) ? true : false;
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
        String msg = "Successfully edited " + transactionType + " no." + idx + description;
        ui.print(msg);
    }
}
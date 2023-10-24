package seedu.duke.command;

import seedu.duke.classes.Goal;
import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.classes.Transaction;
import seedu.duke.exception.DukeException;
import seedu.duke.parser.Parser;
import seedu.duke.ui.Ui;

import java.util.ArrayList;
import java.util.HashMap;

public class AddIncomeCommand extends AddTransactionCommand {
    private static final String AMOUNT_ARG = "amount";
    private static final String GOAL_ARG = "goal";
    private static final String[] HEADERS = {"Description", "Amount", "Goal"};

    private static final String SUCCESS_PRINT = "Nice! The following income has been tracked:";
    private static final String MISSING_GOAL = "Goal cannot be empty...";

    public AddIncomeCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        throwIfInvalidDescOrArgs(GOAL_ARG, MISSING_GOAL);
        Transaction transaction = prepareTransaction();
        Income income = addNewIncome(transaction);
        printSuccess(ui, income);
    }

    private Income addNewIncome(Transaction transaction) throws DukeException {
        Goal goal = handleGoal();
        Income income = new Income(transaction, goal);
        StateManager.getStateManager().addIncome(income);
        return income;
    }

    private Transaction prepareTransaction() {
        String description = getDescription();
        Double amount = Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG));
        return new Transaction(description, amount, null);
    }

    private void printSuccess(Ui ui, Income income) {
        Transaction transaction = income.getTransaction();
        ArrayList<String> printValues = new ArrayList<>();
        printValues.add(transaction.getDescription());
        printValues.add(ui.formatAmount(transaction.getAmount()));
        printValues.add(income.getGoal().getDescription());
        ui.print(SUCCESS_PRINT);
        ui.printTableRow(printValues, HEADERS);
    }

    private Goal handleGoal() throws DukeException {
        StateManager state = StateManager.getStateManager();
        String goal = getArg(GOAL_ARG);
        int index = state.getGoalIndex(goal);
        if (index == -1) {
            String failedGoalMessage = "Please add '" + goal + "' as a goal first.";
            throw new DukeException(failedGoalMessage);
        } else {
            return state.getGoal(index);
        }
    }
}

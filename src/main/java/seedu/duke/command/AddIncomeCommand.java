package seedu.duke.command;

import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.classes.Transaction;
import seedu.duke.exception.DukeException;
import seedu.duke.parser.Parser;
import seedu.duke.ui.Ui;

import java.util.ArrayList;
import java.util.HashMap;

public class AddIncomeCommand extends Command {
    private static final String AMOUNT_ARG = "amount";
    private static final String[] HEADERS = {"Description", "Amount"};

    public AddIncomeCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        // TODO:
        //  Add dates arg to command
        throwIfInvalidDescOrArgs();
        Transaction transaction = prepareTransaction();
        Income income = addNewIncome(transaction);
        printSuccess(ui, income);
    }

    private Income addNewIncome(Transaction transaction) {
        Income income = new Income(transaction, null);
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
        ui.printTableRow(printValues, HEADERS);
    }

    private void throwIfInvalidDescOrArgs() throws DukeException {
        // TODO:
        //  Ensure goal is non-null - after V1.0
        assert getDescription() != null;
        assert getArgs() != null;

        if (getDescription().isBlank()) {
            throw new DukeException("Description cannot be empty...");
        }

        String amountArg = getArg(AMOUNT_ARG);
        if (amountArg == null) {
            throw new DukeException("Amount cannot be empty...");
        }

        Double amount = Parser.parseNonNegativeDouble(amountArg);
        if (amount == null) {
            throw new DukeException("Invalid amount value specified...");
        }
    }
}

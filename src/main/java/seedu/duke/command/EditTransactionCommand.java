package seedu.duke.command;

import seedu.duke.classes.Expense;
import seedu.duke.classes.Goal;
import seedu.duke.classes.Category;
import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.exception.DukeException;
import seedu.duke.parser.Parser;
import seedu.duke.ui.Ui;

import java.util.HashMap;

public class EditTransactionCommand extends Command {

    protected static final String AMOUNT_ARG = "amount";
    protected static final String DESCRIPTION_ARG = "description";
    protected static final String GOAL_ARG = "goal";
    protected static final String CATEGORY_ARG = "category";
    protected static final String DATE_ARG = "date";

    private static final String MISSING_IDX = "Index cannot be empty...";
    private static final String INVALID_IDX = "Please enter a valid index.";
    private static final String MISSING_TYPE = "Please indicate the transaction type.";
    private static final String INVALID_TYPE = "Please indicate either /type in or /type out.";
    private static final String MISSING_EDIT = "Please enter the category to edit";
    private static final String TOO_MANY_ARGUMENTS = "Please enter only one category to edit";
    private static final String BAD_AMOUNT = "Invalid amount value specified...";
    private static final String DATE_EDIT = "Can not edit Date...";
    private static final String SAME_GOAL = "Please enter a different goal description";
    private static final String SAME_CATEGORY = "Please enter a different category name";

    private static final String ERROR_MSG = "Error encountered when editing transaction.";

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

        if (getArg(DATE_ARG) != null && !getArg(DATE_ARG).isBlank()) {
            throw new DukeException(DATE_EDIT);
        }

        if (getArgs().size() == 1) {
            throw new DukeException(MISSING_EDIT);
        }

        if (getArgs().size() > 2) {
            throw new DukeException(TOO_MANY_ARGUMENTS);
        }

        if (getArg(AMOUNT_ARG) != null && !getArg(AMOUNT_ARG).isBlank()) {
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
        int maxSize = getTransactionMaxSize(type);
        int idx = parseIdx(maxSize) - 1; //-1 due to 0 based indexing for arraylist
        assert idx >= 0 : "Index should be a valid integer greater than 0";

        boolean isSuccess = false;
        String transactionDescription = "";
        if (type.equals("in")) {
            Income income = StateManager.getStateManager().getIncome(idx);
            transactionDescription = StateManager.getStateManager().getIncome(idx)
                    .getTransaction().getDescription();
            if (getArg(DESCRIPTION_ARG) != null && !getArg(DESCRIPTION_ARG).isBlank()) {
                String originalDescription = income.getTransaction().getDescription();
                StateManager.getStateManager().getIncome(idx)
                        .getTransaction().setDescription(getArg(DESCRIPTION_ARG));
                String newDescription = income.getTransaction().getDescription();
                isSuccess = !originalDescription.equals(newDescription);
            } else if (getArg(AMOUNT_ARG) != null && !getArg(AMOUNT_ARG).isBlank()) {
                Double originalAmount = income.getTransaction().getAmount();
                StateManager.getStateManager().getIncome(idx)
                        .getTransaction().setAmount(Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG)));
                Double newAmount = income.getTransaction().getAmount();
                isSuccess = !originalAmount.equals(newAmount);
            } else if (getArg(GOAL_ARG) != null && !getArg(GOAL_ARG).isBlank()) {
                isSuccess = handleGoalEdit(income, idx);
            }
        } else if (type.equals("out")) {
            Expense expense = StateManager.getStateManager().getExpense(idx);
            transactionDescription = StateManager.getStateManager().getExpense(idx)
                    .getTransaction().getDescription();
            if (getArg(DESCRIPTION_ARG) != null && !getArg(DESCRIPTION_ARG).isBlank()) {
                String originalDescription = expense.getTransaction().getDescription();
                StateManager.getStateManager().getExpense(idx)
                        .getTransaction().setDescription(getArg(DESCRIPTION_ARG));
                String newDescription = expense.getTransaction().getDescription();
                isSuccess = !originalDescription.equals(newDescription);
            } else if (getArg(AMOUNT_ARG) != null && !getArg(AMOUNT_ARG).isBlank()) {
                Double originalAmount = expense.getTransaction().getAmount();
                StateManager.getStateManager().getExpense(idx)
                        .getTransaction().setAmount(Parser.parseNonNegativeDouble(getArg(AMOUNT_ARG)));
                Double newAmount = expense.getTransaction().getAmount();
                isSuccess = !originalAmount.equals(newAmount);
            } else if (getArg(CATEGORY_ARG) != null && !getArg(CATEGORY_ARG).isBlank()) {
                isSuccess = handleCategoryEdit(expense, idx);
            }
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
        String msg = "Successfully edited " + transactionType + " no." + idx + " " + description;
        ui.print(msg);
    }

    private boolean handleGoalEdit(Income income, int idx) throws DukeException {
        boolean isSuccess = false;
        boolean foundNewGoal = false;
        Goal originalGoal = income.getGoal();
        String newGoalDescription = getArg(GOAL_ARG);
        if (originalGoal.getDescription().equals(newGoalDescription)) {
            throw new DukeException(SAME_GOAL);
        }

        for (Goal goal : StateManager.getStateManager().getAllGoals()) {
            if (goal.getDescription().equals(newGoalDescription)) {
                StateManager.getStateManager().getIncome(idx)
                        .setGoal(goal);
                isSuccess = !originalGoal.equals(income.getGoal());
                foundNewGoal = true;
                break;
            }
        }
        if (!foundNewGoal) {
            throw new DukeException("Please add " + newGoalDescription + " as a goal first.");
        }
        return isSuccess;
    }

    private boolean handleCategoryEdit(Expense expense, int idx) throws DukeException {
        boolean isSuccess = false;
        boolean foundNewCategory = false;
        Category originalCategory = expense.getCategory();
        String newCategoryDescription = getArg(CATEGORY_ARG);
        if (originalCategory.getName().equals(newCategoryDescription)) {
            throw new DukeException(SAME_CATEGORY);
        }

        for (Category category : StateManager.getStateManager().getAllCategories()) {
            if (category.getName().equals(newCategoryDescription)) {
                StateManager.getStateManager().getExpense(idx).setCategory(category);
                isSuccess = !originalCategory.equals(expense.getCategory());
                foundNewCategory = true;
                break;
            }
        }
        if (!foundNewCategory) {
            throw new DukeException("Please add " + newCategoryDescription + " as a category first.");
        }
        return isSuccess;
    }
}

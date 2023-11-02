package seedu.duke.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.duke.classes.StateManager;
import seedu.duke.exception.DukeException;
import seedu.duke.parser.Parser;
import seedu.duke.ui.Ui;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EditTransactionCommandTest {

    private static Parser parser = new Parser();
    private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private static Ui ui = new Ui(outputStream);

    @BeforeEach
    void populateStateManager() {
        try {
            parser.parse("goal /add car /amount 1000").execute(ui);
            parser.parse("goal /add ps5 /amount 1000").execute(ui);

            parser.parse("in part-time job /amount 1000 /goal car /date 18092023").execute(ui);
            parser.parse("in allowance /amount 500 /goal car").execute(ui);
            parser.parse("in sell stuff /amount 50 /goal ps5").execute(ui);

            parser.parse("out buy dinner /amount 15 /category food").execute(ui);
            parser.parse("out popmart /amount 12 /category toy").execute(ui);
            parser.parse("out grab /amount 20 /category transport").execute(ui);
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    void clearStateManager() {
        StateManager.clearStateManager();
    }

    @Test
    void execute_missingIdx_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit /type in /description part-time job");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_missingTypeArgument_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit 1 ");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_missingTypeValue_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit 1 /type /description part-time job");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_negativeIdx_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit -1 /type in /description part-time job");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_outOfRangeIdx_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit 1000 /type in /description part-time job");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_tooManyArguments_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit 2 /type in /description part-time job /amount 50");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_attemptToEditDate_exceptionThrown() throws DukeException {
        Command command = parser.parse("edit 1 /type in /date 18102023");
        assertThrows(DukeException.class, () -> {
            command.execute(ui);
        });
    }

    @Test
    void execute_validIncomeIdx_edited() throws DukeException {
        Command command = parser.parse("edit 1 /type in /description allowance");
        command.execute(ui);
        String transactionDescription = StateManager.getStateManager().getIncome(0) // 0-based indexing
                .getTransaction().getDescription();
        assertNotEquals("part-time job", transactionDescription);
    }

    @Test
    void execute_validExpenseIdx_edited() throws DukeException {
        Command command = parser.parse("edit 2 /type out /amount 10");
        command.execute(ui);
        Double transactionAmount = StateManager.getStateManager().getExpense(1) // 0-based indexing
                .getTransaction().getAmount();
        assertNotEquals("12", transactionAmount);
    }
}

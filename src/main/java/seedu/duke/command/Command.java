package seedu.duke.command;

import seedu.duke.exception.DukeException;
import seedu.duke.ui.Ui;

import java.util.HashMap;

public abstract class Command {
    private final String description;
    private final HashMap<String, String> args;

    public Command() {
        description = null;
        args = null;
    }

    public Command(String description, HashMap<String, String> args) {
        this.description = description;
        this.args = args;
    }

    protected String getDescription() {
        return description;
    }

    protected String getArg(String key) {
        assert args != null;
        return args.get(key);
    }

    protected HashMap<String, String> getArgs() {
        return args;
    }

    public abstract void execute(Ui ui) throws DukeException;
}

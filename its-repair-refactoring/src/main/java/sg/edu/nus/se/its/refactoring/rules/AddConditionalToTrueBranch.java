package sg.edu.nus.se.its.refactoring.rules;

import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import sg.edu.nus.se.its.model.Program;
import sg.edu.nus.se.its.refactoring.RefactoringRule;

/**
 * Add conditional true block (if 1 == 1).
 */
public class AddConditionalToTrueBranch implements RefactoringRule {

  @Override
  public Optional<Program> applyRule(Program program, String functionName, int location) {
    throw new NotImplementedException();
  }
}

package sg.edu.nus.se.its.refactoring;

import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import sg.edu.nus.se.its.model.Program;

/**
 * The Refactoring class. Generates deep copies of programs for refactoring rules to work on them.
 */
public class Refactoring implements Refactor {

  /**
   * Refactor a program using provided refactoring rules.
   *
   * @param program the program to refactor
   * @param rule the refactoring rule to apply
   * @param functionName the function name to apply refactoring rule
   * @param location the location to apply refactoring rule
   * @return the refactored program
   */
  public Optional<Program> refactorProgram(Program program, RefactoringRule rule,
      String functionName, int location) {
    throw new NotImplementedException();
  }
}

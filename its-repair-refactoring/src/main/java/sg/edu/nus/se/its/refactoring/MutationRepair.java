package sg.edu.nus.se.its.refactoring;

import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import sg.edu.nus.se.its.alignment.VariableMapping;
import sg.edu.nus.se.its.errorlocalizer.ErrorLocalisation;
import sg.edu.nus.se.its.interpreter.Interpreter;
import sg.edu.nus.se.its.model.Input;
import sg.edu.nus.se.its.model.Program;
import sg.edu.nus.se.its.repair.Repair;
import sg.edu.nus.se.its.repair.RepairCandidate;

/**
 * Mutation-based repair implements a repair strategy uses mutation templates on the expression
 * level to modify the submitted program.
 */
public class MutationRepair implements Repair {

  @Override
  public List<RepairCandidate> repair(Program referenceProgram, Program submittedProgram,
      ErrorLocalisation errorLocations, VariableMapping variableMapping, List<Input> inputs,
      Interpreter interpreter) throws Exception {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

}

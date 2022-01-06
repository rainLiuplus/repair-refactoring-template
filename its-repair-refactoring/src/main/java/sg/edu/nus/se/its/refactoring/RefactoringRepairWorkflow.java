package sg.edu.nus.se.its.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sg.edu.nus.se.its.alignment.CfgBasedStructuralAlignment;
import sg.edu.nus.se.its.alignment.StructuralMapping;
import sg.edu.nus.se.its.alignment.VariableAlignment;
import sg.edu.nus.se.its.alignment.VariableMapping;
import sg.edu.nus.se.its.alignment.VariableMappingByDefUseAnalysis;
import sg.edu.nus.se.its.errorlocalizer.BasicErrorLocalizer;
import sg.edu.nus.se.its.errorlocalizer.ErrorLocalisation;
import sg.edu.nus.se.its.exception.AlignmentException;
import sg.edu.nus.se.its.interpreter.Interpreter;
import sg.edu.nus.se.its.model.Input;
import sg.edu.nus.se.its.model.Program;
import sg.edu.nus.se.its.refactoring.rules.AddConditionalToTrueBranch;
import sg.edu.nus.se.its.refactoring.rules.AddEmptyElseToConditional;
import sg.edu.nus.se.its.refactoring.rules.AddEmptyTrueBlock;
import sg.edu.nus.se.its.repair.RepairCandidate;
import sg.edu.nus.se.its.util.Constants;

/**
 * Implements the refactoring repair workflow.
 */
public class RefactoringRepairWorkflow {

  /**
   * TODO Note that this implementation is just an example. It needs to be fully implemented and
   * extended by the students.
   */
  public List<RepairCandidate> repair(Program referenceProgram, Program submittedProgram,
      List<Input> inputs, Interpreter interpreter) throws Exception {

    /* 1. Generate Refactorings. */
    Refactoring refactorGenerator = new Refactoring();
    int[] targetLocations = {1}; // TODO e.g., can be also randomized

    RefactoringRule[] rules = {new AddConditionalToTrueBranch(), new AddEmptyElseToConditional(),
        new AddEmptyTrueBlock()}; // TODO needs to be extended

    List<Program> refactoredPrograms = new ArrayList<>();
    // TODO some smart way of enumerating and combination of refactorings is needed
    for (String function : referenceProgram.getFncs().keySet()) {
      for (RefactoringRule rule : rules) {
        for (int location : targetLocations) {
          Optional<Program> refactoring =
              refactorGenerator.refactorProgram(referenceProgram, rule, function, location);
          if (refactoring.isPresent()) {
            refactoredPrograms.add(refactoring.get());
          }
        }
      }
    }

    /* 2. Identify matching reference program. */
    Program refactoredReferenceProgram = null;
    CfgBasedStructuralAlignment structAlign = new CfgBasedStructuralAlignment();
    VariableAlignment varAlign = new VariableMappingByDefUseAnalysis();
    StructuralMapping cfgMapping = null;
    VariableMapping varMapping = null;
    for (Program refactoredProgram : refactoredPrograms) {
      try {
        cfgMapping = structAlign.generateStructuralAlignment(referenceProgram, submittedProgram);
        varMapping =
            varAlign.generateVariableAlignment(referenceProgram, submittedProgram, cfgMapping);
        if (cfgMapping != null && !cfgMapping.getAllMappings().isEmpty() && varMapping != null
            && !varMapping.getAllMappings().isEmpty()) {
          refactoredReferenceProgram = refactoredProgram;
          break;
        }
      } catch (AlignmentException e) {
        // continue searching
      }
    }

    if (refactoredReferenceProgram == null) {
      throw new Exception("Unable to find matching refactoring of the referencep program.");
    }

    /* 3. Detect error location. */
    ErrorLocalisation errorLocations =
        new BasicErrorLocalizer().localizeErrors(submittedProgram, referenceProgram, inputs,
            Constants.DEFAULT_ENTRY_FUNCTION_NAME, cfgMapping, varMapping, interpreter);

    /* 4. Repair the submitted program with the refactored reference program */
    MutationRepair mutationRepair = new MutationRepair();
    List<RepairCandidate> repairs = mutationRepair.repair(refactoredReferenceProgram,
        submittedProgram, errorLocations, varMapping, inputs, interpreter);
    return repairs;
  }
}

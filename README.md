# its-repair-refactoring-template

## Overview
As repair we denote the fixing of the submitted program by reusing the existing reference implementation. At this stage, the project assumes that the two programs are presented in some CFG-based representation. However, the general assumption that reference program and submitted program can be fully aligned, does not always hold because the CFG representation of various submitted programs may differ a lot from the reference solution. Therefore, in this project, the students are asked to implement a repair approach based on refactoring rules which increase the possibility of aligning the two programs and fix the submitted program by expression mutation. In general, this specific approach requires four steps:

1. Design and implement a set of refactoring rules that change the syntactic structure but preserve the semantic meaning of the reference program.
2. Design and implement a set of expression mutation operators for the program CFG-representation.
3. Apply the refactoring rules on reference programs to generate a set of refactored programs and align the refactored programs and the submitted program using existing alignment techniques.
4. Apply the expression mutation operators on the submitted program to find the best (minimal) repair.


## Entry Points

* This projects has a local dependency to the interpreter, error localizer, and alignment. Please use the provided [install.sh](./install.sh) to install the provided [Java Archive](./its-repair-refactoring/lib/) files.

* You can use the alignment to structurally match to program model objects. The following snippet shows an example. You find more details on the alignment in its interface description: [sg.edu.nus.se.its.alignment.StructuralAlignment](./its-core/src/main/java/sg/edu/nus/se/its/alignment/StructuralAlignment.java) and [sg.edu.nus.se.its.alignment.VariableAlignment](./its-core/src/main/java/sg/edu/nus/se/its/alignment/VariableAlignment.java)

```
Program referenceProgram = TestUtils.loadProgramByName("c1.c");
Program submittedProgram = TestUtils.loadProgramByName("i1.c");
    
CfgBasedStructuralAlignment structAlign = new CfgBasedStructuralAlignment();
VariableAlignment varAlign = new VariableMappingByDefUseAnalysis();

try {
   StructuralMapping cfgMapping = structAlign.generateStructuralAlignment(referenceProgram, submittedProgram);
   VariableMapping varMapping = varAlign.generateVariableAlignment(referenceProgram, submittedProgram, cfgMapping);
   ...
} catch (AlignmentException e) {
  ...
}

```

* You can use the interpreter to execute a program model object. The following snippet shows an example. You find more details on the interpreter in its interface description: [sg.edu.nus.se.its.interpreter.Interpreter](./its-core/src/main/java/sg/edu/nus/se/its/interpreter/Interpreter.java).

```
Interpreter cInterpreter = new Interpreter4C(50000, Constants.DEFAULT_ENTRY_FUNCTION_NAME);
Program program = TestUtils.loadProgramByName("arith.c");
Trace trace = cInterpreter.executeProgram(program);
```

* You can use the error localizer to detect faulty locations in a program model object. The following snippet shows an example. You find more details on the error localizer in its interface description: [sg.edu.nus.se.its.errorlocalizer.ErrorLocalizer](./its-core/src/main/java/sg/edu/nus/se/its/errorlocalizer/ErrorLocalizer.java).

```
Program referenceProgram = TestUtils.loadProgramByName("c1.c");
Program submittedProgram = TestUtils.loadProgramByName("i1.c");
List<Input> inputs = Collections.emptyList();

Map<Integer, Integer> locations = new HashMap<>();
locations.put(1, 1);
StructuralMapping cfgMapping = new StructuralMapping();
cfgMapping(Constants.DEFAULT_ENTRY_FUNCTION_NAME, locations);

Map<Variable, Variable> variables = new HashMap<>();
variables.put(new Variable(Constants.VAR_COND), new Variable(Constants.VAR_COND));
variables.put(new Variable(Constants.VAR_RET), new Variable(Constants.VAR_RET));
variables.put(new Variable(Constants.VAR_OUT), new Variable(Constants.VAR_OUT));
variables.put(new Variable(Constants.VAR_IN), new Variable(Constants.VAR_IN));
variables.put(new Variable("a"), new Variable("x"));
variables.put(new Variable("b"), new Variable("y"));

VariableMapping varMapping = new VariableMapping();
varMapping.add(Constants.DEFAULT_ENTRY_FUNCTION_NAME, variables);

Interpreter interpreter = new Interpreter4C(50000, Constants.DEFAULT_ENTRY_FUNCTION_NAME);

ErrorLocalizer errorLocalizer = new BasicErrorLocalizer();
ErrorLocalisation errorLocations = errorLocalizer.localizeErrors(submittedProgram,
        referenceProgram, inputs, Constants.DEFAULT_ENTRY_FUNCTION_NAME, cfgMapping,
        varMapping, interpreter);
```

* You can already find an example for implementing the overall workflow, which you will need to extend: [sg.edu.nus.se.its.refactoring.RefactoringRepairWorkflow](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/RefactoringRepairWorkflow.java)
```
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
    ...
}
```

* The refactoring [sg.edu.nus.se.its.refactoring.Refactoring](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/Refactoring.java) must follow the corresponding interface [sg.edu.nus.se.its.refactoring.Refactor](./its-core/src/main/java/sg/edu/nus/se/its/refactoring/Refactor.java).

```
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
```

* The refactoring rules need to follow the [sg.edu.nus.se.its.refactoring.RefactoringRule](./its-core/src/main/java/sg/edu/nus/se/its/refactoring/RefactoringRule.java) interface.

```
/**
 * Refactoring rules interface.
 */
public interface RefactoringRule {
  /**
   * Refactor the provided program at the given function name and location.
   * Returns an empty Optional if the refactoring rule cannot be applied at the
   * given function name and location.
   *
   * @param program      the program to refactor
   * @param functionName the function name to apply refactoring
   * @param location     the location to apply refactoring
   * @return the refactored program
   */
  public Optional<Program> applyRule(Program program, String functionName, int location);
}
```

* We already created empty classes for [sg.edu.nus.se.its.refactoring.rules.AddEmptyElseToConditional](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddEmptyElseToConditional.java), [sg.edu.nus.se.its.refactoring.rules.AddEmptyTrueBlock](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddEmptyTrueBlock.java), [sg.edu.nus.se.its.refactoring.rules.AddConditionalToTrueBranch](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddConditionalToTrueBranch.java). You will need to implement these and extend the set of refactoring rules.

* You need to implement the mutation-based repair class [sg.edu.nus.se.its.refactoring.MutationRepair](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/MutationRepair.java).

```
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
```

* You can use `mvn clean compile test` to build and test your implementation.

## Restrictions
* You are not allowed to change any code in the [sg.edu.nus.its.its-core](./its-core).
* You need to stick to the provided interfaces.
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.RefactoringRepairWorkflow](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/RefactoringRepairWorkflow.java).
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.Refactoring](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/Refactoring.java).
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.rules.AddEmptyElseToConditional](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddEmptyElseToConditional.java).
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.rules.AddEmptyTrueBlock](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddEmptyTrueBlock.java).
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.rules.AddConditionalToTrueBranch](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/rules/AddConditionalToTrueBranch.java).
* You are not allowed to change the file/class name or move [sg.edu.nus.se.its.refactoring.MutationRepair](./its-repair-refactoring/src/main/java/sg/edu/nus/se/its/refactoring/MutationRepair.java).
* If you would require any other dependencies or libraries, you first need to seek approval by the tutors.
* You are not allowed to change any file within [.github](./.github).

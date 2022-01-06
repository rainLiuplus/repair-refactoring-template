package sg.edu.nus.se.its.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import sg.edu.nus.se.its.alignment.VariableMapping;
import sg.edu.nus.se.its.errorlocalizer.ErrorLocalisation;
import sg.edu.nus.se.its.errorlocalizer.ErrorLocation;
import sg.edu.nus.se.its.interpreter.Interpreter;
import sg.edu.nus.se.its.interpreter.Interpreter4C;
import sg.edu.nus.se.its.model.Constant;
import sg.edu.nus.se.its.model.Input;
import sg.edu.nus.se.its.model.Operation;
import sg.edu.nus.se.its.model.Program;
import sg.edu.nus.se.its.model.Variable;
import sg.edu.nus.se.its.refactoring.rules.AddConditionalToTrueBranch;
import sg.edu.nus.se.its.refactoring.rules.AddEmptyElseToConditional;
import sg.edu.nus.se.its.refactoring.rules.AddEmptyTrueBlock;
import sg.edu.nus.se.its.repair.LocalRepair;
import sg.edu.nus.se.its.repair.RepairCandidate;
import sg.edu.nus.se.its.util.Constants;
import sg.edu.nus.se.its.util.TestUtils;

/**
 * Basic tests for the refactoring-based repair.
 */
public class BasicTest {

  private static Interpreter cInterpreter;

  private static Interpreter getInterpreter4C() {
    if (cInterpreter == null) {
      cInterpreter = new Interpreter4C(50000, Constants.DEFAULT_ENTRY_FUNCTION_NAME);
    }
    return cInterpreter;
  }

  final String main = Constants.DEFAULT_ENTRY_FUNCTION_NAME;

  /* Tests for Refactoring. */

  @Test
  void testRefactoring_SimpleProgramEmptyBlock_Success() {
    Program program = TestUtils.loadProgramByName("c1.c");
    assert program != null;

    AddEmptyTrueBlock addEmptyTrueBlock = new AddEmptyTrueBlock();
    Refactor refactor = new Refactoring();
    Optional<Program> result = refactor.refactorProgram(program, addEmptyTrueBlock, main, 1);

    assertFalse(result.isEmpty());
    Program refactoredProgram = result.get();

    assertEquals("dummy block inserted by refactoring rules",
        refactoredProgram.getfnc(main).getLocdescAt(2));
    assertEquals(2, refactoredProgram.getfnc(main).getTrans(1, true));
  }

  @Test
  void testRefactoring_ComplexProgramEmptyBlock_Success() {
    Program program = TestUtils.loadProgramByName("c6.c");
    assert program != null;

    AddEmptyTrueBlock addEmptyTrueBlock = new AddEmptyTrueBlock();
    Refactor refactor = new Refactoring();
    Optional<Program> result = refactor.refactorProgram(program, addEmptyTrueBlock, main, 4);

    assertFalse(result.isEmpty());
    Program refactoredProgram = result.get();

    assertEquals("dummy block inserted by refactoring rules",
        refactoredProgram.getfnc(main).getLocdescAt(6));
    assertEquals(6, refactoredProgram.getfnc(main).getTrans(4, true));
    assertEquals(2, refactoredProgram.getfnc(main).getTrans(6, true));
    assertEquals(2, refactoredProgram.getfnc(main).getTrans(6, false));
  }

  @Test
  void testRefactoring_ComplexProgramAddConditional_Success() {
    Program program = TestUtils.loadProgramByName("c6.c");
    assert program != null;

    AddConditionalToTrueBranch testRule = new AddConditionalToTrueBranch();
    Refactor refactor = new Refactoring();
    Optional<Program> result = refactor.refactorProgram(program, testRule, main, 5);

    assertFalse(result.isEmpty());
    Program refactoredProgram = result.get();

    assertEquals("dummy if conditional inserted by refactoring rules",
        refactoredProgram.getfnc(main).getLocdescAt(6));
    assertEquals(6, refactoredProgram.getfnc(main).getTrans(5, true));
    assertEquals(4, refactoredProgram.getfnc(main).getTrans(6, true));
    assertNull(refactoredProgram.getfnc(main).getTrans(6, false));
  }

  @Test
  void testRefactoring_AddEmptyElseToConditionalBlock_Success() {
    Program program = TestUtils.loadProgramByName("c6.c");

    AddEmptyElseToConditional testRule = new AddEmptyElseToConditional();
    Refactor refactor = new Refactoring();
    Optional<Program> result = refactor.refactorProgram(program, testRule, main, 2);

    assertFalse(result.isEmpty());
    Program refactoredProgram = result.get();

    assertEquals("dummy block inserted by refactoring rules",
        refactoredProgram.getfnc(main).getLocdescAt(6));
    assertEquals(5, refactoredProgram.getfnc(main).getTrans(2, true));
    assertEquals(6, refactoredProgram.getfnc(main).getTrans(2, false));
    assertEquals(3, refactoredProgram.getfnc(main).getTrans(6, true));
    assertEquals(3, refactoredProgram.getfnc(main).getTrans(6, false));
  }

  @Test
  void testRefactoring_DoesNotModifyOriginalProgram_Success() {
    Program program = TestUtils.loadProgramByName("c6.c");

    AddEmptyElseToConditional testRule = new AddEmptyElseToConditional();
    Refactor refactor = new Refactoring();
    refactor.refactorProgram(program, testRule, main, 2);

    assertNotNull(program);
    assertNull(program.getfnc(main).getLocexprs().get(6));
  }

  @Test
  void testRefactoring_AddEmptyElseToNotConditionalBlock_ReturnEmpty() {
    Program program = TestUtils.loadProgramByName("c6.c");

    AddEmptyElseToConditional testRule = new AddEmptyElseToConditional();
    Refactor refactor = new Refactoring();
    Optional<Program> result = refactor.refactorProgram(program, testRule, main, 1);

    assertTrue(result.isEmpty());
  }

  /* Tests for Repair. */

  @Test
  void testTrivialFix() throws Exception {
    Program referenceSolution = TestUtils.loadProgramByName("c1.c");
    Program submittedProgram = TestUtils.loadProgramByName("i1.c");
    int index = 1;

    VariableMapping mappings = TestUtils.hardcodeVariableMapping(index);
    ErrorLocalisation errorLocations = new ErrorLocalisation();
    for (Map<Variable, Variable> mapping : mappings
        .getMappings(Constants.DEFAULT_ENTRY_FUNCTION_NAME)) {
      errorLocations.addLocation(Constants.DEFAULT_ENTRY_FUNCTION_NAME, mapping,
          new ErrorLocation(1, 1));
    }

    RepairCandidate repairCandidates =
        new MutationRepair().repair(referenceSolution, submittedProgram, errorLocations, mappings,
            Collections.emptyList(), getInterpreter4C()).get(0);
    List<LocalRepair> chosenRepairs = repairCandidates.getLocalRepairs();

    int expectedRepairNum = 1;
    assertEquals(expectedRepairNum, chosenRepairs.size());
    for (LocalRepair chosenRepair : chosenRepairs) {
      if (chosenRepair.getErrorLocation().getValue0() == 1) {
        if (chosenRepair.getRepairedVariable().getValue0().getUnprimedName().equals("y")) {
          assertEquals(Operation.class, chosenRepair.getRepairedVariable().getValue2().getClass());
          Operation operation = (Operation) chosenRepair.getRepairedVariable().getValue2();
          assertEquals("+", operation.getName());
          assertEquals(2, operation.getArgs().size());
          assertEquals(Constant.class, operation.getArgs().get(0).getClass());
          assertEquals(Variable.class, operation.getArgs().get(1).getClass());
          Constant constant = (Constant) operation.getArgs().get(0);
          Variable variable = (Variable) operation.getArgs().get(1);
          assertEquals("1", constant.getValue());
          assertEquals("x'", variable.getName());
        } else {
          fail(String.format("Unexpected repair occurred at location %d for variable %s",
              chosenRepair.getErrorLocation().getValue0(),
              chosenRepair.getRepairedVariable().getValue0()));
        }
      } else {
        fail(String.format("Unexpected repair at location %d",
            chosenRepair.getErrorLocation().getValue0()));
      }
    }
    String expectedRepairAsString = "Change y = 5 + x to y = 1 + x at location 1";

    assertEquals(expectedRepairAsString, LocalRepair.toString(chosenRepairs));
  }

  @Test
  void sameVariableNameTest() throws Exception {
    Program referenceSolution = TestUtils.loadProgramByName("c2.c");
    Program submittedProgram = TestUtils.loadProgramByName("i2.c");
    int index = 2;

    VariableMapping mappings = TestUtils.hardcodeVariableMapping(index);
    ErrorLocalisation errorLocations = new ErrorLocalisation();
    for (Map<Variable, Variable> mapping : mappings
        .getMappings(Constants.DEFAULT_ENTRY_FUNCTION_NAME)) {
      errorLocations.addLocation(Constants.DEFAULT_ENTRY_FUNCTION_NAME, mapping,
          new ErrorLocation(1, 1));
    }

    RepairCandidate repairCandidates =
        new MutationRepair().repair(referenceSolution, submittedProgram, errorLocations, mappings,
            Collections.emptyList(), getInterpreter4C()).get(0);
    List<LocalRepair> chosenRepairs = repairCandidates.getLocalRepairs();

    int expectedRepairNum = 1;
    assertEquals(expectedRepairNum, chosenRepairs.size());
    for (LocalRepair chosenRepair : chosenRepairs) {
      if (chosenRepair.getErrorLocation().getValue0() == 1) {
        if (chosenRepair.getRepairedVariable().getValue0().getUnprimedName().equals("b")) {
          assertEquals(Operation.class, chosenRepair.getRepairedVariable().getValue2().getClass());
          Operation operation = (Operation) chosenRepair.getRepairedVariable().getValue2();
          assertEquals("+", operation.getName());
          assertEquals(2, operation.getArgs().size());
          assertEquals(Constant.class, operation.getArgs().get(0).getClass());
          assertEquals(Variable.class, operation.getArgs().get(1).getClass());
          Constant constant = (Constant) operation.getArgs().get(0);
          Variable variable = (Variable) operation.getArgs().get(1);
          assertEquals("1", constant.getValue());
          assertEquals("a'", variable.getName());
        } else {
          fail(String.format("Unexpected repair occurred at location %d for variable %s",
              chosenRepair.getErrorLocation().getValue0(),
              chosenRepair.getRepairedVariable().getValue0()));
        }
      } else {
        fail(String.format("Unexpected repair at location %d",
            chosenRepair.getErrorLocation().getValue0()));
      }
    }
    assertEquals("Change b = 5 + a to b = 1 + a at location 1",
        LocalRepair.toString(chosenRepairs));
  }


  @Test
  void testIfConditionalFix() throws Exception {
    Program referenceSolution = TestUtils.loadProgramByName("c3.c");
    Program submittedProgram = TestUtils.loadProgramByName("i3.c");
    int index = 3;

    VariableMapping mappings = TestUtils.hardcodeVariableMapping(index);
    ErrorLocalisation errorLocations = new ErrorLocalisation();
    for (Map<Variable, Variable> mapping : mappings
        .getMappings(Constants.DEFAULT_ENTRY_FUNCTION_NAME)) {
      errorLocations.addLocation(Constants.DEFAULT_ENTRY_FUNCTION_NAME, mapping,
          new ErrorLocation(1, 1));
    }

    RepairCandidate repairCandidates =
        new MutationRepair().repair(referenceSolution, submittedProgram, errorLocations, mappings,
            Collections.emptyList(), getInterpreter4C()).get(0);
    List<LocalRepair> chosenRepairs = repairCandidates.getLocalRepairs();

    int expectedRepairNum = 1;
    assertEquals(expectedRepairNum, chosenRepairs.size());
    for (LocalRepair chosenRepair : chosenRepairs) {
      if (chosenRepair.getErrorLocation().getValue0() == 1) {
        if (chosenRepair.getRepairedVariable().getValue0().getUnprimedName().equals("z")) {
          assertEquals(Operation.class, chosenRepair.getRepairedVariable().getValue2().getClass());
          Operation operation = (Operation) chosenRepair.getRepairedVariable().getValue2();
          assertEquals("ite", operation.getName());
          assertEquals(3, operation.getArgs().size());
          assertEquals(Operation.class, operation.getArgs().get(0).getClass());
          Operation iteOperation = (Operation) operation.getArgs().get(0);
          assertEquals("==", iteOperation.getName());
          assertEquals(Variable.class, iteOperation.getArgs().get(0).getClass());
          assertEquals(Constant.class, iteOperation.getArgs().get(1).getClass());
          Variable variable1 = (Variable) iteOperation.getArgs().get(0);
          Constant constant1 = (Constant) iteOperation.getArgs().get(1);
          assertEquals("x'", variable1.getName());
          assertEquals("1", constant1.getValue());
          assertEquals(Constant.class, operation.getArgs().get(1).getClass());
          assertEquals(Constant.class, operation.getArgs().get(2).getClass());
          Constant constant2 = (Constant) operation.getArgs().get(1);
          Constant constant3 = (Constant) operation.getArgs().get(2);
          assertEquals("1", constant2.getValue());
          assertEquals("0", constant3.getValue());
        } else {
          fail(String.format("Unexpected repair occurred at location %d for variable %s",
              chosenRepair.getErrorLocation().getValue0(),
              chosenRepair.getRepairedVariable().getValue0()));
        }
      } else {
        fail(String.format("Unexpected repair at location %d",
            chosenRepair.getErrorLocation().getValue0()));
      }
    }
    assertEquals("Change z = ite(y == 1 , 1, 0) to z = ite(x == 1 , 1, 0) at location 1",
        LocalRepair.toString(chosenRepairs));
  }

  @Test
  void testLab3Example() throws Exception {
    // incorrect program obtained from
    // https://github.com/jyi/ITSP/blob/master/dataset/Lab-3/2811/270138_buggy.c
    // correct program obtained from
    // https://github.com/jyi/ITSP/blob/master/dataset/Lab-3/2811/270138_correct.c

    Program referenceSolution = TestUtils.loadProgramByName("c4.c");
    Program submittedProgram = TestUtils.loadProgramByName("i4.c");
    List<Input> inputs = TestUtils.loadInputsByProgramName("c4.c");
    int index = 4;

    VariableMapping mappings = TestUtils.hardcodeVariableMapping(index);
    ErrorLocalisation errorLocations = new ErrorLocalisation();
    for (Map<Variable, Variable> mapping : mappings
        .getMappings(Constants.DEFAULT_ENTRY_FUNCTION_NAME)) {
      errorLocations.addLocation(Constants.DEFAULT_ENTRY_FUNCTION_NAME, mapping,
          new ErrorLocation(1, 1));
    }

    RepairCandidate repairCandidates = new MutationRepair().repair(referenceSolution,
        submittedProgram, errorLocations, mappings, inputs, getInterpreter4C()).get(0);
    List<LocalRepair> chosenRepairs = repairCandidates.getLocalRepairs();

    assertEquals(3, chosenRepairs.size());
    for (LocalRepair localRepair : chosenRepairs) {
      if (localRepair.getErrorLocation().getValue0() == 1) {
        switch (localRepair.getRepairedVariable().getValue0().toString()) {
          case "X":
            assertEquals(Operation.class, localRepair.getRepairedVariable().getValue2().getClass());
            Operation operation = (Operation) localRepair.getRepairedVariable().getValue2();
            assertEquals("ite", operation.getName());
            assertEquals(3, operation.getArgs().size());
            assertEquals(Operation.class, operation.getArgs().get(0).getClass());

            Operation iteOperation = (Operation) operation.getArgs().get(0);
            assertEquals("==", iteOperation.getName());
            assertEquals(Operation.class, iteOperation.getArgs().get(0).getClass());
            assertEquals(Constant.class, iteOperation.getArgs().get(1).getClass());
            Operation operation1 = (Operation) iteOperation.getArgs().get(0);
            Constant constant1 = (Constant) iteOperation.getArgs().get(1);
            assertEquals("-(*(a1', b2'), *(a2', b1'))", operation1.toString(false));
            assertEquals("0", constant1.toString(false));

            assertEquals(Variable.class, operation.getArgs().get(1).getClass());
            Variable variable1 = (Variable) operation.getArgs().get(1);
            assertEquals("X", variable1.getName());

            assertEquals(Operation.class, operation.getArgs().get(2).getClass());
            Operation operation2 = (Operation) operation.getArgs().get(2);
            assertEquals("/(-(b2', b1'), -(*(b2', a1'), *(b1', a2')))", operation2.toString());

            assertEquals("Change X = ite(a1 / a2 == b1 / b2 , X, b2 - b1 / b2 * a1 - b1 * a2)"
                + " to X = ite(a1 * b2 - a2 * b1 == 0 , X, b2 - b1 / b2 * a1 - b1 * a2)"
                + " at location 1\n", LocalRepair.toString(localRepair));
            break;
          case "Y":
            assertEquals(Operation.class, localRepair.getRepairedVariable().getValue2().getClass());
            operation = (Operation) localRepair.getRepairedVariable().getValue2();
            assertEquals("ite", operation.getName());
            assertEquals(3, operation.getArgs().size());
            assertEquals(Operation.class, operation.getArgs().get(0).getClass());

            iteOperation = (Operation) operation.getArgs().get(0);
            assertEquals("==", iteOperation.getName());
            assertEquals(Operation.class, iteOperation.getArgs().get(0).getClass());
            assertEquals(Constant.class, iteOperation.getArgs().get(1).getClass());
            operation1 = (Operation) iteOperation.getArgs().get(0);
            constant1 = (Constant) iteOperation.getArgs().get(1);
            assertEquals("-(*(a1', b2'), *(a2', b1'))", operation1.toString(false));
            assertEquals("0", constant1.toString(false));

            assertEquals(Variable.class, operation.getArgs().get(1).getClass());
            variable1 = (Variable) operation.getArgs().get(1);
            assertEquals("Y", variable1.getName());

            assertEquals(Operation.class, operation.getArgs().get(2).getClass());
            operation2 = (Operation) operation.getArgs().get(2);
            assertEquals("/(-(a2', a1'), -(*(a2', b1'), *(b2', a1')))", operation2.toString());

            assertEquals("Change Y = ite(a1 / a2 == b1 / b2 , Y, a2 - a1 / a2 * b1 - b2 * a1)"
                + " to Y = ite(a1 * b2 - a2 * b1 == 0 , Y, a2 - a1 / a2 * b1 - b2 * a1)"
                + " at location 1\n", LocalRepair.toString(localRepair));
            break;
          case "$out":
            assertEquals(Operation.class, localRepair.getRepairedVariable().getValue2().getClass());
            operation = (Operation) localRepair.getRepairedVariable().getValue2();
            assertEquals("ite", operation.getName());
            assertEquals(3, operation.getArgs().size());
            assertEquals(Operation.class, operation.getArgs().get(0).getClass());

            iteOperation = (Operation) operation.getArgs().get(0);
            assertEquals("==", iteOperation.getName());
            assertEquals(Operation.class, iteOperation.getArgs().get(0).getClass());
            assertEquals(Constant.class, iteOperation.getArgs().get(1).getClass());
            operation1 = (Operation) iteOperation.getArgs().get(0);
            constant1 = (Constant) iteOperation.getArgs().get(1);
            assertEquals("-(*(a1', b2'), *(a2', b1'))", operation1.toString(false));
            assertEquals("0", constant1.toString(false));

            assertEquals(Operation.class, operation.getArgs().get(1).getClass());
            Operation operation3 = (Operation) operation.getArgs().get(1);
            assertEquals("StrAppend($out, StrFormat(\"INF\"))", operation3.toString(false));

            assertEquals(Operation.class, operation.getArgs().get(2).getClass());
            operation2 = (Operation) operation.getArgs().get(2);
            assertEquals(
                "StrAppend($out, StrFormat(\"(%.3f,%.3f)\", /(-(b2', b1'), "
                    + "-(*(b2', a1'), *(b1', a2'))), /(-(a2', a1'), -(*(a2', b1'), *(b2', a1')))))",
                operation2.toString());

            assertEquals(
                "Change $out = ite(a1 / a2 == b1 / b2 , $out StrAppend \"INF\" StrForm "
                    + "StrAppe, $out StrAppend \"(%.3f,%.3f)\" StrFormat b2 - b1 / b2 * a1 - "
                    + "b1 * a2 StrFormat a2 - a1 / a2 * b1 - b2 * a1 StrForm StrAppe) to "
                    + "$out = ite(a1 * b2 - a2 * b1 == 0 , $out StrAppend \"INF\" StrForm StrAppe, "
                    + "$out StrAppend \"(%.3f,%.3f)\" StrFormat b2 - b1 / b2 * a1 - b1 * a2 "
                    + "StrFormat a2 - a1 / a2 * b1 - b2 * a1 StrForm StrAppe) at location 1\n",
                LocalRepair.toString(localRepair));
            break;
          default:
            fail("Test not run successfully");
        }
      } else {
        fail(String.format("Unexpected repair at location %d for variable %s",
            localRepair.getErrorLocation().getValue0(),
            localRepair.getRepairedVariable().getValue0().toString()));
      }
    }

  }

  @Test
  void testTrivialFixForWorkflow() throws Exception {
    Program referenceSolution = TestUtils.loadProgramByName("c1.c");
    Program submittedProgram = TestUtils.loadProgramByName("i1.c");
    int index = 1;

    VariableMapping mappings = TestUtils.hardcodeVariableMapping(index);
    ErrorLocalisation errorLocations = new ErrorLocalisation();
    for (Map<Variable, Variable> mapping : mappings
        .getMappings(Constants.DEFAULT_ENTRY_FUNCTION_NAME)) {
      errorLocations.addLocation(Constants.DEFAULT_ENTRY_FUNCTION_NAME, mapping,
          new ErrorLocation(1, 1));
    }

    RepairCandidate repairCandidates = new RefactoringRepairWorkflow()
        .repair(referenceSolution, submittedProgram, Collections.emptyList(), getInterpreter4C())
        .get(0);
    List<LocalRepair> chosenRepairs = repairCandidates.getLocalRepairs();

    int expectedRepairNum = 1;
    assertEquals(expectedRepairNum, chosenRepairs.size());
    for (LocalRepair chosenRepair : chosenRepairs) {
      if (chosenRepair.getErrorLocation().getValue0() == 1) {
        if (chosenRepair.getRepairedVariable().getValue0().getUnprimedName().equals("y")) {
          assertEquals(Operation.class, chosenRepair.getRepairedVariable().getValue2().getClass());
          Operation operation = (Operation) chosenRepair.getRepairedVariable().getValue2();
          assertEquals("+", operation.getName());
          assertEquals(2, operation.getArgs().size());
          assertEquals(Constant.class, operation.getArgs().get(0).getClass());
          assertEquals(Variable.class, operation.getArgs().get(1).getClass());
          Constant constant = (Constant) operation.getArgs().get(0);
          Variable variable = (Variable) operation.getArgs().get(1);
          assertEquals("1", constant.getValue());
          assertEquals("x'", variable.getName());
        } else {
          fail(String.format("Unexpected repair occurred at location %d for variable %s",
              chosenRepair.getErrorLocation().getValue0(),
              chosenRepair.getRepairedVariable().getValue0()));
        }
      } else {
        fail(String.format("Unexpected repair at location %d",
            chosenRepair.getErrorLocation().getValue0()));
      }
    }
    String expectedRepairAsString = "Change y = 5 + x to y = 1 + x at location 1";

    assertEquals(expectedRepairAsString, LocalRepair.toString(chosenRepairs));
  }

}

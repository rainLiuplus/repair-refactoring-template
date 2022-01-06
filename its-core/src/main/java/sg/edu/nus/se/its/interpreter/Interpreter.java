package sg.edu.nus.se.its.interpreter;

import sg.edu.nus.se.its.model.Constant;
import sg.edu.nus.se.its.model.Function;
import sg.edu.nus.se.its.model.Input;
import sg.edu.nus.se.its.model.Memory;
import sg.edu.nus.se.its.model.Operation;
import sg.edu.nus.se.its.model.Program;
import sg.edu.nus.se.its.model.Variable;

/**
 * Interfaces for the interpretation of the program execution.
 */
public interface Interpreter {

  public Trace executeProgram(Program program);

  public Trace executeProgram(Program program, Input input);

  public Object execute(Executable executable, Memory memory);

  public Object executeFunction(Function function, Memory memory);

  public Object executeConstant(Constant constant, Memory memory);

  public Object executeOperation(Operation operation, Memory memory);

  public Object executeVariable(Variable variable, Memory memory);

  public void setTimeout(int timeout);

}

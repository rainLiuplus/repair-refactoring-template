package sg.edu.nus.se.its.model;

import java.util.List;
import java.util.Set;
import sg.edu.nus.se.its.interpreter.Executable;
import sg.edu.nus.se.its.util.JsonSerializable;

/**
 * Represents an expression in the program.
 */
public abstract class Expression implements Executable, JsonSerializable {

  /**
   * The source line number of the expression.
   */
  private int line;

  /**
   * Not used for now, but can hold the original names when introduces "primed" variables.
   */
  private List<?> original;

  /**
   * Initiates an expression.
   *
   * @param line -- line number in the source code
   * @param isStatement -- determines whether the expression is used in a statement
   * @param original -- (not used at the moment), can be kept null
   */
  protected Expression(int line, List<?> original) {
    super();
    this.line = line;
    this.original = original;
  }

  protected String exprOriginal(String s) {
    if (original == null) {
      return s;
    }
    return String.format("%s{%s, %s}", s, line, original);
  }

  /**
   * Method call to get string representation of expression. The original field is included by
   * default.
   *
   * @return string representation of expression
   */
  public String toString() {
    return toString(true);
  }

  /**
   * Method call to get string representation of expression.
   *
   * @param withOriginalField denotes if original field is to be included
   * @return string representation of expression
   */
  public abstract String toString(boolean withOriginalField);

  public int getLineNumber() {
    return line;
  }

  public Expression prime() {
    return this;
  }

  /**
   * Method to prime variables given a set of already primed variables.
   *
   * @param name set of expressions to prime
   * @return primed expression if it has to be primed
   */
  public Expression prime(Set<String> name) {
    return this;
  }

  public Expression unprime() {
    return this;
  }

  /**
   * Method call to replace an expression iff the particular expression matches varName. Note that
   * if this expression object is not called varName, it will just return this expression unchanged.
   *
   * @param varName variable which needs to be replaced
   * @param expression expression to replace variable varName with
   * @return final expression after replacement if necessary
   */
  public Expression replace(String varName, Expression expression) {
    return this;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return this;
  }
}

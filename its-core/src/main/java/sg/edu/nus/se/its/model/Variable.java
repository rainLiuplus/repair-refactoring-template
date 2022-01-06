package sg.edu.nus.se.its.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import sg.edu.nus.se.its.interpreter.Interpreter;

/**
 * Represents a variable expression.
 */
public class Variable extends Expression implements Cloneable {

  private String name;

  /**
   * Determines whether the variable is from the reference (i.e., prime = false) or submitted (i.e.,
   * prime=true) program.
   */
  private boolean primed = false;

  public Variable(String name, int line, List<?> original) {
    super(line, original);
    this.name = name;
  }

  public Variable(String name, int line) {
    super(line, null);
    this.name = name;
  }

  // to create temporary variables
  public Variable(String name) {
    super(0, null);
    this.name = name;
  }

  /**
   * Returns the variable name as String, including the "'" for primed variables.
   */
  public String getName() {
    if (isPrimed()) {
      return name + "'";
    }
    return name;
  }

  /**
   * Returns the variable name as String, excluding the "'" for primed variables.
   */
  public String getUnprimedName() {
    return name;
  }

  /**
   * Returns the variable name as String, excluding the "'" for primed variables.
   */
  public static String getUnprimedName(String name) {
    return name;
  }

  /**
   * Returns the variable name as String, including the "'" for primed variables.
   */
  public String getPrimedName() {
    return name + "'";
  }

  /**
   * Sets the prime status of the variable, without generating any clone.
   */
  public void setPrimed(boolean primed) {
    this.primed = primed;
  }

  /**
   * Returns whether the variable is primed. For instance, a primed variable v' is part of the
   * submitted (incorrect) program, while the unprimed variable v is part of the reference program.
   */
  public boolean isPrimed() {
    return primed;
  }

  /**
   * Returns whether variable name belongs to a primed variable.
   */
  public static boolean isPrimedName(String variableName) {
    return variableName.endsWith("'");
  }

  /**
   * Adds the "primed" indicator to the variable name. Note that this method does not check whether
   * the prime already exists, which can lead to unwanted side-effects.
   */
  public static String asPrimedVariableName(String variableName) {
    return variableName + '\'';
  }

  /**
   * Removes the "primed" indicator to the variable name. Note that this method does not check
   * whether the prime actually exists, which can lead to unwanted side-effects.
   */
  public static String asUnprimedVariableName(String variableName) {
    return variableName.substring(0, variableName.length() - 1);
  }

  /**
   * Retrieves the unprimed counterpart of the given variable.
   */
  public static Variable createUnprimedClone(Variable var) {
    if (!var.isPrimed()) {
      throw new RuntimeException(String.format("Var %s is already not primed!", var.getName()));
    }
    try {
      Variable result = (Variable) var.clone();
      result.setPrimed(false);
      return result;
    } catch (CloneNotSupportedException e) {
      // should not happen
      return null;
    }
  }

  @Override
  public String toString(boolean withOriginalField) {
    return exprOriginal(getName());
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    Variable cloneVariable = new Variable(this.name, this.getLineNumber());
    cloneVariable.setPrimed(this.isPrimed());
    return cloneVariable;
  }

  @Override
  public String getType() {
    return getClass().getSimpleName();
  }

  @Override
  public Object execute(Memory memory, Interpreter withInterpreter) {
    return withInterpreter.executeVariable(this, memory);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Variable) {
      Variable variable = (Variable) o;
      return Objects.equals(
          Variable.isPrimedName(name) ? Variable.asUnprimedVariableName(name) : name,
          Variable.isPrimedName(variable.name) ? Variable.asUnprimedVariableName(variable.name)
              : variable.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, primed, getLineNumber());
  }

  @Override
  public Expression prime() {
    this.setPrimed(true);
    return this;
  }

  @Override
  public Expression prime(Set<String> name) {
    for (String varName : name) {
      if (this.name.equals(varName)) {
        this.setPrimed(true);
        return this;
      }
    }
    return this;
  }

  @Override
  public Expression replace(String varName, Expression expression) {
    if (name.equals(varName)) {
      try {
        return (Expression) expression.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    }
    return this;
  }

  @Override
  public Expression unprime() {
    this.setPrimed(false);
    return this;
  }
}

package sg.edu.nus.se.its.model;

import java.util.List;
import java.util.Objects;
import sg.edu.nus.se.its.interpreter.Interpreter;

/**
 * Represents a constant expression.
 */
public class Constant extends Expression {

  private String value;

  public Constant(String value, int line, boolean statement, List<?> original) {
    super(line, original);
    this.value = value;
  }

  public Constant(String value, int line) {
    super(line, null);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString(boolean withOriginalField) {
    if (withOriginalField) {
      return exprOriginal(value);
    }
    return value;
  }

  @Override
  public String getType() {
    return getClass().getSimpleName();
  }

  @Override
  public Object execute(Memory memory, Interpreter withInterpreter) {
    return withInterpreter.executeConstant(this, memory);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Constant) {
      Constant constant = (Constant) o;
      return Objects.equals(value, constant.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}

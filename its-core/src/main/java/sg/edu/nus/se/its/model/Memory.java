package sg.edu.nus.se.its.model;

import java.util.HashMap;

/**
 * Maps variable names to their values.
 */
public class Memory extends HashMap<String, Object> {

  private static final long serialVersionUID = 1L;

  public Memory() {
    super();
  }

  public Object getValueForVariable(String variableName) {
    return this.get(variableName);
  }
}

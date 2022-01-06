package sg.edu.nus.se.its.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sg.edu.nus.se.its.util.JsonSerializable;

/**
 * Internal data structure to represent a program.
 */
public class Program implements JsonSerializable {

  // importStatements contains a list of header statements, this is language dependent!
  private List<String> importStatements;
  private Map<String, Function> fncs;
  private Map<String, Function> meta;
  private Map<String, Function> warns;
  private Map<String, Function> loops;

  /**
   * Initializes the empty program object.
   */
  public Program() {
    fncs = new HashMap<>();
    meta = new HashMap<>();
    warns = new HashMap<>();
    loops = new HashMap<>();
    importStatements = new ArrayList<>();
  }

  public Map<String, Function> getFncs() {
    return fncs;
  }

  public Map<String, Function> getMeta() {
    return meta;
  }

  public Map<String, Function> getWarns() {
    return warns;
  }

  public Map<String, Function> getLoops() {
    return loops;
  }

  public void addfnc(Function fnc) {
    this.fncs.put(fnc.getName(), fnc);
  }

  public Function getfnc(String fncName) {
    return this.fncs.get(fncName);
  }

  public Function getFunctionForName(String fncName) {
    return this.fncs.get(fncName);
  }

  public void setImportStatements(List<String> importStatements) {
    this.importStatements = importStatements;
  }

  public List<String> getImportStatements() {
    return this.importStatements;
  }

  @Override
  public String toString() {
    String programString = "\n\n";
    for (Function fnc : this.fncs.values()) {
      programString += fnc.toString();
    }
    return programString;
  }

  @Override
  public String getType() {
    return getClass().getSimpleName();
  }

}

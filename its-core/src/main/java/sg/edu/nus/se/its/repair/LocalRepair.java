package sg.edu.nus.se.its.repair;

import java.util.List;
import java.util.Map;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import sg.edu.nus.se.its.model.Constant;
import sg.edu.nus.se.its.model.Expression;
import sg.edu.nus.se.its.model.Operation;
import sg.edu.nus.se.its.model.Variable;

/**
 * Represents one possible repair repair at one location for a given possible variable mapping.
 */
public class LocalRepair {

  private Map<Variable, Variable> mapping; // mapping used for the particular repair
  private float cost; // cost associated with that particular fix

  // variable to repair, expression in incorrect program,
  // followed by expression in correct program
  private Triplet<Variable, Expression, Expression> repairedVariable;
  private Pair<Integer, Integer> errorLocation; // (mapping of the erroneous blocks)
  private String funcName; // name of function being fixed

  /**
   * Constructor that represents one possible repair candidate. The repair candidate may not always
   * be valid, or need a sequence of RepairCandidates to fix the problem.
   *
   * @param mapping Mapping of variables for particular repair
   * @param cost incurred for particular repair
   * @param repairedVariable variable to be repaired with the repaired expression
   * @param errorLocation error location repair should take place in
   * @throws Exception when error location or mapping is not valid
   */
  public LocalRepair(Map<Variable, Variable> mapping, float cost,
      Triplet<Variable, Expression, Expression> repairedVariable, String funcName,
      Pair<Integer, Integer> errorLocation) throws Exception {
    this.mapping = mapping;
    this.cost = cost;
    this.repairedVariable = repairedVariable;
    this.errorLocation = errorLocation;
    this.funcName = funcName;
  }

  /**
   * Generates pretty printed String representation of the given list of repair candidates.
   */
  public static String toString(List<LocalRepair> listOfLocalRepairs) {
    StringBuilder prettyPrintRepair = new StringBuilder();
    for (LocalRepair localRepair : listOfLocalRepairs) {
      prettyPrintRepair.append(toString(localRepair));
    }
    String result = prettyPrintRepair.toString();
    // remove trailing \n
    if (result.length() > 1 && result.endsWith("\n")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  /**
   * Generates pretty printed String representation of the given repair candidates.
   */
  public static String toString(LocalRepair localRepair) {
    StringBuilder prettyPrintRepair = new StringBuilder();
    Expression expr1 = localRepair.repairedVariable.getValue1();
    Expression expr2 = localRepair.repairedVariable.getValue2();
    Variable var = localRepair.repairedVariable.getValue0();
    Integer loc1 = localRepair.errorLocation.getValue1();

    if (expr1 != null && expr2 != null) {
      prettyPrintRepair.append("Change ").append(var).append(" = ").append(prettyPrintExpr(expr1))
          .append(" to ").append(var).append(" = ").append(prettyPrintExpr(expr2));

    } else if (expr1 == null) {
      // need to delete expression
      prettyPrintRepair.append("Add ").append(var).append(" = ").append(prettyPrintExpr(expr2));
    } else {
      prettyPrintRepair.append("Delete ").append(var).append(" = ").append(prettyPrintExpr(expr1));
    }
    prettyPrintRepair.append(" at location ").append(loc1);
    prettyPrintRepair.append("\n");
    return prettyPrintRepair.toString();
  }

  private static String prettyPrintExpr(Expression expr) {
    if (expr instanceof Constant) {
      return ((Constant) expr).getValue();
    } else if (expr instanceof Variable) {
      String name = expr.toString();
      return Variable.isPrimedName(name) ? Variable.asUnprimedVariableName(name) : name;
    } else if (expr instanceof Operation) {
      if (((Operation) expr).getName().equals("ite")) {
        return prettyPrintIteExpr((Operation) expr);
      }
      List<Expression> args = ((Operation) expr).getArgs();
      StringBuilder prettyPrint = new StringBuilder();
      for (Expression arg : args) {
        prettyPrint.append(prettyPrintExpr(arg));
        prettyPrint.append(" ").append(((Operation) expr).getName()).append(" ");
      }

      return prettyPrint.substring(0, prettyPrint.length() - 3);
    }
    return " ";
  }

  /**
   * Print out special case of if-then statements.
   *
   * @param expr Operation 'ite'
   * @return Representative version of
   */
  private static String prettyPrintIteExpr(Operation expr) {
    List<Expression> args = expr.getArgs();
    StringBuilder prettyPrint = new StringBuilder();
    prettyPrint.append("ite(");
    for (Expression arg : args) {
      prettyPrint.append(prettyPrintExpr(arg));
      prettyPrint.append(", ");
    }
    prettyPrint.delete(prettyPrint.length() - 2, prettyPrint.length()); // remove trailing ", "
    prettyPrint.append(")");

    return prettyPrint.toString();
  }

  public Map<Variable, Variable> getMapping() {
    return mapping;
  }

  public float getCost() {
    return cost;
  }

  public void setCost(float cost) {
    this.cost = cost;
  }

  public Triplet<Variable, Expression, Expression> getRepairedVariable() {
    return repairedVariable;
  }

  public void setRepairedVariable(Triplet<Variable, Expression, Expression> repairedVariable) {
    this.repairedVariable = repairedVariable;
  }

  public Pair<Integer, Integer> getErrorLocation() {
    return errorLocation;
  }

  public String getFuncName() {
    return funcName;
  }
}

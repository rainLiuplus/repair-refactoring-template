package sg.edu.nus.se.its.repair;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent one set of consistent fixes necessary to correct an incorrect program. Note
 * that not all repair strategies would return multiple sets of complete repairs.
 */
public class RepairCandidate {

  private List<LocalRepair> localRepairs = new ArrayList<>();

  public RepairCandidate() {}

  public RepairCandidate(List<LocalRepair> localRepairs) {
    this.localRepairs.addAll(localRepairs);
  }

  public List<LocalRepair> getLocalRepairs() {
    return localRepairs;
  }

  public boolean addLocalRepair(LocalRepair localRepair) {
    return localRepairs.add(localRepair);
  }

}

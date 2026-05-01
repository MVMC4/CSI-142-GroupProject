package com.ub.project.dispatch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ub.project.disasters.AbstractDisaster;
import com.ub.project.disasters.DisasterStatus;
import com.ub.project.responders.Responder;
import com.ub.project.store.DataStore;
import com.ub.project.util.Logging;

public class Dispatch {

    public enum DispatchStatus  { PENDING, DISPATCHED, IN_PROGRESS, COMPLETED, CANCELLED }
    public enum DispatchPriority { LOW, MODERATE, HIGH, CRITICAL }

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final Logging logs = new Logging();
    private final DataStore store;

    private String dispatchId;
    private String incidentId;
    private List<String> assignedResponderIds;
    private List<String> allocatedResourceIds;
    private LocalDateTime dispatchTimestamp;
    private DispatchStatus status;
    private DispatchPriority priority;
    private List<String> activityLog;

    public Dispatch(DataStore store,
                    String dispatchId,
                    String incidentId,
                    List<String> responderIds,
                    List<String> resourceIds,
                    DispatchPriority priority) {

        if (store == null) throw new IllegalArgumentException("DataStore required");
        if (dispatchId == null || dispatchId.isBlank())
            throw new IllegalArgumentException("Dispatch ID required");
        if (incidentId == null || incidentId.isBlank())
            throw new IllegalArgumentException("Incident ID required");

        this.store = store;
        this.dispatchId = dispatchId;
        this.incidentId = incidentId;
        this.priority = (priority != null) ? priority : DispatchPriority.MODERATE;

        this.assignedResponderIds = new ArrayList<>();
        this.allocatedResourceIds = new ArrayList<>();
        this.activityLog = new ArrayList<>();
        this.status = DispatchStatus.PENDING;
        this.dispatchTimestamp = LocalDateTime.now();

        if (responderIds != null) this.assignedResponderIds.addAll(responderIds);
        if (resourceIds != null) this.allocatedResourceIds.addAll(resourceIds);

        logs.postUpdate("Dispatch " + dispatchId + " created for incident " + incidentId);
    }

    // 🔥 MAIN EXECUTION
    public boolean dispatchTeamsAndResources() {

        if (status != DispatchStatus.PENDING) {
            logs.postWarning("Already dispatched.");
            return false;
        }

        AbstractDisaster incident =
                (AbstractDisaster) store.findDisasterById(incidentId);

        if (incident == null) {
            logs.postError("Incident not found.");
            return false;
        }

        String location = incident.getLocation();
        List<String> required = incident.getRequiredResponderTypes();
        Set<String> covered = new HashSet<>();

        int successCount = 0;

        for (String id : assignedResponderIds) {

            Responder responder = store.findResponderByName(id);

            if (responder == null) {
                logs.postWarning("Responder not found: " + id);
                continue;
            }

            if (!responder.isAvailable()) {
                logs.postWarning("Responder busy: " + responder.getName());
                continue;
            }

            responder.dispatchTo(location);
            logActivity("Dispatched " + responder.getName() + " → " + location);
            successCount++;

            String type = responder.getResponderCategory().name().replace("_", "");

            for (String req : required) {
                if (req.equalsIgnoreCase(type)) {
                    covered.add(req);
                }
            }
        }

        if (successCount == 0) {
            logs.postError("No responders dispatched.");
            return false;
        }

        // Update disaster status
        if (covered.size() >= required.size()) {
            incident.setStatus(DisasterStatus.RESPONDING);
        } else {
            incident.setStatus(DisasterStatus.PARTIAL_RESPONSE);
        }

        status = DispatchStatus.DISPATCHED;
        logActivity("Dispatch completed.");

        return true;
    }

    // STATUS UPDATE
    public void updateStatus(DispatchStatus newStatus) {

        if (newStatus == null) throw new IllegalArgumentException();

        DispatchStatus prev = status;
        status = newStatus;

        logActivity("Status: " + prev + " → " + newStatus);

        if (newStatus == DispatchStatus.COMPLETED) {
            releaseResponders();
        }
    }

    private void releaseResponders() {
        for (String id : assignedResponderIds) {
            Responder r = store.findResponderByName(id);
            if (r != null) {
                r.completeTask();
                logActivity("Released: " + r.getName());
            }
        }
    }

    private void logActivity(String msg) {
        activityLog.add("[" + LocalDateTime.now().format(DISPLAY_FMT) + "] " + msg);
    }

    // REPORT
    public String generateDispatchSummary() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== DISPATCH REPORT ===\n");
        sb.append("ID: ").append(dispatchId).append("\n");
        sb.append("Incident: ").append(incidentId).append("\n");
        sb.append("Priority: ").append(priority).append("\n");
        sb.append("Status: ").append(status).append("\n\n");

        sb.append("Responders:\n");
        assignedResponderIds.forEach(r -> sb.append(" - ").append(r).append("\n"));

        sb.append("\nActivity:\n");
        activityLog.forEach(a -> sb.append(" ").append(a).append("\n"));

        return sb.toString();
    }
}
package com.ub.project.dispatch;

import com.ub.project.disasters.AbstractDisaster;
import com.ub.project.disasters.DisasterStatus;
import com.ub.project.responders.Responder;
import com.ub.project.store.DataStore;
import com.ub.project.util.Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Coordinates the assignment of responder units and resources to an active
 * disaster incident, and tracks the full lifecycle of that response.
 *
 * @author CSI-142 Group Project Team
 * @version 1.1
 */
public class Dispatch {

    // PENDING → DISPATCHED → IN_PROGRESS → COMPLETED
    //                                     ↘ CANCELLED
    public enum DispatchStatus { PENDING, DISPATCHED, IN_PROGRESS, COMPLETED, CANCELLED }

    public enum DispatchPriority { LOW, MODERATE, HIGH, CRITICAL }

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final Logging logs   = new Logging();
    private static final DataStore store = DataStore.getInstance();

    private String dispatchId;
    private String incidentId;
    private List<String> assignedResponderIds;
    private List<String> allocatedResourceIds;
    private LocalDateTime dispatchTimestamp;
    private DispatchStatus status;
    private DispatchPriority priority;
    private List<String> activityLog;

    /** Creates an empty PENDING dispatch; populate fields via setters before dispatching. */
    public Dispatch() {
        this.assignedResponderIds = new ArrayList<>();
        this.allocatedResourceIds = new ArrayList<>();
        this.activityLog          = new ArrayList<>();
        this.status               = DispatchStatus.PENDING;
        this.priority             = DispatchPriority.MODERATE;
        this.dispatchTimestamp    = LocalDateTime.now();
    }

    /**
     * Preferred constructor — validates IDs immediately and logs creation.
     *
     * @throws IllegalArgumentException if dispatchId or incidentId is null/blank
     */
    public Dispatch(String dispatchId, String incidentId,
                    List<String> responderIds, List<String> resourceIds,
                    DispatchPriority priority) {
        this();

        if (dispatchId == null || dispatchId.isBlank())
            throw new IllegalArgumentException("Dispatch ID must not be null or blank.");
        if (incidentId == null || incidentId.isBlank())
            throw new IllegalArgumentException("Incident ID must not be null or blank.");

        this.dispatchId = dispatchId;
        this.incidentId = incidentId;
        this.priority   = (priority != null) ? priority : DispatchPriority.MODERATE;

        if (responderIds != null) this.assignedResponderIds.addAll(responderIds);
        if (resourceIds  != null) this.allocatedResourceIds.addAll(resourceIds);

        logs.postUpdate("Dispatch " + dispatchId + " created for incident " + incidentId
                + " | Priority: " + this.priority
                + " | Responders: " + this.assignedResponderIds.size()
                + " | Resources: "  + this.allocatedResourceIds.size());
    }

    /**
     * Validates and executes the dispatch: sends each available responder to the
     * incident location, updates the incident status based on type coverage, and
     * persists this record in the DataStore.
     *
     * @return true if the dispatch succeeded, false if validation failed
     */
    public boolean dispatchTeamsAndResources() {

        // Guard: prevent re-dispatching an already active record
        if (status != DispatchStatus.PENDING) {
            logs.postWarning("Dispatch " + dispatchId
                    + " is already " + status + " — cannot re-dispatch.");
            return false;
        }

        if (!isValidDispatch()) {
            logs.postWarning("Dispatch " + dispatchId + " failed validation — aborting.");
            return false;
        }

        AbstractDisaster incident = (AbstractDisaster) store.get(incidentId);
        String location = incident.getLocation();
        List<String> requiredTypes = incident.getRequiredResponderTypes();

        // Track distinct covered types — a Set prevents two Helicopters counting as double coverage
        Set<String> coveredTypes = new HashSet<>();

        for (String responderId : assignedResponderIds) {
            Object raw = store.get(responderId);
            if (!(raw instanceof Responder responder)) {
                logs.postWarning("Responder '" + responderId + "' not found — skipping.");
                continue;
            }
            if (!responder.isAvailable()) {
                logs.postWarning("Responder " + responder.getName() + " unavailable — skipping.");
                continue;
            }

            responder.dispatchTo(location);
            logActivity("Responder " + responder.getName() + " dispatched to " + location);

            // e.g. FIRE_TRUCK → "FIRETRUCK" matched case-insensitively against "FireTruck"
            String catName = responder.getResponderCategory().name().replace("_", "");
            requiredTypes.stream()
                    .filter(t -> t.equalsIgnoreCase(catName))
                    .forEach(coveredTypes::add);
        }

        if (coveredTypes.isEmpty()) {
            logs.postWarning("Dispatch " + dispatchId + ": no required responder types covered.");
        } else if (coveredTypes.size() >= requiredTypes.size()) {
            incident.setStatus(DisasterStatus.RESPONDING);
            logActivity("Incident " + incidentId + " → RESPONDING (full coverage)");
        } else {
            incident.setStatus(DisasterStatus.PARTIAL_RESPONSE);
            logActivity("Incident " + incidentId + " → PARTIAL_RESPONSE ("
                    + coveredTypes.size() + "/" + requiredTypes.size() + " types covered)");
        }

        status = DispatchStatus.DISPATCHED;
        logActivity("Dispatch status set to DISPATCHED");
        store.put(dispatchId, this);
        logs.postUpdate(this, "Dispatch " + dispatchId + " committed to DataStore.");

        return true;
    }

    /**
     * Transitions this dispatch to a new status.
     * COMPLETED and CANCELLED are terminal — no further updates are allowed.
     * Transitioning to COMPLETED automatically releases all responders back to IDLE.
     *
     * @throws IllegalArgumentException if newStatus is null
     * @throws IllegalStateException    if already in a terminal state
     */
    public void updateStatus(DispatchStatus newStatus) {
        if (newStatus == null)
            throw new IllegalArgumentException("New dispatch status must not be null.");
        if (status == DispatchStatus.COMPLETED || status == DispatchStatus.CANCELLED)
            throw new IllegalStateException("Dispatch " + dispatchId
                    + " is already in a terminal state (" + status + ").");

        DispatchStatus previous = this.status;
        this.status = newStatus;
        logActivity("Status changed: " + previous + " → " + newStatus);
        logs.postUpdate(this, "Dispatch " + dispatchId + " | " + previous + " → " + newStatus);

        if (newStatus == DispatchStatus.COMPLETED) releaseResponders();
    }

    /**
     * String overload of {@link #updateStatus(DispatchStatus)} — accepts values
     * like "IN_PROGRESS" or "completed" (case-insensitive), useful for menu input.
     */
    public void updateStatus(String newStatusName) {
        try {
            updateStatus(DispatchStatus.valueOf(newStatusName.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            logs.postError("'" + newStatusName + "' is not a valid DispatchStatus. "
                    + "Valid values: " + Arrays.toString(DispatchStatus.values()));
            throw e;
        }
    }

    /**
     * Checks that this dispatch is safe to execute:
     * IDs are present, at least one responder is assigned, the incident exists
     * and is still active, and all assigned responders are currently IDLE.
     *
     * @return true if all checks pass
     */
    public boolean isValidDispatch() {
        if (dispatchId == null || dispatchId.isBlank()) {
            logs.postError("Validation failed: dispatch ID is missing.");
            return false;
        }
        if (incidentId == null || incidentId.isBlank()) {
            logs.postError("Validation failed [" + dispatchId + "]: incident ID is missing.");
            return false;
        }
        if (assignedResponderIds.isEmpty()) {
            logs.postError("Validation failed [" + dispatchId + "]: no responders assigned.");
            return false;
        }

        Object raw = store.get(incidentId);
        if (!(raw instanceof AbstractDisaster incident)) {
            logs.postError("Validation failed [" + dispatchId + "]: incident '"
                    + incidentId + "' not found in DataStore.");
            return false;
        }

        DisasterStatus ds = incident.getStatus();
        if (ds == DisasterStatus.RESOLVED || ds == DisasterStatus.CANCELLED) {
            logs.postError("Validation failed [" + dispatchId + "]: incident '"
                    + incidentId + "' is already " + ds + ".");
            return false;
        }

        boolean allAvailable = true;
        for (String id : assignedResponderIds) {
            Object respRaw = store.get(id);
            if (!(respRaw instanceof Responder responder)) {
                logs.postWarning("Validation [" + dispatchId + "]: responder '"
                        + id + "' not found in DataStore.");
                allAvailable = false;
            } else if (!responder.isAvailable()) {
                logs.postWarning("Validation [" + dispatchId + "]: responder '"
                        + responder.getName() + "' is not IDLE (status: "
                        + responder.getStatus() + ").");
                allAvailable = false;
            }
        }

        if (!allAvailable) {
            logs.postError("Validation failed [" + dispatchId
                    + "]: one or more responders are unavailable.");
            return false;
        }

        logs.postUpdate("Dispatch " + dispatchId + " passed validation.");
        return true;
    }

    /** @return a formatted multi-line summary of this dispatch for console output */
    public String generateDispatchSummary() {
        String divider = "=".repeat(60);
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(divider).append("\n");
        sb.append("          DISPATCH SUMMARY REPORT\n");
        sb.append(divider).append("\n");
        sb.append(String.format("  Dispatch ID   : %s%n", dispatchId));
        sb.append(String.format("  Incident ID   : %s%n", incidentId));
        sb.append(String.format("  Priority      : %s%n", priority));
        sb.append(String.format("  Status        : %s%n", status));
        sb.append(String.format("  Timestamp     : %s%n", dispatchTimestamp.format(DISPLAY_FMT)));

        sb.append("\n  Assigned Responders (").append(assignedResponderIds.size()).append("):\n");
        if (assignedResponderIds.isEmpty()) sb.append("    [none]\n");
        else assignedResponderIds.forEach(id -> sb.append("    • ").append(id).append("\n"));

        sb.append("\n  Allocated Resources (").append(allocatedResourceIds.size()).append("):\n");
        if (allocatedResourceIds.isEmpty()) sb.append("    [none]\n");
        else allocatedResourceIds.forEach(id -> sb.append("    • ").append(id).append("\n"));

        sb.append("\n  Activity Log:\n");
        if (activityLog.isEmpty()) sb.append("    [no entries]\n");
        else activityLog.forEach(e -> sb.append("    ").append(e).append("\n"));

        sb.append(divider).append("\n");
        return sb.toString();
    }

    /** Adds a responder ID to this dispatch. Only allowed while PENDING. */
    public void addResponder(String responderId) {
        requirePendingState("addResponder");
        if (responderId != null && !responderId.isBlank()
                && !assignedResponderIds.contains(responderId)) {
            assignedResponderIds.add(responderId);
            logActivity("Responder added: " + responderId);
        }
    }

    /** Removes a responder ID from this dispatch. Only allowed while PENDING. */
    public void removeResponder(String responderId) {
        requirePendingState("removeResponder");
        if (assignedResponderIds.remove(responderId))
            logActivity("Responder removed: " + responderId);
    }

    /** Adds a resource ID to this dispatch. Only allowed while PENDING. */
    public void addResource(String resourceId) {
        requirePendingState("addResource");
        if (resourceId != null && !resourceId.isBlank()
                && !allocatedResourceIds.contains(resourceId)) {
            allocatedResourceIds.add(resourceId);
            logActivity("Resource added: " + resourceId);
        }
    }

    private void releaseResponders() {
        for (String id : assignedResponderIds) {
            Object raw = store.get(id);
            if (raw instanceof Responder responder) {
                responder.completeTask();
                logActivity("Responder " + responder.getName() + " released → IDLE");
            }
        }
    }

    private void logActivity(String message) {
        activityLog.add("[" + LocalDateTime.now().format(DISPLAY_FMT) + "] " + message);
    }

    private void requirePendingState(String methodName) {
        if (status != DispatchStatus.PENDING)
            throw new IllegalStateException(methodName
                    + "() requires PENDING status. Current: " + status);
    }

    // Getters & Setters

    public String getDispatchId() { return dispatchId; }

    public void setDispatchId(String dispatchId) {
        if (dispatchId == null || dispatchId.isBlank())
            throw new IllegalArgumentException("Dispatch ID must not be null or blank.");
        this.dispatchId = dispatchId;
    }

    public String getIncidentId()                { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }

    public LocalDateTime getDispatchTimestamp()  { return dispatchTimestamp; }
    public DispatchStatus getStatus()            { return status; }
    public DispatchPriority getPriority()        { return priority; }

    public void setPriority(DispatchPriority priority) {
        if (priority != null) this.priority = priority;
    }

    // Unmodifiable views prevent bypassing addResponder / addResource guards
    public List<String> getAssignedResponderIds() {
        return Collections.unmodifiableList(assignedResponderIds);
    }
    public List<String> getAllocatedResourceIds() {
        return Collections.unmodifiableList(allocatedResourceIds);
    }
    public List<String> getActivityLog() {
        return Collections.unmodifiableList(activityLog);
    }

    @Override
    public String toString() {
        return "DISPATCH [" + dispatchId + "]"
                + " | Incident: "   + incidentId
                + " | Priority: "   + priority
                + " | Status: "     + status
                + " | Responders: " + assignedResponderIds.size()
                + " | Resources: "  + allocatedResourceIds.size()
                + " | Created: "    + dispatchTimestamp.format(DISPLAY_FMT);
    }
}
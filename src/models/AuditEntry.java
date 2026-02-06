package models;

import java.time.LocalDateTime;

public class AuditEntry {
    private int id;
    private String action;
    private String tableName;
    private Integer recordId;
    private String user;
    private String details;
    private LocalDateTime createdAt;

    public AuditEntry(int id, String action, String tableName, Integer recordId, String user, String details, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.user = user;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getAction() { return action; }
    public String getTableName() { return tableName; }
    public Integer getRecordId() { return recordId; }
    public String getUser() { return user; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

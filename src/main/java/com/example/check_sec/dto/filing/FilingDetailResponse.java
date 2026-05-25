package com.example.check_sec.dto.filing;

import java.util.List;

public class FilingDetailResponse {

    private FilingResponse filing;
    private List<OperationLogResponse> logs;

    public FilingResponse getFiling() { return filing; }
    public void setFiling(FilingResponse filing) { this.filing = filing; }
    public List<OperationLogResponse> getLogs() { return logs; }
    public void setLogs(List<OperationLogResponse> logs) { this.logs = logs; }
}

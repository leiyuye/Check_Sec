package com.example.check_sec.dto.dashboard;

public class DashboardStatsResponse {

    private long totalUsers;
    private long filingUnitCount;
    private long testingAgencyCount;
    private long pendingReviewCount;
    private long approvedCount;
    private long returnedCount;
    private long myTotalFilings;
    private long myPendingCount;
    private long myReturnedCount;
    private long myApprovedCount;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getFilingUnitCount() { return filingUnitCount; }
    public void setFilingUnitCount(long filingUnitCount) { this.filingUnitCount = filingUnitCount; }
    public long getTestingAgencyCount() { return testingAgencyCount; }
    public void setTestingAgencyCount(long testingAgencyCount) { this.testingAgencyCount = testingAgencyCount; }
    public long getPendingReviewCount() { return pendingReviewCount; }
    public void setPendingReviewCount(long pendingReviewCount) { this.pendingReviewCount = pendingReviewCount; }
    public long getApprovedCount() { return approvedCount; }
    public void setApprovedCount(long approvedCount) { this.approvedCount = approvedCount; }
    public long getReturnedCount() { return returnedCount; }
    public void setReturnedCount(long returnedCount) { this.returnedCount = returnedCount; }
    public long getMyTotalFilings() { return myTotalFilings; }
    public void setMyTotalFilings(long myTotalFilings) { this.myTotalFilings = myTotalFilings; }
    public long getMyPendingCount() { return myPendingCount; }
    public void setMyPendingCount(long myPendingCount) { this.myPendingCount = myPendingCount; }
    public long getMyReturnedCount() { return myReturnedCount; }
    public void setMyReturnedCount(long myReturnedCount) { this.myReturnedCount = myReturnedCount; }
    public long getMyApprovedCount() { return myApprovedCount; }
    public void setMyApprovedCount(long myApprovedCount) { this.myApprovedCount = myApprovedCount; }
}

package com.example.check_sec.dto.filing;

public class ReviewRequest {

    private String reviewComment;
    private Long adminCommentFileId;
    private boolean returnForModify = true;

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public Long getAdminCommentFileId() { return adminCommentFileId; }
    public void setAdminCommentFileId(Long adminCommentFileId) { this.adminCommentFileId = adminCommentFileId; }
    public boolean isReturnForModify() { return returnForModify; }
    public void setReturnForModify(boolean returnForModify) { this.returnForModify = returnForModify; }
}

package com.example.check_sec.dto.filing;

import com.example.check_sec.common.enums.FilingLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FilingRequest {

    private String filingUnitName;
    private String filingUnitCreditCode;
    private String filingUnitContact;
    private String filingUnitPhone;
    private String filingUnitAddress;

    @NotBlank(message = "系统名称不能为空")
    private String systemName;

    private String systemDescription;

    @NotNull(message = "备案等级不能为空")
    private FilingLevel filingLevel;

    private String industry;
    private String systemOwner;
    private String contactPhone;
    private Long materialFileId;

    public String getFilingUnitName() { return filingUnitName; }
    public void setFilingUnitName(String filingUnitName) { this.filingUnitName = filingUnitName; }
    public String getFilingUnitCreditCode() { return filingUnitCreditCode; }
    public void setFilingUnitCreditCode(String filingUnitCreditCode) { this.filingUnitCreditCode = filingUnitCreditCode; }
    public String getFilingUnitContact() { return filingUnitContact; }
    public void setFilingUnitContact(String filingUnitContact) { this.filingUnitContact = filingUnitContact; }
    public String getFilingUnitPhone() { return filingUnitPhone; }
    public void setFilingUnitPhone(String filingUnitPhone) { this.filingUnitPhone = filingUnitPhone; }
    public String getFilingUnitAddress() { return filingUnitAddress; }
    public void setFilingUnitAddress(String filingUnitAddress) { this.filingUnitAddress = filingUnitAddress; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getSystemDescription() { return systemDescription; }
    public void setSystemDescription(String systemDescription) { this.systemDescription = systemDescription; }
    public FilingLevel getFilingLevel() { return filingLevel; }
    public void setFilingLevel(FilingLevel filingLevel) { this.filingLevel = filingLevel; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getSystemOwner() { return systemOwner; }
    public void setSystemOwner(String systemOwner) { this.systemOwner = systemOwner; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Long getMaterialFileId() { return materialFileId; }
    public void setMaterialFileId(Long materialFileId) { this.materialFileId = materialFileId; }
}

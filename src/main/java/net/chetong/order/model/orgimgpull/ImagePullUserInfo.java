package net.chetong.order.model.orgimgpull;

import java.util.Date;

public class ImagePullUserInfo {
    private Long id;
    private String appId;
    private String appName;
    private String secretId;
    private String secretKey;
    private String lastTime;
    private String orgAreaNo;
    private String orgAreaName;
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName == null ? null : appName.trim();
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey == null ? null : secretKey.trim();
    }

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSecretId() {
		return secretId;
	}

	public String getOrgAreaNo() {
		return orgAreaNo;
	}

	public void setOrgAreaNo(String orgAreaNo) {
		this.orgAreaNo = orgAreaNo;
	}

	public String getOrgAreaName() {
		return orgAreaName;
	}

	public void setOrgAreaName(String orgAreaName) {
		this.orgAreaName = orgAreaName;
	}
}
package com.geo.miniooss.domain.vo;


import io.minio.messages.Item;
import io.minio.messages.Owner;
import org.simpleframework.xml.Element;

import java.time.ZonedDateTime;
import java.util.Map;

public class ItemVo {

    @Element(name = "ETag", required = false)
    public String etag; // except DeleteMarker

    @Element(name = "Key")
    public String objectName;

    @Element(name = "LastModified")
    public ZonedDateTime lastModified;

    @Element(name = "Owner", required = false)
    public Owner owner;

    @Element(name = "Size", required = false)
    public long size; // except DeleteMarker

    @Element(name = "StorageClass", required = false)
    public String storageClass; // except DeleteMarker, not in case of MinIO server.

    @Element(name = "IsLatest", required = false)
    public boolean isLatest; // except ListObjects V1

    @Element(name = "VersionId", required = false)
    public String versionId; // except ListObjects V1

    @Element(name = "UserMetadata", required = false)
    public Map userMetadata;

    public boolean isDir = false;
    public String encodingType = null;

    public ItemVo(Item item){
        this.etag = item.etag();
        this.objectName = item.objectName();
        this.lastModified = item.lastModified();
        this.owner = item.owner();
        this.size = item.size();
        this.storageClass = item.storageClass();
        this.isLatest = item.isLatest();
        this.versionId = item.versionId();
        this.userMetadata = item.userMetadata();
        this.isDir = item.isDir();
    }
}
